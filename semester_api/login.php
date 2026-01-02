<?php
require 'connect.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $_POST['email'];
    $password = $_POST['password'];

    $query = "SELECT * FROM users WHERE email='$email'";
    $result = mysqli_query($con, $query);

    if (mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        $hashed_password = $row['password'];

        // Verify the password
        if (password_verify($password, $hashed_password)) {
            $response['error'] = false;
            $response['message'] = "Login successful";
            $response['user'] = $row; // Send user data back to Android
        } else {
            $response['error'] = true;
            $response['message'] = "Invalid password";
        }
    } else {
        $response['error'] = true;
        $response['message'] = "User not found";
    }
} else {
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}

echo json_encode($response);
?>