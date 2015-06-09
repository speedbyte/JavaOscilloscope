package scope.gui;

import scope.graphic.SerialReaderInterface;

public interface ViewInterface {
	public void notifyDataChange();
	public void initView(int datasetCount);
	void setModel(MMInterface model);
	void setReader(SerialReaderInterface reader);
}
