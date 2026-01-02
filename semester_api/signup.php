<?php
require 'connect.php';

// Check if request is POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $full_name = $_POST['full_name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $phone = $_POST['phone'];
    $role = $_POST['role']; // Important: Pass 'customer' or 'provider' from Android

    // Check if email already exists
    $checkQuery = "SELECT * FROM users WHERE email='$email'";
    $result = mysqli_query($con, $checkQuery);

    if (mysqli_num_rows($result) > 0) {
        $response['error'] = true;
        $response['message'] = "Email already registered";
    } else {
        // Hash the password for security
        $hashed_password = password_hash($password, PASSWORD_DEFAULT);

        $insertQuery = "INSERT INTO users (full_name, email, password, phone, role) VALUES ('$full_name', '$email', '$hashed_password', '$phone', '$role')";

        if (mysqli_query($con, $insertQuery)) {
            $response['error'] = false;
            $response['message'] = "Registration successful";
        } else {
            $response['error'] = true;
            $response['message'] = "Registration failed";
        }
    }
} else {
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}

echo json_encode($response);
?>