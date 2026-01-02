<?php
$host = "localhost";
$user = "root";
$pass = ""; // Default XAMPP password is empty
$db = "semester_project_db";

$con = mysqli_connect($host, $user, $pass, $db);

if (!$con) {
    die("Connection failed: " . mysqli_connect_error());
}
?>