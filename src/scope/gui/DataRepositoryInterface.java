package scope.gui;

import java.util.*;

public interface DataRepositoryInterface<E> {
	/* adds data array from serial port without time stamps */
	void addDataSetArray(E array);

	/* returns all data set arrays in a LinkedList */
	LinkedList<E> getDataSetArrays();
	
	/* clears all recorded data of the indexed data set,
	 * further data for that data set will be recorded */
	void clearDataSet(int dataSetIndex);
	
	/* clears all recorded data of the indexed data set,
	 * data for that data set will no longer be recorded */
	void removeDataSet(int dataSetIndex);
	
	/* get last length of the array that was added with addDataSetArray */
	int getDataSetArrayLength();
	
	/* get count of data sets that are actually recorded */
	int getDataSetCount();
}
