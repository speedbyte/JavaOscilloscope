package scope.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ModelMediator implements MMInterface, DataSourceInteface {

	private List<ViewInterface> observerList = new ArrayList<>();
	// private List<DataSourceInterface> dataSourceList = new ArrayList<>();
	private DataSourceRepository repository = new DataSourceRepository();

	// temporary uuid variable
	private UUID tempID;

	@Override
	public void registerObserver(ViewInterface observer) {
		observerList.add(observer);
	}

	@Override
	public void removeObserver(ViewInterface observer) {
		observerList.remove(observer);
	}

	@Override
	public LinkedList<double[]> getData() {
		return repository.pollData(tempID);
	}
	
	@Override
	public void notifyObservers() {
//		if (repository.peekData(tempID).size() > 0)
			for (ViewInterface observer : observerList) {
				observer.notifyDataChange();
			}
	}

	@Override
	public void pushUpdate(UUID uuid, double[] data) {
		if (!repository.containsKey(uuid)) {
			repository.createSerie(uuid);
			tempID = uuid;
		}
		repository.addData(uuid, data);
		int bufferQueueLength = repository.peekData(uuid).size();
		if (bufferQueueLength > 0)
			System.out.println("Data sub queue size: " + bufferQueueLength);
		if (repository.peekData(uuid).size() > 200){
			synchronized ("pop"){
				repository.peekData(uuid).pop();
			}
		}
//		notifyObservers();
	}
}
