<?php
header('Content-Type: application/json');
include 'connect.php';

$sender_id = isset($_GET['sender_id']) ? intval($_GET['sender_id']) : 0;
$receiver_id = isset($_GET['receiver_id']) ? intval($_GET['receiver_id']) : 0;

if ($sender_id === 0 || $receiver_id === 0) {
    echo json_encode([]); // Return empty array instead of error object
    exit;
}

try {
    $stmt = $con->prepare("
        SELECT 
            message_id,
            sender_id,
            receiver_id,
            message_content,
            image_path,
            timestamp,
            is_edited,
            is_deleted,
            vanish_mode
        FROM chat_messages
        WHERE ((sender_id=? AND receiver_id=?) OR (sender_id=? AND receiver_id=?))
        AND is_deleted=0
        ORDER BY timestamp ASC
    ");

    $stmt->bind_param("iiii",$sender_id,$receiver_id,$receiver_id,$sender_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $messages = [];
    while ($row = $result->fetch_assoc()) {
        $messages[] = [
            'message_id' => (string)$row['message_id'],
            'sender_id' => (string)$row['sender_id'],
            'receiver_id' => (string)$row['receiver_id'],
            'message_content' => $row['message_content'],
            'image_path' => $row['image_path'],
            'timestamp' => (int)$row['timestamp'],
            'is_edited' => (bool)$row['is_edited'],
            'is_deleted' => (bool)$row['is_deleted'],
            'vanish_mode' => (bool)$row['vanish_mode']
        ];
    }

    // Return array directly, not wrapped in object
    echo json_encode($messages);
    $stmt->close();
} catch (Exception $e) {
    echo json_encode([]); // Return empty array on error
}

$con->close();
?>