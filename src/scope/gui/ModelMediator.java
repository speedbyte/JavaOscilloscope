package scope.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages {@link DataRepository} and its observers
 */

public class ModelMediator implements MMInterface {
	/**
	 * List of observers that are notified on data change
	 */
	private List<ViewInterface> observerList = new ArrayList<ViewInterface>();
	/**
	 * Respository containing data
	 */
	private DataRepository<double[]> repository = new DataRepository<double[]>();
	
	/**
	 * Adds an observer to the {@link observerList}
	 */
	@Override
	public void registerObserver(ViewInterface observer) {
		observerList.add(observer);
	}

	/**
	 * Removes an observer from the {@link observerList}
	 */
	@Override
	public void removeObserver(ViewInterface observer) {
		observerList.remove(observer);
	}
	
	/**
	 * @return List of double arrays
	 */
	@Override
	public  LinkedList<double[]> getData() {
		return repository.getDataSetArrays();
	}
	
	/**
	 * Notifies observers
	 */
	@Override
	public void notifyObservers() {
		for (ViewInterface observer : observerList) {
			observer.notifyDataChange();
		}
	}
	
	/**
	 * Adds data to the {@link DataRepository}
	 */
	@Override
	public void pushDataArray(double[] data) {
		repository.addDataSetArray(data);
		notifyObservers();
	}
}
