package scope.gui;

import java.util.UUID;


public interface DataSourceInteface {
	public void pushUpdate (UUID uuid, double[] data);
}
