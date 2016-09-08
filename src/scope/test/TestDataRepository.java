package scope.test;

import static org.junit.Assert.*;

import scope.data.SQLController;
import scope.gui.*;

import org.junit.Test;

public class TestDataRepository {

// Runs on a local machine
//	@Test
//	public void testConfig() {
//		View view = new View();
//		int value1 = view.getComponentCount();
//		view.applyConfig();
//		int value2 = view.getComponentCount();
//		System.out.println("Ergebnis: " + value1 + " " + value2);
//		assertSame(value1, value2);
//	}
	
	@Test
	public void testIndex() {
		DataRepository<Integer> dr = new DataRepository<Integer>();
		int value1 = 10;
		dr.addDataSetArray(value1);
		int value2 = dr.getDataSetArrays().get(0);
		assertSame(value1, value2);
	}

}

