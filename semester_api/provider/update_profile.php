<?php
require '../connect.php';

// Set header to JSON
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    
    // 1. Get All Inputs
    $provider_id = $_POST['provider_id'];
    $name = $_POST['workshop_name'];
    $owner = $_POST['owner_name'];
    $desc = $_POST['description'];
    $address = $_POST['address'];
    $city = $_POST['city'];
    $pin = $_POST['pincode'];
    
    // NEW FIELDS
    // Use isset() check to prevent errors if they are empty
    $email = isset($_POST['contact_email']) ? $_POST['contact_email'] : '';
    $phone = isset($_POST['contact_phone']) ? $_POST['contact_phone'] : '';
    $img1 = isset($_POST['image_1']) ? $_POST['image_1'] : null;
    $img2 = isset($_POST['image_2']) ? $_POST['image_2'] : null;

    // 2. Check if Profile Exists
    $check = mysqli_query($con, "SELECT id FROM workshop_details WHERE provider_id='$provider_id'");

    if (mysqli_num_rows($check) > 0) {
        // --- UPDATE EXISTING RECORD ---
        
        // We build the query carefully. 
        // Note: We use prepared statements or simple string concatenation here for your XAMPP setup.
        // For simplicity in this project, we update all fields.
        
        $query = "UPDATE workshop_details SET 
                  workshop_name='$name', 
                  owner_name='$owner', 
                  description='$desc', 
                  address='$address', 
                  city='$city', 
                  pincode='$pin',
                  contact_email='$email',
                  contact_phone='$phone'";

        // Only update images if the user actually selected a new one (sent a base64 string)
        if ($img1 != null) {
            $query .= ", image_1='$img1'";
        }
        if ($img2 != null) {
            $query .= ", image_2='$img2'";
        }

        $query .= " WHERE provider_id='$provider_id'";

    } else {
        // --- INSERT NEW RECORD ---
        $query = "INSERT INTO workshop_details 
                  (provider_id, workshop_name, owner_name, description, address, city, pincode, contact_email, contact_phone, image_1, image_2) 
                  VALUES 
                  ('$provider_id', '$name', '$owner', '$desc', '$address', '$city', '$pin', '$email', '$phone', '$img1', '$img2')";
    }

    // 3. Execute Query
    if (mysqli_query($con, $query)) {
        echo json_encode(["error" => false, "message" => "Profile saved successfully"]);
    } else {
        echo json_encode(["error" => true, "message" => "Database Error: " . mysqli_error($con)]);
    }

} else {
    echo json_encode(["error" => true, "message" => "Invalid Request"]);
}
?>