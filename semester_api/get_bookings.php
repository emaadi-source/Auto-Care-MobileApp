<?php

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

require_once "connect.php";

if (!isset($_GET["customer_id"])) {
    echo json_encode([
        "success" => false,
        "message" => "customer_id is required"
    ]);
    exit();
}

$customer_id = mysqli_real_escape_string($con, $_GET["customer_id"]);

$sql = "
    SELECT 
        b.id AS booking_id,
        p.workshop_name,
        p.address,
        p.city,
        b.total_amount AS total_price,
        b.booking_date,
        b.booking_time,
        b.status
    FROM bookings b
    INNER JOIN workshop_details p ON b.provider_id = p.provider_id
    WHERE b.customer_id = '$customer_id'
    ORDER BY b.booking_date DESC
";

$result = mysqli_query($con, $sql);

$bookings = [];

if ($result && mysqli_num_rows($result) > 0) {
    while ($row = mysqli_fetch_assoc($result)) {
        $bookings[] = $row;
    }

    echo json_encode([
        "success" => true,
        "bookings" => $bookings
    ]);

} else {
    echo json_encode([
        "success" => false,
        "message" => "No bookings found",
        "bookings" => []
    ]);
}
?>
