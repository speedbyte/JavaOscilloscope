package scope.gui;

import java.util.*;

public class DataRepository<E> implements DataRepositoryInterface<E>{
	int dataSetArrayLength = 0;
	int dataSetCount = 0;
	Ringbuffer<E> ringbuffer = new Ringbuffer<>(100);

	@Override
	public void addDataSetArray(E dataSetArray) {
		System.out.println("write:	"+dataSetArray);
		ringbuffer.addItem(dataSetArray);
	}

	@Override
	public LinkedList<E> getDataSetArrays() {
		LinkedList<E> list = new LinkedList<>();
		E item;
		while ((item = ringbuffer.getItem()) != null){
			System.out.println("read:	"+item);
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
