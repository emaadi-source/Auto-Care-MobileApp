<?php
header('Content-Type: application/json');
include 'connect.php'; // your DB connection

// Enable error logging for debugging
error_log("=== CALL API REQUEST ===");
error_log("POST: " . print_r($_POST, true));
error_log("GET: " . print_r($_GET, true));
error_log("REQUEST: " . print_r($_REQUEST, true));

$response = ['success' => false, 'message' => 'Unknown error'];

// Get POST or GET data
$action = isset($_REQUEST['action']) ? $_REQUEST['action'] : '';
error_log("Action: " . $action);

if ($action === 'create') {
    $caller_id = isset($_POST['caller_id']) ? intval($_POST['caller_id']) : 0;
   $receiver_id = isset($_POST['receiver_id']) ? intval($_POST['receiver_id']) : 0;


    if ($caller_id <= 0 || $receiver_id <= 0) {
        echo json_encode(['success' => false, 'message' => 'Missing data']);
        exit;
    }

    $stmt = $con->prepare("INSERT INTO calls (caller_id, receiver_id, status) VALUES (?, ?, 'ringing')");
    $stmt->bind_param("ii", $caller_id, $receiver_id);

    if ($stmt->execute()) {
        echo json_encode([
            'success' => true,
            'message' => 'Call created',
            'call' => [
                'id' => $stmt->insert_id,
                'caller_id' => $caller_id,
                'receiver_id' => $receiver_id,
                'status' => 'ringing'
            ]
        ]);
    } else {
        echo json_encode(['success' => false, 'message' => 'DB insert failed: ' . $stmt->error]);
    }
    $stmt->close();
    exit;
}

if ($action === 'update') {
    $call_id = isset($_POST['caller_id']) ? intval($_POST['caller_id']) : 0;
    $status = isset($_POST['status']) ? $_POST['status'] : '';

    // Add "pending" to allowed statuses
    if ($call_id <= 0 || !in_array($status, ['ringing', 'active', 'ended', 'pending'])) {
        echo json_encode(['success' => false, 'message' => 'Invalid data']);
        exit;
    }

    $stmt = $con->prepare("UPDATE calls SET status=? WHERE id=?");
    $stmt->bind_param("si", $status, $call_id);

    if ($stmt->execute()) {
        echo json_encode(['success' => true, 'message' => 'Call status updated']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Update failed: ' . $stmt->error]);
    }

    $stmt->close();
    exit;
}


if ($action === 'status') {
    $call_id = isset($_GET['caller_id']) ? intval($_GET['caller_id']) : 0;

    if ($call_id <= 0) {
        echo json_encode(['success' => false, 'message' => 'Missing call ID']);
        exit;
    }

    $stmt = $con->prepare("SELECT id, caller_id, receiver_id, status FROM calls WHERE id=?");
    $stmt->bind_param("i", $call_id);
    $stmt->execute();
    $result = $stmt->get_result()->fetch_assoc();

    if ($result) {
        echo json_encode(['success' => true, 'call' => $result]);
    } else {
        echo json_encode(['success' => false, 'message' => 'Call not found']);
    }

    $stmt->close();
    exit;
}

// Check for incoming calls for a specific receiver
if ($action === 'check_incoming') {

    // MUST BE $_GET (Retrofit uses GET)
    $receiver_id = isset($_GET['receiver_id']) ? intval($_GET['receiver_id']) : 0;

    error_log("check_incoming called with receiver_id: " . $receiver_id);

    if ($receiver_id <= 0) {
        echo json_encode(['success' => false, 'message' => 'Missing receiver ID']);
        exit;
    }

    $stmt = $con->prepare("
        SELECT id, caller_id, receiver_id, status 
        FROM calls 
        WHERE receiver_id=? AND status='ringing'
        ORDER BY id DESC 
        LIMIT 1
    ");

    $stmt->bind_param("i", $receiver_id);
    $stmt->execute();
    $result = $stmt->get_result()->fetch_assoc();

    if ($result) {
        error_log("Incoming call found: " . print_r($result, true));
        echo json_encode(['success' => true, 'call' => $result]);
    } else {
        echo json_encode(['success' => false, 'message' => 'No incoming calls']);
    }

    exit;
}




// Default fallback
error_log("WARNING: Invalid action or no action matched: " . $action);
echo json_encode(['success' => false, 'message' => 'Invalid action']);
$con->close();
?>