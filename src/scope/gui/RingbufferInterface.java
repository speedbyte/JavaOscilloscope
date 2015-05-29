package scope.gui;

import java.util.UUID;

public interface RingbufferInterface {
	void pushUpdate (UUID uuid, double[] data);
	void setCapacity(int length);
}
