package scope.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;


public class ModelMediator implements MMInterface {

	private List<ViewInterface> observerList = new ArrayList<>();
	private DataRepository<long[]> repository = new DataRepository<>();
	

	@Override
	public void registerObserver(ViewInterface observer) {
		observerList.add(observer);
	}

	@Override
	public void removeObserver(ViewInterface observer) {
		observerList.remove(observer);
	}
	
	@Override
	public  LinkedList<long[]> getData() {
		return repository.getDataSetArrays();
	}
	
	@Override
	public void notifyObservers() {
		for (ViewInterface observer : observerList) {
			observer.notifyDataChange();
		}
	}
	
	@Override
	public void pushDataArray(long[] data) {
		repository.addDataSetArray(data);
		notifyObservers();
		System.out.println("View notified");
	}
}
