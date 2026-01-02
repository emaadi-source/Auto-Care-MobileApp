<?php
header('Content-Type: application/json');
include 'connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success'=>false,'message'=>'Invalid request method']);
    exit;
}

$message_id = isset($_POST['message_id']) ? intval($_POST['message_id']) : 0;
$new_text = isset($_POST['new_text']) ? trim($_POST['new_text']) : '';

if ($message_id === 0 || empty($new_text)) {
    echo json_encode(['success'=>false,'message'=>'Missing message ID or new text']);
    exit;
}

$stmt = $conn->prepare("UPDATE chat_messages SET message_content=?, is_edited=1 WHERE message_id=?");
$stmt->bind_param("si",$new_text,$message_id);

if ($stmt->execute()) {
    echo json_encode(['success'=>true,'message'=>'Message edited successfully','message_id'=>$message_id,'new_text'=>$new_text]);
} else {
    echo json_encode(['success'=>false,'message'=>'DB update failed: '.$stmt->error]);
}

$stmt->close();
$conn->close();
?>
