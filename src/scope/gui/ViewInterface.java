package scope.gui;

import scope.graphic.DataReaderInterface;

public interface ViewInterface {
	void notifyDataChange();
	void initDatasetCapacity(int datasetCount);
	void setModel(MMInterface model);
	void setReader(DataReaderInterface reader);
	String getTextID();
}
