
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class TestBlob {

	public static void main(String[] args) {
		
		Connection conn=null;
		Path dir = Paths.get("C:/Users/prashant/Desktop/");
		File file = new File(dir+"/networkruntime.zip");
		try {
						
			byte[] bytes = FileUtils.readFileToByteArray(file);
			String origCheckSum= DigestUtils.md5Hex(bytes);
	
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionString = "jdbc:sqlserver://;serverName=localhost;databaseName=master;integratedSecurity=true;";
	        String uName="RFL207\\prashant";
	        String pWord="bareilly";
			conn = DriverManager.getConnection(connectionString,uName,pWord);
			
			String exQuery="insert into testblob (name,body,checksum) values(?,?,?)"; 
			PreparedStatement exStmt = conn.prepareStatement(exQuery);
		    
		    exStmt.setString(1, "NetworkRunTimePS.zip");
		    exStmt.setBytes(2, bytes);
		    exStmt.setString(3, origCheckSum);
		   
		   // int i=exStmt.executeUpdate();  
		    //System.out.println(i+" records inserted");  
		    
		    String query="select * from testblob";
		    PreparedStatement sStmt = conn.prepareStatement(query,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
	
			ResultSet rsData=sStmt.executeQuery();
			while(rsData.next())
			{
				Blob blob = rsData.getBlob("body");
				String fileName=rsData.getString("Name");
				InputStream inputStream = blob.getBinaryStream();
				
				System.out.println("-----------------------------------");
				Files.copy(inputStream,Paths.get(dir+"/"+fileName));
			}
				
	
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
