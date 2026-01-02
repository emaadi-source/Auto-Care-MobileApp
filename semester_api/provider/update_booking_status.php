<?php
include '../connect.php';
header('Content-Type: application/json');

$booking_id = isset($_POST['booking_id']) ? intval($_POST['booking_id']) : 0;
$status = isset($_POST['status']) ? $_POST['status'] : ''; // 'confirmed' or 'cancelled'

if ($booking_id == 0 || empty($status)) {
    echo json_encode(['error' => true, 'message' => 'Missing data']);
    exit;
}

$stmt = $con->prepare("UPDATE bookings SET status = ? WHERE id = ?");
$stmt->bind_param("si", $status, $booking_id);

if ($stmt->execute()) {
    echo json_encode(['error' => false, 'message' => 'Booking updated']);
} else {
    echo json_encode(['error' => true, 'message' => 'Database error']);
}
?>