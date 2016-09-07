package scope.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import scope.gui.Configuration;

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
	public static int limit = 500;
	/**
	 * The index that keeps track of SQL queries 
	 */
	public static int readIndex = limit/2;
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
	/**
	 * Flag that is necessary when both dataLists are empty
	 */
	private static boolean doFirstTime = true;
	
	/**
	 * Increases the readIndex and triggers database queries after 1/5th of the ResultSet size (limit)
	 */
	public static void increaseReadIndex(){
		readIndex++;
		if(readIndex == limit/5 && !hasActiveThread)
			readFromTimestamp(new Timestamp(lastResultsetTimestamp.getTime()));
					
		if (readIndex > limit)
			readIndex = 0;
		
	}
	
	/**
	 * Creates a new {@link SQLQuerier} Thread, which queries the database. Timestamp is the last timestamp of the last ResultSet and dataListProperty
	 * changes as soon as it changes in the other thread. This way, as soon as the SQL query is finished, the result is returned and stored in the currently
	 * unused dataList, indicated by the dataListIndex
	 * @param timestamp the timestamp that will be used in the SQL query
	 */
	public static void readFromTimestamp(Timestamp timestamp){
		
		dataListProperty = new ArrayListProperty();
		Thread sqlthread = new Thread(new SQLQuerier(timestamp, config, dataListProperty));
		sqlthread.setDaemon(false);
		sqlthread.start();
		dataListProperty.addPropertyChangeListener(new PropertyChangeListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				if(doFirstTime){
					dataListOne = (ArrayList<double[]>) evt.getNewValue();
					doFirstTime = false;
				} else {
					
					switch(dataListIndex){
						case 2:
							dataListOne = (ArrayList<double[]>) evt.getNewValue();
							break;
						case 1: 
							dataListTwo = (ArrayList<double[]>) evt.getNewValue();
							break;			
					}
				}
		}});
		
		getNewTimestampFlag = true;
		
	}

	/**
	 * Sets the config and executes {@link readFromTimestamp}
	 * @param timestamp the timestamp that will be used in the SQL query
	 * @param config the config file that will be used for all future database communication
	 */
	public static void initializeDatabaseConnection(Timestamp timestamp, Configuration config){
		SQLController.config = config;
		readFromTimestamp(timestamp);
	}
	
}
