<?php
// 1. Clean Output Buffer to prevent JSON errors
ob_start();
include '../connect.php';
ob_clean();

header('Content-Type: application/json');

// Error handling wrapper
try {
    $provider_id = isset($_POST['provider_id']) ? intval($_POST['provider_id']) : 0;

    if ($provider_id == 0) {
        echo json_encode([]);
        exit;
    }

    // 2. The Query (Fixed 'total' -> 'total_amount')
    $query = "
        SELECT 
            b.id,
            u.full_name as customer_name,
            b.booking_date,
            b.total_amount as total,  -- FIX: Changed 'total' to 'total_amount'
            b.status,
            -- Subquery to get the first service name
            (SELECT s.name FROM booking_items bi 
             JOIN provider_services s ON bi.service_id = s.id 
             WHERE bi.booking_id = b.id LIMIT 1) as service_name
        FROM bookings b
        JOIN users u ON b.customer_id = u.id
        WHERE b.provider_id = ?
        ORDER BY b.created_at DESC
        LIMIT 10 -- Only show recent 10
    ";

    $stmt = $con->prepare($query);
    
    if (!$stmt) {
        throw new Exception("SQL Prepare Failed: " . $con->error);
    }

    $stmt->bind_param("i", $provider_id);
    
    if (!$stmt->execute()) {
        throw new Exception("SQL Execute Failed: " . $stmt->error);
    }

    $result = $stmt->get_result();
    $bookings = array();

    while ($row = $result->fetch_assoc()) {
        // Fallback if service name is null
        $service_display = $row['service_name'] ? $row['service_name'] : "General Service";
        
        $bookings[] = [
            "id" => $row['id'],
            "name" => $row['customer_name'],
            "service" => $service_display . " • " . $row['booking_date'],
            "price" => "Rs " . $row['total'],
            "status" => $row['status']
        ];
    }

    echo json_encode($bookings);

} catch (Exception $e) {
    error_log("Dashboard List Error: " . $e->getMessage());
    echo json_encode([]); // Return empty list on error
}
?>