package scope.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.ini4j.Ini;

import scope.gui.Configuration;

/**
 * Contacts a database and reads data from it.
 * @author Philipp
 *
 */
public class SQLQuerier implements Runnable{
	
	private Timestamp timestamp;
	private Configuration config;
	private ArrayListProperty dataListProperty;
	
	public SQLQuerier(Timestamp timestamp, Configuration config, ArrayListProperty dataListProperty){
		this.timestamp = timestamp;
		this.config = config;
		this.dataListProperty = dataListProperty;
	
	}
	
	/**
	 * Creates a connection to a config-file specified database and executs SQL queries to read data. The returned resultSet is processed into a double array
	 * which is further processed by {@link scope.gui.GUIClass}
	 */
	@Override
	public void run() {
		//In case the query goes on for a while, no more threads are created when set to true.
		SQLController.hasActiveThread = true;
		Ini ini = config.getDefaultIni();
		Connection connection;
		ResultSet resultSet = null;
		ArrayList<double[]> dataList = new ArrayList<double[]>();
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"+ini.get("server", "host")+":"
					+Integer.parseInt(ini.get("server", "port"))+"/"+ini.get("server", "database"),
					ini.get("server", "user"), ini.get("server", "password"));
		
		String dbtable = ini.get("server", "table");
		Statement statement = connection.createStatement();
		
		
			int limit = SQLController.limit;
			//Execute query until a result is returned. This way, live-inserted data can also be read.
			while(resultSet == null){
				System.out.println("Frage an");
				if(timestamp.getTime() == -1){
					resultSet = statement.executeQuery("SELECT * FROM " + dbtable + " ORDER BY PITIME ASC limit " + limit);
				} else {
					resultSet = statement.executeQuery("SELECT * FROM " + dbtable + " WHERE PITIME >= '" + timestamp.toString() +"' ORDER BY PITIME ASC limit " + limit);
				}
			}
				if(resultSet != null && resultSet.next()){
					
					//Save the timestamp from the last row of the current ResultSet, such that the next query can go on from there
					if(SQLController.getNewTimestampFlag){
						resultSet.afterLast();
						resultSet.previous();
						SQLController.lastResultsetTimestamp = resultSet.getTimestamp("PITIME");
						SQLController.getNewTimestampFlag = false;
						resultSet.beforeFirst();
					}
					
					while(resultSet.next()){
					double[] data = {
							(double) resultSet.getTimestamp("PITIME").getTime(),
							resultSet.getDouble("ACC_X"),
							resultSet.getDouble("ACC_Y"),
							resultSet.getDouble("ACC_Z"),
							resultSet.getDouble("MAG_X"),
							resultSet.getDouble("MAG_Y"),
							resultSet.getDouble("MAG_Z"),
							resultSet.getDouble("G_ROLL"),
							resultSet.getDouble("G_YAW"),
							resultSet.getDouble("G_PITCH"),
							resultSet.getDouble("TEMP"),
							resultSet.getDouble("PRESS"),
							resultSet.getDouble("M1"),
							resultSet.getDouble("M2"),
							resultSet.getDouble("M3"),
							resultSet.getDouble("M4"),
					};
					
					dataList.add(data);
					}
					dataListProperty.setList(dataList);
					resultSet = null;
					
				}
				SQLController.hasActiveThread = false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
