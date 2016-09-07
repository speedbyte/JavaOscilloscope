package scope.gui;

import java.util.ArrayList;
import scope.data.SQLController;

/**
 * This class initializes the application and provides the link between database querying and displaying data
 * @author Philipp Gackstatter
 * @version 1.0
 */

public class GUIClass {

	/**
	 * Starts the main class for this project. 
	 * @param args command line arguments
	 */
	
	public static void main(String[] args) {

		// init mediator
		final MMInterface mm = new ModelMediator();
		
		ViewInterface XYSeriesChart = new View();
		//Prevents NumberAxis to show on initial startup, this is unwanted when using a config file
		//XYSeriesChart.initDatasetCapacity(8);
		XYSeriesChart.setModel(mm);
		
				
		Thread GUIThread = new Thread(new Runnable() {
			@Override
			public void run() {
			    
			        try {
			            while(true){
			            	
			            	double[] data = {0};
			            	ArrayList<double[]> dataList = null;
							
							switch(SQLController.dataListIndex){
							
								case 1:
									dataList = SQLController.dataListOne;
									break;
								case 2:
									dataList = SQLController.dataListTwo;
									break;
							
							}
							
							try {
								
								if(dataList != null && SQLController.listIndex < dataList.size()){
									
									data = dataList.get(SQLController.listIndex);
									SQLController.increaseReadIndex();
									SQLController.listIndex++;
									
								}  else {
									SQLController.listIndex = 0;
									SQLController.increaseReadIndex();
									switch(SQLController.dataListIndex){
										case 1:
											SQLController.dataListOne = null;
											SQLController.dataListIndex = 2;
											break;
										case 2:
											SQLController.dataListTwo = null;
											SQLController.dataListIndex = 1;
											break;							
									}
									
								}
								
								mm.pushDataArray(data);
							} catch (Exception e) {
								e.printStackTrace();
							}
			            	
			            	
			            //Thread.sleep(20);
			            
			            }
			        }
			        catch (Exception e) {}
			
			}
		});
		GUIThread.start();
	}
	
}
