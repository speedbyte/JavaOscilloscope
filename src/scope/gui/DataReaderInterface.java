package scope.gui;

import java.io.File;

public interface DataReaderInterface {
	void startReading();
	void stopReading();
	
	void setReadUdp();
	void setImportFile(File selectedLogFile);
	
	void terminateReader();
	
	void setView(ViewInterface view);
	void setModel(MMInterface model);
}
