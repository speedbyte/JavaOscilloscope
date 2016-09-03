package scope.gui;

import java.util.ArrayList;
import scope.data.SQLController;

public class GUIClass {

	public static void main(String[] args) {

		// init mediator
		final MMInterface mm = new ModelMediator();
		
		ViewInterface XYSeriesChart = new View();
		//Prevents NumberAxis to show on initial startup using config file
		//XYSeriesChart.initDatasetCapacity(8);
		XYSeriesChart.setModel(mm);
		
				
		Thread GUIThread = new Thread(new Runnable() {
			@Override
			public void run() {
			    
			        try {
			            while(true){
			            	
			            	double[] data = {0};
			            	ArrayList<double[]> dataList = null;
							
							switch(SQLController.resultFlag){
							
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
									switch(SQLController.resultFlag){
										case 1:
											SQLController.dataListOne = null;
											SQLController.resultFlag = 2;
											break;
										case 2:
											SQLController.dataListTwo = null;
											SQLController.resultFlag = 1;
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
