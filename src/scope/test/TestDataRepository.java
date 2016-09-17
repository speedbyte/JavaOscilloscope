package scope.test;

import static org.junit.Assert.*;

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
	 * Inserts 6 values into a {@link scope.gui.DataRepository}, extracts them and compares both arrays
	 */
	@Test
	public void testIndex() {
		DataRepository<Integer> dr = new DataRepository<Integer>();
		int[] values = {1, 2, 2, 15, 16, 17};
		for (int i = 0; i < values.length; i++) {
			dr.addDataSetArray(values[i]);
		}
		int[] extractedValues = new int[6];
		for (int i = 0; i < values.length; i++) {
			extractedValues[i] = dr.getDataSetArrays().get(i); 
		}
		assertSame(values, extractedValues);
	}

}

