package scope.graphic;

import java.io.File;

public interface SerialReaderInterface {
	void startReading();
	void stopReading();
	
	void setReadBluetooth();
	void setReadZigbee();
	void setImportFile(File selectedLogFile);
	
	void terminateReader();
}
