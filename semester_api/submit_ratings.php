<?php
header('Content-Type: application/json');
require 'connect.php'; // your DB connection

$customer_id = $_POST['customer_id'] ?? '';
$workshop_id = $_POST['workshop_id'] ?? '';
$rating = $_POST['rating'] ?? '';
$comment = $_POST['comment'] ?? '';

if (empty($customer_id) || empty($workshop_id) || empty($rating)) {
    echo json_encode(['success' => false, 'message' => 'All required fields must be filled']);
    exit;
}

// Insert into ratings table
$stmt = $con->prepare("INSERT INTO workshop_ratings (customer_id, workshop_id, rating, comment) VALUES (?, ?, ?, ?)");
$stmt->bind_param("iiis", $customer_id, $workshop_id, $rating, $comment);

if ($stmt->execute()) {
    echo json_encode(['success' => true, 'message' => 'Rating submitted successfully']);
} else {
    echo json_encode(['success' => false, 'message' => 'Failed to submit rating']);
}

$stmt->close();
$con->close();
?>
