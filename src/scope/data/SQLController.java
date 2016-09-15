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
	public static ArrayList<double[]> dataListThree;
	/**
	 * Jumps between 1 and 2, so that {@link readFromTimestamp} writes to {@link dataListOne} or {@link dataListTwo} 
	 */
	public static int dataListIndex = 1;
	/**
	 * Flag that indicates if the {@link lastResultsetTimestamp} can be updated
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
	 * Flag that keeps track if there is an active Thread. If there is, no new {@link readFromTimestamp} will be called.
	 */
	public static boolean hasActiveThread = false;
	/**
	 * {@link ArrayListProperty} that allows for communication between this and the SQLQuerier thread  
	 */
	private static ArrayListProperty dataListProperty;
	private static BooleanProperty booleanProperty;
	private static TimestampProperty timestampProperty;
	/**
	 * Flag that is necessary when both dataLists are empty
	 */
	private static boolean doFirstTime = true;
	
	public static int time = 0;
	/**
	 * Increases the readIndex and triggers database queries after 1/5th of the ResultSet size (limit)
	 */
//	public static void increaseReadIndex(){
//		readIndex++;
////		System.out.println(readIndex);
//		int lowerLimit = (int) (limit * 1.0/3.0);
//		int upperLimit = (int) (limit * 2.0/3.0);
////		System.out.println("lower: " + lowerLimit + " upper: " + upperLimit);
//		if((readIndex == lowerLimit || readIndex == upperLimit) && !hasActiveThread)
//			readFromTimestamp(new Timestamp(lastResultsetTimestamp.getTime()));
//					
//		if (readIndex > limit)
//			readIndex = 0;
//		
//	}
	
	/**
	 * Creates a new {@link SQLQuerier} Thread, which queries the database. Timestamp is the last timestamp of the last ResultSet and dataListProperty
	 * changes as soon as it changes in the other thread. This way, as soon as the SQL query is finished, the result is returned and stored in the currently
	 * unused dataList, indicated by the dataListIndex
	 * @param timestamp the timestamp that will be used in the SQL query
	 */
	public static void readFromTimestamp(Timestamp timestamp){
		//System.out.println("Call DB: " + readIndex);
		timestampProperty.setTimestamp(timestamp);
		booleanProperty.setBoolean(true);
		getNewTimestampFlag = true;	
	}

	/**
	 * Sets the config and executes {@link readFromTimestamp}
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
				dataListProperty = new ArrayListProperty();
				booleanProperty = new BooleanProperty();
				timestampProperty = new TimestampProperty();
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
