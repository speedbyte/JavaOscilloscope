package scope.gui;

import java.util.*;

public class DataRepository implements DataRepositoryInterface{
	int dataSetArrayLength = 0;
	int dataSetCount = 0;
	Ringbuffer<Double[]> ringbuffer = new Ringbuffer<>(1000);

	@Override
	public void addDataSetArray(Double[] dataSetArray) {
		this.ringbuffer.addItem(dataSetArray);
	}

	@Override
	public LinkedList<Double[]> getDataSetArrays() {
		LinkedList<Double[]> list = new LinkedList<>();
		list.add(ringbuffer.getItem());
		Double[] item;
		while ((item = ringbuffer.getItem()) != null){
			list.add(item);
		}
		return list;
	}

	@Override
	public void clearDataSet(int dataSetIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDataSet(int dataSetIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getDataSetArrayLenth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDataSetCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
