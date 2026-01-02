<?php
header('Content-Type: application/json');
require 'connect.php'; // your MySQL connection

$user_id = $_POST['user_id'] ?? '';
$full_name = $_POST['full_name'] ?? '';
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? ''; // optional

if(empty($user_id) || empty($full_name) || empty($email)) {
    echo json_encode(['success' => false, 'message' => 'User ID, name, and email are required']);
    exit;
}

try {
    if(!empty($password)) {
        $hashedPassword = password_hash($password, PASSWORD_BCRYPT);
        $stmt = $con->prepare("UPDATE users SET full_name = ?, email = ?, password = ? WHERE id = ?");
        $stmt->bind_param("sssi", $full_name, $email, $hashedPassword, $user_id);
    } else {
        $stmt = $con->prepare("UPDATE users SET full_name = ?, email = ? WHERE id = ?");
        $stmt->bind_param("ssi", $full_name, $email, $user_id);
    }

    if($stmt->execute()) {
        echo json_encode(['success' => true, 'message' => 'Profile updated successfully']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Update failed']);
    }

    $stmt->close();
    $con->close();
} catch(Exception $e) {
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
}
?>
