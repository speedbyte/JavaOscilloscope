package scope.gui;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.jfree.ui.RefineryUtilities;

public class TestGUI {

	private static class Rand {
		private double x = 0;
		private double lastY = 100;
		private int incdec = 1;

		public double getYRand() {
//			 final double factor = (0.90 + 0.2 * Math.random());
//			 lastY = lastY * factor;
			 double randNum = 200 * Math.random();
			 return randNum;
		}
		public double getYWave() {
			if (lastY <= 0)
				incdec = 1;
			if (lastY >= 200)
				incdec = -1;
			return lastY = lastY + incdec;
		}

		public double getX() {
			x = x + 0.02;
			return x;
		}
	}

	public static void main(String[] args) {

		// init mediator
		final MMInterface mm = new ModelMediator();
		
		ViewInterface XYSeriesChart = new View();
		XYSeriesChart.initView(8);
		XYSeriesChart.setModel(mm);
		

		// init random data source
		final Rand rand1 = new Rand();

		// Thread sending Data
		Timer timerData = new Timer();
		timerData.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				double[] data = { rand1.getX(),
						rand1.getYRand(),
						rand1.getYWave(),
						rand1.getYWave()+rand1.getYRand(),
						rand1.getYRand()
						};
				System.out.println(data[0] + ", " + data[1]);
				mm.pushDataArray(data);
//				System.out.println("data array pushed");
			}
		}, 0, 20);

//		 //Thread calling frequently observers to update, determines refresh rate
//		Timer timerNotify = new Timer();
//		timerNotify.scheduleAtFixedRate(new TimerTask() {
//			@Override
//			public void run() {
//				mm.notifyObservers();
//			}
//		}, 0, 20);
	}
}
