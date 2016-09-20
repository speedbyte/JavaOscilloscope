package scope.test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import scope.data.SQLController;
import scope.gui.*;

import org.junit.Test;
/**
 * Test for the {@link DataRepository} class
 * @author Philipp
 *
 */
public class TestDataRepository {

	/**
	 * Inserts 2 integer arrays into a {@link scope.gui.DataRepository}, extracts them and compares both list containing
	 * the arrays
	 */
	@Test
	public void testRepository() {
		DataRepository<int[]> dr = new DataRepository<int[]>();
		
		int[] values = {1, 2};
		int[] values2 = {15, 16};
		
		LinkedList<int[]> list = new LinkedList<int[]>();
		list.add(values);list.add(values2);
		dr.addDataSetArray(values);dr.addDataSetArray(values2);
		
		LinkedList<int[]> result = new LinkedList<int[]>();
		result = dr.getDataSetArrays();
		assertEquals(list, result);
	}

}

