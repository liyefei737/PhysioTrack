<?php

$servername = "localhost";
$username = "root";
$password = "Ma80Ny08@sql";
$dbname = "capstone";

try {
    	$connection = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    	$connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }
catch(PDOException $e)
    {
	echo "dying now";
    	die("OOPs something went wrong");
    }

?>