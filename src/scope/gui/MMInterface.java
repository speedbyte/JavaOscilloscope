package scope.gui;

import java.util.LinkedList;

public interface MMInterface extends ObserverInterface {
	void pushDataArray(double[] data);
	public LinkedList<double[]> getData();
	public void notifyObservers();	
}
