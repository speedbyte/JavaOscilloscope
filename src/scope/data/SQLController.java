package scope.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import scope.gui.Configuration;

public class SQLController {
	
	public static ArrayList<double[]> dataListOne;
	public static ArrayList<double[]> dataListTwo;
	public static int resultFlag = 1;
	public static boolean doOnce = true;
	public static String dbtable;
	public static SQLQuerier sqlquerier;
	public static Configuration config;
	public static int limit = 500;
	public static int readIndex = limit/2;
	public static Timestamp lastRowTimestamp;
	public static int listIndex = 0;
	private static ArrayListProperty dataListProperty;
	private static boolean doFirstTime = true;
	
	
	public static void increaseReadIndex(){
		readIndex++;
		if(readIndex == limit/5)
			readFromTimestamp(new Timestamp(lastRowTimestamp.getTime()));
					
		if (readIndex > limit)
			readIndex = 0;
		
	}
	
	public static void readFromTimestamp(Timestamp timestmp){
		dataListProperty = new ArrayListProperty();
		Thread sqlthread = new Thread(new SQLQuerier(timestmp, config, dataListProperty));
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
					
					switch(resultFlag){
						case 2:
							dataListOne = (ArrayList<double[]>) evt.getNewValue();
							break;
						case 1: 
							dataListTwo = (ArrayList<double[]>) evt.getNewValue();
							break;			
					}
				}
		}});
		
		doOnce = true;
	}

	
	public static void initializeDatabaseConnection(Timestamp timestamp, Configuration config){
		SQLController.config = config;
		readFromTimestamp(timestamp);
	}
	
}
