package scope.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class XYPlotter implements Runnable {
	public void run() {
		JFrame frame = new JFrame("My JFrame Example");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(400, 200));
		frame.pack();
		frame.setVisible(true);
	}
}
