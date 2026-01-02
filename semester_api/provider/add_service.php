<?php
require '../connect.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $provider_id = $_POST['provider_id']; // You must send this from Android
    $name = $_POST['name'];
    $category = $_POST['category'];
    $price = $_POST['price'];
    $type = $_POST['type']; // 'SERVICE' or 'PART'
    
    // Optional fields
    $brand = isset($_POST['brand']) ? $_POST['brand'] : NULL;
    $stock = isset($_POST['stock']) ? $_POST['stock'] : NULL;
    $duration = isset($_POST['duration']) ? $_POST['duration'] : NULL;

    $query = "INSERT INTO provider_services (provider_id, name, category, price, type, brand, stock_quantity, duration) 
              VALUES ('$provider_id', '$name', '$category', '$price', '$type', '$brand', '$stock', '$duration')";

    if (mysqli_query($con, $query)) {
        echo json_encode(["error" => false, "message" => "Item added successfully"]);
    } else {
        echo json_encode(["error" => true, "message" => "Database error: " . mysqli_error($con)]);
    }
}
?>