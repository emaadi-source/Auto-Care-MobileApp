<?php
// Enable error reporting for debugging logs, but hide from output
error_reporting(E_ALL);
ini_set('display_errors', 0); 

header('Content-Type: application/json; charset=utf-8');
include 'connect.php'; 

function resp($ok, $msg) {
    echo json_encode(['success' => $ok, 'message' => $msg]);
    exit;
}

// 1. Read Input
$customer_id = isset($_POST['customer_id']) ? intval($_POST['customer_id']) : 0;
$provider_id = isset($_POST['provider_id']) ? intval($_POST['provider_id']) : 0;
$date_raw     = isset($_POST['date']) ? trim($_POST['date']) : '';
// We ignore $_POST['total'] because we recalculate it below for security
$services_json = isset($_POST['services']) ? $_POST['services'] : '';
$vehicle_model = isset($_POST['vehicle_model']) ? trim($_POST['vehicle_model']) : 'Unknown';
$vehicle_number = isset($_POST['vehicle_number']) ? trim($_POST['vehicle_number']) : 'N/A';

// 2. Validate
if ($customer_id <= 0 || $provider_id <= 0 || empty($date_raw) || empty($services_json)) {
    resp(false, 'Missing required fields');
}

// 3. Handle Date
$date_db = $date_raw; 
if (!strtotime($date_db)) {
    resp(false, "Invalid date format: $date_raw");
}

// 4. Decode Services
$services = json_decode($services_json, true);
if (!is_array($services) || count($services) == 0) {
    resp(false, 'Invalid services list');
}

mysqli_begin_transaction($con);

try {
    // A. Insert Booking
    // We set total_amount to 0.00 initially. We will update it later.
    $stmt = mysqli_prepare($con,
        "INSERT INTO bookings (customer_id, provider_id, booking_date, total_amount, vehicle_model, vehicle_number, status)
         VALUES (?, ?, ?, 0.00, ?, ?, 'pending')"
    );
    
    if (!$stmt) throw new Exception("Prepare booking failed: " . mysqli_error($con));

    // --- FIX WAS HERE ---
    // Previous: "iiss" (4 types) for 5 variables. 
    // Fixed: "iisss" (5 types: int, int, string, string, string)
    mysqli_stmt_bind_param($stmt, "iisss", $customer_id, $provider_id, $date_db, $vehicle_model, $vehicle_number);

    if (!mysqli_stmt_execute($stmt)) {
        throw new Exception("Execute booking failed: " . mysqli_stmt_error($stmt));
    }

    $booking_id = mysqli_insert_id($con);
    mysqli_stmt_close($stmt);

    // B. Process Items and Calculate REAL Total
    $calculated_total = 0.0;

    $stmt_get_price = mysqli_prepare($con, "SELECT price FROM provider_services WHERE id = ?");
    $stmt_insert_item = mysqli_prepare($con, 
        "INSERT INTO booking_items (booking_id, service_id, price) VALUES (?, ?, ?)"
    );

    foreach ($services as $s) {
        if (!isset($s['id'])) throw new Exception("Service ID missing");
        
        $service_id = intval($s['id']);

        // 1. Fetch REAL price
        mysqli_stmt_bind_param($stmt_get_price, "i", $service_id);
        mysqli_stmt_execute($stmt_get_price);
        $res_price = mysqli_stmt_get_result($stmt_get_price);
        
        if ($row_price = mysqli_fetch_assoc($res_price)) {
            $real_price = floatval($row_price['price']);
        } else {
            throw new Exception("Service ID $service_id not found in database");
        }

        // 2. Add to total
        $calculated_total += $real_price;

        // 3. Insert item
        mysqli_stmt_bind_param($stmt_insert_item, "iid", $booking_id, $service_id, $real_price);
        if (!mysqli_stmt_execute($stmt_insert_item)) {
            throw new Exception("Item insert failed: " . mysqli_stmt_error($stmt_insert_item));
        }
    }
    
    mysqli_stmt_close($stmt_get_price);
    mysqli_stmt_close($stmt_insert_item);

    // C. Update Booking with Correct Total
    $stmt_update = mysqli_prepare($con, "UPDATE bookings SET total_amount = ? WHERE id = ?");
    mysqli_stmt_bind_param($stmt_update, "di", $calculated_total, $booking_id);
    mysqli_stmt_execute($stmt_update);
    mysqli_stmt_close($stmt_update);

    // D. Commit
    mysqli_commit($con);
    resp(true, "Booking created successfully");

} catch (Exception $e) {
    mysqli_rollback($con);
    // Log the actual error to your server's error log
    error_log($e->getMessage());
    resp(false, "Server Error: " . $e->getMessage());
}

mysqli_close($con);
?>