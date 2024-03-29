package scope.data;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTable;
 
public class SQL {
	
public static JTable table;
public static Statement statement;
public static ResultSet resultSet;
protected static Connection connection = null;

public static void main() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.print("no driver found");
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"+InitialFrame.textField_ServerAddress.getText()+":"
					+Integer.parseInt(InitialFrame.textField_Port.getText())+"/"+InitialFrame.textField_DatabaseName.getText(),
					InitialFrame.textField_UserInstance.getText(), InitialFrame.textField_Password.getText());
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print("no database found");
			e.printStackTrace();
		}
		finally
		{
			System.out.print("database connection established\n");
		}
		/**/
  }
public static int checkDatetimeExists(String date, String time) throws SQLException {
	try {
		resultSet = statement.executeQuery("SELECT id FROM datetime WHERE date = '"+date+"' AND time = '"+time+"'; ");
	} catch (SQLException e) {
		e.printStackTrace();
	}
	boolean test = resultSet.first();
	if (test==true){
		int id_datetime = resultSet.getInt("id");
		return id_datetime;}
	else{return -1;}
}
	static int importIntoDatetime(String date, String time) throws SQLException{
		System.out.println(date);
		statement.executeUpdate("INSERT INTO datetime (id, date, time) VALUES (NULL, '"+date+"', '"+time+"');");
		int id_datetime = checkDatetimeExists(date,time);
		return id_datetime;
	}
	  //static void importValues (String date, String time, int data1, int data2, int data3, int data4,
	//		  int data5, int data6, int data7, int data8, int can_id, int timestamp) throws SQLException{
	//	  statement.executeUpdate("INSERT INTO can_data (id,date,time, data1, data2, data3, data4, data5, data6, data7, data8, can_id, timestamp)VALUES (NULL, '"+date+"', '"+time+"', '"+data1+"', '"+data2+"', '"+data3+"', '"+data4+"', '"+data5+"', '"+data6+"', '"+data7+"', '"+data8+"', '"+can_id+"', '"+timestamp+"');");
	  //}
	static int importValues(int k, int id_datetime, float timestamp, int can_id, int[] data, float GPS_lat, float GPS_lon) throws SQLException{
		String s = "";
		String st = "";
		for (int i = 0; i < data.length; i++){
			s = s+" data"+(i+1)+",";
			st = st+" '"+data[i]+"',";
		}
		try{
		statement.executeUpdate("INSERT INTO can_data ("+s+" can_id, timestamp, id_datetime, GPS_lat, GPS_lon, id) VALUES ("+st+" '"+can_id+"', '"+timestamp+"', '"+id_datetime+"', '"+GPS_lat+"', '"+GPS_lon+"', NULL);");
		}
		catch(SQLException e){return k;}
		k++;
		return k;
		}
	
	public static int deleteData(int id_datetime) throws SQLException {
		int numDeletedData=0;
		try {
			System.out.println("id_datetime="+id_datetime);
			resultSet = statement.executeQuery("SELECT id FROM can_data WHERE id_datetime = '"+id_datetime+"';");
			//resultSet = statement.executeQuery("DELETE FROM can_data WHERE id_datetime = '"+id_datetime+"';");
			//resultSet.last();
			//numDeletedData = resultSet.getRow()+1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if (resultSet.last()){
			 resultSet.first();
			 String str = "";
			 int FirstValid=0;
			 do{
				 if (FirstValid==0){str = "delete from can_data where id = "+resultSet.getInt("id")+" "; FirstValid=1;}
					else{str=str+" OR id = "+resultSet.getInt("id");}
				 numDeletedData++;
			 }while (resultSet.next());
			 str=str+";";
			 System.out.println(str);
			 try {
					 statement.executeUpdate(""+str);
					} 
			 catch (SQLException e) {
						e.printStackTrace();
						return 0;
					}		 
			return numDeletedData;
		 }
		 else {return 0;}
	}
	public static boolean testConnection(){
		boolean result = true;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.print("no driver found");
			e.printStackTrace();
			result = false;
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"+InitialFrame.textField_ServerAddress.getText()+":"
					+Integer.parseInt(InitialFrame.textField_Port.getText())+"/"+InitialFrame.textField_DatabaseName.getText(),
					InitialFrame.textField_UserInstance.getText(), InitialFrame.textField_Password.getText());
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print("no database found");
			e.printStackTrace();
			result = false;
		}
		finally
		{
			System.out.print("database connection established\n");
		}	
		return result;
	}
}