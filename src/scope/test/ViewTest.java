package scope.test;

import static org.junit.Assert.*;
import scope.gui.*;

import org.junit.Test;

public class ViewTest {

	@Test
	public void testConfig() {
		View view = new View();
		int value1 = view.getComponentCount();
		view.applyConfig();
		int value2 = view.getComponentCount();
		System.out.println("Ergebnis: " + value1 + " " + value2);
		assertSame(value1, value2);
	}

}
