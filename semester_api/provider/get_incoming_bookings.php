<?php
// 1. Start Output Buffering (Traps any accidental text/warnings)
ob_start();

include '../connect.php';

// 2. Clear the buffer (Delete any text like "Connected successfully" or Warnings)
ob_clean();

header('Content-Type: application/json');

// Error handling wrapper
try {
    $provider_id = isset($_POST['provider_id']) ? intval($_POST['provider_id']) : 0;
    $status_filter = isset($_POST['status']) ? $_POST['status'] : 'pending';

    if ($provider_id == 0) { 
        echo json_encode([]); 
        exit; 
    }

    // 3. The Query
    $query = "
        SELECT 
            b.id,
            b.customer_id,
            b.booking_date,
            b.total_amount as total,  -- FIX: Changed 'total' to 'total_amount as total'
            b.status,
            b.vehicle_model,
            b.vehicle_number,
            b.problem_description,
            u.full_name as customer_name,
            u.phone as customer_phone,
            u.email as customer_email,
            (SELECT s.name FROM booking_items bi 
             JOIN provider_services s ON bi.service_id = s.id 
             WHERE bi.booking_id = b.id LIMIT 1) as main_service
        FROM bookings b
        JOIN users u ON b.customer_id = u.id
        WHERE b.provider_id = ? AND b.status = ?
        ORDER BY b.booking_date ASC
    ";

    $stmt = $con->prepare($query);
    
    if (!$stmt) {
        throw new Exception("SQL Prepare Failed: " . $con->error);
    }

    $stmt->bind_param("is", $provider_id, $status_filter);
    
    if (!$stmt->execute()) {
        throw new Exception("SQL Execute Failed: " . $stmt->error);
    }

    $result = $stmt->get_result();
    $data = array();

    while ($row = $result->fetch_assoc()) {
        // Ensure main_service is not null
        if ($row['main_service'] == null) {
            $row['main_service'] = "General Service";
        }
        $data[] = $row;
    }

    // 4. Output Final JSON
    echo json_encode($data);

} catch (Exception $e) {
    // Log error to a file on server so you can debug if it happens again
    error_log("API Error: " . $e->getMessage()); 
    echo json_encode([]); 
}
?>