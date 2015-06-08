package scope.gui;

import java.util.LinkedList;

public interface MMInterface extends ObserverInterface {
	void pushDataArray(long[] data);
	public LinkedList<long[]> getData();
	public void notifyObservers();	
}
