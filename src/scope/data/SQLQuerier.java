package scope.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
	
	/**
	 * Contains the timestamp which is used in the sql query
	 */
	private DataProperty<Timestamp> timestampProperty;
	/**
	 * Config containing any sql related information to contact the database
	 */
	private Configuration config;
	/**
	 * Property to push new data to the calling thread
	 */
	private DataProperty<ArrayList<double[]>> dataListProperty;
	/**
	 * Controls the execution of the query
	 */
	private DataProperty<Boolean> booleanProperty;
	
	public SQLQuerier(DataProperty<Timestamp> timestampProperty, Configuration config, DataProperty<ArrayList<double[]>> dataListProperty, DataProperty<Boolean> booleanProperty){
		this.timestampProperty = timestampProperty;
		this.config = config;
		this.dataListProperty = dataListProperty;
		this.booleanProperty = booleanProperty;
		
		booleanProperty.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO Auto-generated method stub
				
			}
		});
		
		timestampProperty.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * Creates a connection to a config-file specified database and executs SQL queries to read data. The returned resultSet is processed into a double array
	 * which is further processed by {@link scope.data.SQLController}
	 */
	@Override
	public void run() {
		
		while(true){
			
			if(booleanProperty.getData()){
				//In case the query goes on for a while, no more threads are created when set to true.
				SQLController.hasActiveThread = true;
				Ini ini = config.getDefaultIni();
				Connection connection;
				ResultSet resultSet = null;
				ArrayList<double[]> dataList = new ArrayList<double[]>();
				try {
					connection = DriverManager.getConnection("jdbc:mysql://"+ini.get("server", "host")+":"
							+Integer.parseInt(ini.get("server", "port"))+"/"+ini.get("server", "database") + "?useSSL=false",
							ini.get("server", "user"), ini.get("server", "password"));
				
				String dbtable = ini.get("server", "table");
				Statement statement = connection.createStatement();
				
				
					int limit = SQLController.limit;
					//Execute query until a result is returned. This way, live-inserted data can also be read.
//					while(resultSet == null){
		//				System.out.println("Frage an");
						//if(timestamp.getTime() == -1){
							//resultSet = statement.executeQuery("SELECT * FROM " + dbtable + " ORDER BY PITIME ASC limit " + limit);
						//} else {
							resultSet = statement.executeQuery("SELECT * FROM " + dbtable + " WHERE PITIME >= '" + timestampProperty.getData().toString() +"' ORDER BY PITIME ASC limit " + limit);
						//}
//					}
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
									//SQLController.time++,
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
							dataListProperty.setData(dataList);
							resultSet = null;
							
						}
						SQLController.hasActiveThread = false;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				booleanProperty.setData(false);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
