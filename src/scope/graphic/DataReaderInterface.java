package scope.graphic;

import java.io.File;

import scope.gui.MMInterface;
import scope.gui.ViewInterface;

public interface DataReaderInterface {
	void startReading();
	void stopReading();
	
	void setReadBluetooth();
	void setReadZigbee();
	void setImportFile(File selectedLogFile);
	
	void terminateReader();
	
	void setView(ViewInterface view);
	void setModel(MMInterface model);
}
