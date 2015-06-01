package scope.gui;

import java.util.LinkedList;

public interface MMInterface extends ObserverInterface {
	public LinkedList<Double[]> getData();
	public void notifyObservers();	
}
