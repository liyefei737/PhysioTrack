<?php
     error_log("Post:".$_POST['searchQuery']);
     if(isset($_POST['searchQuery']))
     {
     	  require_once('FullSimDataConfig.inc.php');
	  $search_query=$_POST['searchQuery'];
          $sql = 'SELECT * from FullSimData7 where (DateTime REGEXP :search_query)';
          $statement = $connection->prepare($sql);
	  $statement->bindParam(':search_query', $search_query, PDO::PARAM_STR);
          $statement->execute();
	  error_log("Row count: ".$statement->rowCount());
          if($statement->rowCount())
          {
	    $row_all = $statement->fetchall(PDO::FETCH_ASSOC);
	    header('Content-type: application/json');
   	    echo json_encode($row_all);
          }  
          elseif(!$statement->rowCount())
          {
	    echo "no rows";
          }
     }		  
?>
