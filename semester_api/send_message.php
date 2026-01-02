<?php
header('Content-Type: application/json');

// Make sure your connect.php defines $con (or change variable here)
include 'connect.php';

$response = ['success' => false, 'message' => 'Unknown error'];

// Ensure POST request
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Invalid request method']);
    exit;
}

// Get POST parameters
$sender_id = isset($_POST['sender_id']) ? intval($_POST['sender_id']) : 0;
$receiver_id = isset($_POST['receiver_id']) ? intval($_POST['receiver_id']) : 0;
$text = isset($_POST['message_content']) ? trim($_POST['message_content']) : '';
$base64_image = isset($_POST['image_path']) ? $_POST['image_path'] : '';

// Validate required fields
if ($sender_id === 0 || $receiver_id === 0 || (empty($text) && empty($base64_image))) {
    echo json_encode(['success' => false, 'message' => 'Missing data']);
    exit;
}

// Handle optional image
$image_path = NULL;

if (!empty($base64_image)) {
    $image_data = base64_decode($base64_image);
    if ($image_data === false) {
        echo json_encode(['success' => false, 'message' => 'Image decoding failed']);
        exit;
    }

    $upload_dir = "uploads/";
    if (!is_dir($upload_dir)) {
        if (!mkdir($upload_dir, 0777, true)) {
            echo json_encode(['success' => false, 'message' => 'Failed to create upload directory']);
            exit;
        }
    }

    $file_name = "chat_{$sender_id}_" . time() . ".jpg";
    $file_path = $upload_dir . $file_name;

    if (file_put_contents($file_path, $image_data)) {
        $image_path = $file_path;
    } else {
        echo json_encode(['success' => false, 'message' => 'Failed to save image']);
        exit;
    }
}

// Generate timestamp in milliseconds
$timestamp = round(microtime(true) * 1000);

// Prepare SQL safely
$stmt = $con->prepare("INSERT INTO chat_messages (sender_id, receiver_id, message_content, image_path, timestamp, vanish_mode) VALUES (?, ?, ?, ?, ?, ?)");

if (!$stmt) {
    echo json_encode(['success' => false, 'message' => 'Prepare failed: '.$con->error]);
    exit;
}

// Bind parameters, NULL image handled correctly
$stmt->bind_param(
    "iissii",
    $sender_id,
    $receiver_id,
    $text,
    $image_path,
    $timestamp,
    $vanish_mode
);

// Execute query
if ($stmt->execute()) {
    echo json_encode([
        'success' => true,
        'message' => 'Message sent',
        'message_id' => $stmt->insert_id,
        'timestamp' => $timestamp,
        'image_path' => $image_path,
        'vanish_mode' => $vanish_mode
    ]);
} else {
    echo json_encode(['success' => false, 'message' => 'DB insert failed: '.$stmt->error]);
}

// Close resources
$stmt->close();
$con->close();
?>
