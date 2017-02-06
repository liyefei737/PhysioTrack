package testSQLite;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class Driver {// localhost sql server db.

	public static void main(String[] args) throws IOException {
		String csvfile = "csv.csv";
		BufferedReader br=null;
		String line ="";
		String splitBy=",";
		System.out.println("Start!");
		
		long start_time = System.nanoTime();
		
		
		
		try{
			Connection myCon= DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlite","mehmet","Ma80Ny08");
			
			Statement mySta = myCon.createStatement();
			
			//ResultSet myRes = mySta.executeQuery("Select * from data");
			
			//while(myRes.next()){
				//System.out.println(myRes.getString("id")+", "+ myRes.getString("time")+", "+myRes.getString("col1"));
			//}
		try{
			br = new BufferedReader(new FileReader(csvfile));
			while ((line = br.readLine())!=null){
				String[] time = line.split(splitBy);
				//System.out.println("deneme "+ time[0]+" "+time[13]);
				String exe = "INSERT INTO data (time,col1,col2) VALUES(?,?,?)";
				PreparedStatement pst = myCon.prepareStatement(exe);
				pst.setString(1,time[0]);
				pst.setString(2, time[13]);
				pst.setString(3, time[14]);
				pst.executeUpdate();
			}
		}catch(FileNotFoundException e){
			throw new FileNotFoundException("File not found");
		}
		catch(IOException e){
			throw new IOException("IO hatasi");
		}
		finally{
			if(br!=null){
				try{
					br.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
		
		long end_time = System.nanoTime();
		double difference = (end_time - start_time)/1e6;
		System.out.println("Done in " + difference + " ms");
	}


}
