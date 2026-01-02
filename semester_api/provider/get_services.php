<?php
require '../connect.php';

if (isset($_POST['provider_id'])) {
    $provider_id = $_POST['provider_id'];
    
    $query = "SELECT * FROM provider_services WHERE provider_id = '$provider_id' ORDER BY id DESC";
    $result = mysqli_query($con, $query);

    $items = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $items[] = $row;
    }
    
    echo json_encode(["error" => false, "data" => $items]);
} else {
    echo json_encode(["error" => true, "message" => "Provider ID missing"]);
}
?>