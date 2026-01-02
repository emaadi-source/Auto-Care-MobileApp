<?php
include "connect.php";

$query = "SELECT * FROM workshop_details";
$result = mysqli_query($con, $query);

$workshops = array();

while ($row = mysqli_fetch_assoc($result)) {

    $item = [
        "id" => $row["provider_id"],
        "name" => $row["workshop_name"],
        "details" => $row["description"],
        "image" => $row["image_1"]
    ];

    $workshops[] = $item;
}

echo json_encode($workshops);

?>
