package scope.gui;

public interface ObserverInterface {
	public void registerObserver(ViewInterface observer);
	public void removeObserver(ViewInterface observer);
}
