package testSQLite2;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
public class Driver {
	
	public static void main(String[] args) throws IOException {

		Connection conn = null;
		String csvfile = "csv.csv";
		BufferedReader br=null;
		String line ="";
		String splitBy=",";
		System.out.println("Start!");
		
		long start_time = System.nanoTime();

		try{
			String url="jdbc:sqlite://Users/mehmetatmaca/Desktop/yeni.sqlite";
			conn = DriverManager.getConnection(url);
			//System.out.println("Connection established");
			
			try{
				br = new BufferedReader(new FileReader(csvfile));
				while ((line = br.readLine())!=null){
					String[] time = line.split(splitBy);
					String exe = "INSERT INTO test (time,col1,col2) VALUES(?,?,?)";
					PreparedStatement pst = conn.prepareStatement(exe);
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
		catch(SQLException e){
			System.out.println(e.getMessage());
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
				}
				catch(SQLException ex){
					System.out.println(ex.getMessage());
				}
			}
		long end_time = System.nanoTime();
		double difference = (end_time - start_time)/1e6;
		System.out.println("Done in " + difference + " ms");
		}
	

	}
