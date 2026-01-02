<?php
include '../connect.php';
header('Content-Type: application/json');

$service_id = isset($_POST['service_id']) ? intval($_POST['service_id']) : 0;
$status = isset($_POST['status']) ? intval($_POST['status']) : 1;

if ($service_id == 0) {
    echo json_encode(['error' => true, 'message' => 'Invalid ID']);
    exit;
}

$stmt = $con->prepare("UPDATE provider_services SET is_active = ? WHERE id = ?");
$stmt->bind_param("ii", $status, $service_id);

if ($stmt->execute()) {
    echo json_encode(['error' => false, 'message' => 'Status updated']);
} else {
    echo json_encode(['error' => true, 'message' => 'Failed to update']);
}
?>