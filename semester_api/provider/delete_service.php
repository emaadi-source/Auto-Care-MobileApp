<?php
include '../connect.php';
header('Content-Type: application/json');

$service_id = isset($_POST['service_id']) ? intval($_POST['service_id']) : 0;

if ($service_id == 0) {
    echo json_encode(['error' => true, 'message' => 'Invalid ID']);
    exit;
}

$stmt = $con->prepare("DELETE FROM provider_services WHERE id = ?");
$stmt->bind_param("i", $service_id);

if ($stmt->execute()) {
    echo json_encode(['error' => false, 'message' => 'Service deleted']);
} else {
    echo json_encode(['error' => true, 'message' => 'Failed to delete']);
}
?>