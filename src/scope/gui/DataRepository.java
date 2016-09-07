package scope.gui;

import java.util.*;

/**
 * Manages the {@link Ringbuffer}
 * @param <E> data type of the item
 */
public class DataRepository<E> implements DataRepositoryInterface<E>{
	int dataSetArrayLength = 0;
	int dataSetCount = 0;
	Ringbuffer<E> ringbuffer = new Ringbuffer<E>(100);

	/**
	 * Adds an item to the {@link Ringbuffer}
	 */
	@Override
	public void addDataSetArray(E dataSetArray) {
		ringbuffer.addItem(dataSetArray);
	}

	/**
	 * @return List of items in the {@link Ringbuffer}
	 */
	@Override
	public LinkedList<E> getDataSetArrays() {
		LinkedList<E> list = new LinkedList<E>();
		E item;
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
	public int getDataSetArrayLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDataSetCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
