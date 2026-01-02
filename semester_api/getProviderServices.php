<?php
include "connect.php";

// Get provider_id from POST safely
$provider_id = isset($_POST['provider_id']) ? intval($_POST['provider_id']) : 0;

$query = "SELECT name FROM provider_services WHERE provider_id='$provider_id'";
$result = mysqli_query($con, $query);

$services = array();
while ($row = mysqli_fetch_assoc($result)) {
    $services[] = $row;
}

echo json_encode($services);

?>
