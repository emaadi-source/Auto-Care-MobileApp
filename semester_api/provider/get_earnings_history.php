<?php
include '../connect.php';
header('Content-Type: application/json');

$provider_id = isset($_POST['provider_id']) ? intval($_POST['provider_id']) : 0;

if ($provider_id == 0) {
    echo json_encode(['error' => true, 'message' => 'Invalid ID']);
    exit;
}

$response = [
    "total_earnings" => 0.00,
    "monthly_earnings" => 0.00,
    "history" => []
];

// 1. Calculate Total Earnings (All Time)
$stmt = $con->prepare("SELECT SUM(total_amount) as total FROM bookings WHERE provider_id = ? AND status = 'completed'");
$stmt->bind_param("i", $provider_id);
$stmt->execute();
$res = $stmt->get_result()->fetch_assoc();
$response['total_earnings'] = $res['total'] ? (float)$res['total'] : 0.00;
$stmt->close();

// 2. Calculate Monthly Earnings
$currentMonth = date('m');
$currentYear = date('Y');
$stmt = $con->prepare("SELECT SUM(total_amount) as total FROM bookings WHERE provider_id = ? AND status = 'completed' AND MONTH(booking_date) = ? AND YEAR(booking_date) = ?");
$stmt->bind_param("iss", $provider_id, $currentMonth, $currentYear);
$stmt->execute();
$res = $stmt->get_result()->fetch_assoc();
$response['monthly_earnings'] = $res['total'] ? (float)$res['total'] : 0.00;
$stmt->close();

// 3. Get Transaction History
$query = "
    SELECT 
        b.id,
        b.booking_date,
        b.total_amount,
        u.full_name as customer_name,
        (SELECT s.name FROM booking_items bi 
         JOIN provider_services s ON bi.service_id = s.id 
         WHERE bi.booking_id = b.id LIMIT 1) as service_name
    FROM bookings b
    JOIN users u ON b.customer_id = u.id
    WHERE b.provider_id = ? AND b.status = 'completed'
    ORDER BY b.booking_date DESC
";

$stmt = $con->prepare($query);
$stmt->bind_param("i", $provider_id);
$stmt->execute();
$result = $stmt->get_result();

while ($row = $result->fetch_assoc()) {
    $response['history'][] = [
        "id" => $row['id'],
        "date" => $row['booking_date'],
        "amount" => $row['total_amount'],
        "customer" => $row['customer_name'],
        "service" => $row['service_name'] ?: "Service"
    ];
}

echo json_encode($response);
?>