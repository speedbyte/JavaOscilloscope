package scope.gui;

import scope.graphic.SerialReaderInterface;

public interface ViewInterface {
	void notifyDataChange();
	void initView(int datasetCount);
	void setModel(MMInterface model);
	void setReader(SerialReaderInterface reader);
	String getTextID();
}
