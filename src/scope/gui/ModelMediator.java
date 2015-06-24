package scope.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;


public class ModelMediator implements MMInterface {

	private List<ViewInterface> observerList = new ArrayList<ViewInterface>();
	private DataRepository<double[]> repository = new DataRepository<double[]>();
	

	@Override
	public void registerObserver(ViewInterface observer) {
		observerList.add(observer);
	}

	@Override
	public void removeObserver(ViewInterface observer) {
		observerList.remove(observer);
	}
	
	//Ringbuffer test
	@Override
	public  LinkedList<double[]> getData() {
		return repository.getDataSetArrays();
	}
	
	@Override
	public void notifyObservers() {
		for (ViewInterface observer : observerList) {
			observer.notifyDataChange();
		}
	}
	
	//Rinbuffer test
	@Override
	public void pushDataArray(double[] data) {
		repository.addDataSetArray(data);
		notifyObservers();
	}
}
