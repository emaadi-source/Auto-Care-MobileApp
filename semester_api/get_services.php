<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Access-Control-Allow-Methods: POST");

include "connect.php";

$provider_id = isset($_POST['provider_id']) ? intval($_POST['provider_id']) : 0;

// --- FIX IS HERE: Added 'id' to the query ---
$query = "SELECT id, name, category, price, type FROM provider_services WHERE provider_id='$provider_id'";
$result = mysqli_query($con, $query);

$services = array();
while ($row = mysqli_fetch_assoc($result)) {
    $services[] = $row;
}

echo json_encode($services);
?>