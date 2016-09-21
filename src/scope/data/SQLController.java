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
import scope.gui.Configuration;
import org.ini4j.Ini;

/**
 * Controls the data flow between {@link scope.gui.GUIClass} and {@link SQLQuerier} 
 * @author Philipp
 *
 */

public class SQLController {
	
	/**
	 * List containting double arrays that contain the data
	 */
	public static ArrayList<double[]> dataListOne;
	/**
	 * List containting double arrays that contain the data
	 */
	public static ArrayList<double[]> dataListTwo;
	/**
	 * List containting double arrays that contain the data
	 */
	public static ArrayList<double[]> dataListThree;
	/**
	 * Jumps between 1 and 2, so that readFromTimestamp writes to dataListOne or dataListTwo 
	 */
	public static int dataListIndex = 1;
	/**
	 * Flag that indicates if the lastResultsetTimestamp can be updated
	 */
	public static boolean getNewTimestampFlag = true;
	/**
	 * The database table from which {@link SQLQuerier} reads
	 */
	public static String dbtable;
	/**
	 * The loaded config file that is used for all database communication
	 */
	public static Configuration config;
	/**
	 * The size of the ResultSet
	 */
	public static int limit = 150;
	/**
	 * The index that keeps track of SQL queries 
	 */
	public static int readIndex = 0;
	/**
	 * The timestamp of the last row from the last used ResultSet 
	 */
	public static Timestamp lastResultsetTimestamp;
	/**
	 * The index of the currently used dataList
	 */
	public static int listIndex = 0;
	/**
	 * Flag that keeps track if there is an active Thread. If there is, no new readFromTimestamp will be called.
	 */
	public static boolean hasActiveThread = false;
	/**
	 * {@link ArrayListProperty} that allows for communication between this and the SQLQuerier thread  
	 */
	private static DataProperty<ArrayList<double[]>> dataListProperty;
	/**
	 * {@link BooleanProperty} that controls the start of queries SQLQuerier thread  
	 */
	private static DataProperty<Boolean> booleanProperty;
	/**
	 * {@link TimestampProperty} that controls the query's timestamp in the SQLQuerier thread  
	 */
	private static DataProperty<Timestamp> timestampProperty;
	
	/**
	 * Creates a new {@link SQLQuerier} Thread, which queries the database. Timestamp is the last timestamp of the last ResultSet.
	 * @param timestamp the timestamp that will be used in the SQL query
	 */
	public static void readFromTimestamp(Timestamp timestamp){
		timestampProperty.setData(timestamp);
		booleanProperty.setData(true);
		getNewTimestampFlag = true;	
	}

	/**
	 * Sets the config and initializes the SQL thread
	 * dataListProperty changes as soon as it changes in the SQLQuerier thread. This way, as soon as the SQL query is finished, the result 
	 * is returned and stored in the currently unused dataList, indicated by the dataListIndex
	 * Differentiates between 'live' and 'all' mode, which displays live inserted data or everything in the database
	 * @param timestamp the timestamp that will be used in the SQL query
	 * @param config the config file that will be used for all future database communication
	 */
	public static void initializeDatabaseConnection(Timestamp timestamp, Configuration config){
		SQLController.config = config;
		Ini ini = config.getDefaultIni();
		
			Connection connection;
			try {
				connection = DriverManager.getConnection("jdbc:mysql://"+ini.get("server", "host")+":"
						+Integer.parseInt(ini.get("server", "port"))+"/"+ini.get("server", "database"),
						ini.get("server", "user"), ini.get("server", "password"));
			
			
				String dbtable = ini.get("server", "table");
				String queryMode = ini.get("server", "mode");
				if(queryMode.equals("live")){
					Statement statement = connection.createStatement();
					ResultSet rs = statement.executeQuery("SELECT * FROM " + dbtable + " ORDER BY PITIME DESC limit 1");
					if(rs.next());
						timestamp = rs.getTimestamp("PITIME");
					SQLController.lastResultsetTimestamp = timestamp;
					statement.close();
					connection.close();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if(queryMode.equals("all")){
					timestamp = new Timestamp(-1);
				}
				dataListProperty = new DataProperty<ArrayList<double[]>>();
				booleanProperty = new DataProperty<Boolean>();
				booleanProperty.setData(true);
				timestampProperty = new DataProperty<Timestamp>();
				Thread sqlthread = new Thread(new SQLQuerier(timestampProperty, config, dataListProperty, booleanProperty));
				sqlthread.setDaemon(false);
				sqlthread.start();
				

				dataListProperty.addPropertyChangeListener(new PropertyChangeListener() {
					
					@SuppressWarnings("unchecked")
					@Override
					public void propertyChange(PropertyChangeEvent evt) {

						if(dataListOne == null && dataListTwo == null && dataListThree == null){
							dataListOne = (ArrayList<double[]>) evt.getNewValue();
//							doFirstTime = false;
						} else {
							System.out.println("Write " + ((ArrayList<double[]>) evt.getNewValue()).size() + " values to list " + dataListIndex);
							switch(dataListIndex){
								case 1: 
									dataListTwo = (ArrayList<double[]>) evt.getNewValue();
									break;
								case 2:
									dataListThree = (ArrayList<double[]>) evt.getNewValue();
									break;
								case 3:
									dataListOne = (ArrayList<double[]>) evt.getNewValue();
									break;
							}
						}
				}});
				
				readFromTimestamp(timestamp);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
	}
	
}
