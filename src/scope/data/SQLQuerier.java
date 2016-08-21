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

public class SQLQuerier implements Runnable{
	
	private Timestamp timestamp;
	private Configuration config;
	private ArrayListProperty dataListProperty;
	
	public SQLQuerier(Timestamp timestamp, Configuration config, ArrayListProperty dataListProperty){
		this.timestamp = timestamp;
		this.config = config;
		this.dataListProperty = dataListProperty;
	
	}

	@Override
	public void run() {
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
			if(timestamp.getTime() == -1){
				resultSet = statement.executeQuery("SELECT * FROM " + dbtable + " ORDER BY PITIME ASC limit " + limit);
			} else {
				resultSet = statement.executeQuery("SELECT * FROM " + dbtable + " WHERE PITIME >= '" + timestamp.toString() +"' ORDER BY PITIME ASC limit " + limit);
			}
				if(resultSet != null && resultSet.next()){
					
					if(SQLController.doOnce){
						resultSet.afterLast();
						resultSet.previous();
						SQLController.lastRowTimestamp = resultSet.getTimestamp("PITIME");
						SQLController.doOnce = false;
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
					
				}
				
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
