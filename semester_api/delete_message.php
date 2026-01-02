<?php
header('Content-Type: application/json');
include 'connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success'=>false,'message'=>'Invalid request method']);
    exit;
}

$message_id = isset($_POST['message_id']) ? intval($_POST['message_id']) : 0;

if ($message_id === 0) {
    echo json_encode(['success'=>false,'message'=>'Missing message ID']);
    exit;
}

$stmt = $con->prepare("UPDATE chat_messages SET is_deleted=1 WHERE message_id=?");
$stmt->bind_param("i",$message_id);

if ($stmt->execute()) {
    echo json_encode(['success'=>true,'message'=>'Message deleted successfully','message_id'=>$message_id]);
} else {
    echo json_encode(['success'=>false,'message'=>'DB update failed: '.$stmt->error]);
}

$stmt->close();
$con->close();
?>
