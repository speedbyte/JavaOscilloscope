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

	private void notifyObservers() {
		if (repository.peekData(tempID).size() > 0)
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
		if (bufferQueueLength > 1)
			System.out.println("Data sub queue size: " + bufferQueueLength);
		notifyObservers();
	}

	private class DataSourceRepository {
		private ConcurrentHashMap<UUID, ConcurrentLinkedQueue> dataSourceMap = new ConcurrentHashMap<>();

		public boolean containsKey(UUID uuid) {
			return dataSourceMap.containsKey(uuid);
		}

		public void createSerie(UUID uuid) {
			ConcurrentLinkedQueue<Object> shiftRegisterQueue = new ConcurrentLinkedQueue<>();
			for (int i = 0; i < 2; i++)
				shiftRegisterQueue.add(new LinkedList<Object>());
			dataSourceMap.put(uuid, shiftRegisterQueue);
		}

		public void removeSerie(UUID uuid) {
			dataSourceMap.remove(uuid);
		}

		public void addData(UUID uuid, Object data) {
			LinkedList tempQ;
			synchronized (uuid) {
				tempQ = (LinkedList) dataSourceMap.get(uuid).peek();
				tempQ.add(data);
			}
		}

		public LinkedList<double[]> peekData(UUID uuid) {
			return (LinkedList<double[]>) dataSourceMap.get(uuid).peek();
		}

		public LinkedList<double[]> pollData(UUID uuid) {
			try {
				synchronized (uuid) {
					return (LinkedList<double[]>) dataSourceMap.get(uuid).poll();
				}
			} finally {
				dataSourceMap.get(uuid).add(new LinkedList<>());
			}
		}
	}
}
