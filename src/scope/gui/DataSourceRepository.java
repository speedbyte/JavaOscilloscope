package scope.gui;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataSourceRepository {
	private ConcurrentHashMap<UUID, ConcurrentLinkedQueue> dataSourceMap = new ConcurrentHashMap<>();

	public boolean containsKey(UUID uuid) {
		return dataSourceMap.containsKey(uuid);
	}

	public void createSerie(UUID uuid) {
		ConcurrentLinkedQueue<Object> shiftRegisterQueue = new ConcurrentLinkedQueue<>();
		for (int i = 0; i < 50; i++)
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
