<?php
require '../connect.php';

header('Content-Type: application/json');

if (isset($_POST['provider_id'])) {
    $provider_id = $_POST['provider_id'];

    $query = "SELECT * FROM workshop_details WHERE provider_id = '$provider_id'";
    $result = mysqli_query($con, $query);

    if (mysqli_num_rows($result) > 0) {
        $data = mysqli_fetch_assoc($result);
        echo json_encode(["error" => false, "data" => $data]);
    } else {
        echo json_encode(["error" => true, "message" => "Profile not found"]);
    }
}
?>