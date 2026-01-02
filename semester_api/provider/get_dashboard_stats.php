<?php
include '../connect.php';
header('Content-Type: application/json');

$provider_id = isset($_POST['provider_id']) ? intval($_POST['provider_id']) : 0;

if ($provider_id == 0) {
    echo json_encode(['error' => true, 'message' => 'Invalid Provider ID']);
    exit;
}

// Initialize default values
$response = [
    "error" => false,
    "total_bookings" => 0,
    "earnings" => 0.00,
    "pending_today" => 0,
    "completed_today" => 0
];

// 1. Total Bookings (All time)
$stmt = $con->prepare("SELECT COUNT(*) as count FROM bookings WHERE provider_id = ?");
$stmt->bind_param("i", $provider_id);
$stmt->execute();
$res = $stmt->get_result()->fetch_assoc();
$response['total_bookings'] = $res['count'];
$stmt->close();

// 2. Earnings (Only 'completed' bookings) - Using correct 'total_amount' column
$stmt = $con->prepare("SELECT SUM(total_amount) as total FROM bookings WHERE provider_id = ? AND status = 'completed'");
$stmt->bind_param("i", $provider_id);
$stmt->execute();
$res = $stmt->get_result()->fetch_assoc();
$response['earnings'] = $res['total'] ? $res['total'] : 0.00; // Handle null if no earnings
$stmt->close();

// 3. Pending (All time or Today? Let's do All Time Pending for better visibility)
$stmt = $con->prepare("SELECT COUNT(*) as count FROM bookings WHERE provider_id = ? AND status = 'pending'");
$stmt->bind_param("i", $provider_id);
$stmt->execute();
$res = $stmt->get_result()->fetch_assoc();
$response['pending_today'] = $res['count'];
$stmt->close();

// 4. Completed (All time)
$stmt = $con->prepare("SELECT COUNT(*) as count FROM bookings WHERE provider_id = ? AND status = 'completed'");
$stmt->bind_param("i", $provider_id);
$stmt->execute();
$res = $stmt->get_result()->fetch_assoc();
$response['completed_today'] = $res['count'];
$stmt->close();

echo json_encode($response);
?>