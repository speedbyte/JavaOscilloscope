package scope.graphic;

import scope.gui.*;


public class TestOscilloscope {
	
	public static void main(String[] args) {
		
		DataReader reader = new DataReader();
		MMInterface mm = new ModelMediator();
		ViewInterface view = new View();
		
		view.setReader(reader);
		view.setModel(mm);
		reader.setView(view);
		reader.setModel(mm);
	}
}
