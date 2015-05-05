package scope.gui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.swing.SwingUtilities;

public class TestGUI {

	private static class Rand {
		private double x = 0;
		private double lastY = 100;

		public double getY() {
			// final double factor = (0.90 + 0.2 * Math.random());
			// lastY = lastY * factor;
			lastY = (int) (200 * Math.random());
			return lastY;
		}

		public double getX() {
			return x++;
		}
	}

	private static Thread getDataPushThread(final ModelMediator model, final UUID uuid) {
		final Rand rand = new Rand();
		Thread dataPushThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					double data[] = { rand.getX(), rand.getY() };
					model.pushUpdate(uuid, data);
				}
			}
		};
		return dataPushThread;
	}
	
	public static void main (String[] args) {


		// init mediator
		final ModelMediator mm = new ModelMediator();

		// init demo GUI
		final DynamicDataDemo demo = new DynamicDataDemo("Dynamic Data Demo");
		demo.setModel(mm);

		// init random data source
		final Rand rand1 = new Rand();
		final Rand rand2 = new Rand();
		
//		final TestGUI rand1 = new TestGUI();
//		final TestGUI rand2 = new TestGUI();
		
		// init identifiers for data series
		final UUID uuid1 = UUID.randomUUID();
		final UUID uuid2 = UUID.randomUUID();
		
//		Thread t = getDataPushThread(mm, uuid1);
//		t.start();
//		try {
//			t.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		ArrayList<Thread> threadList = new ArrayList<>();
//		for (int i = 0; i < 1; i++)
//			threadList.add(getDataPushThread(mm, uuid1));
//		
//		for (Thread thread : threadList){
//			thread.start();
//		}
//		
//		for (Thread thread : threadList){
//			try {
//				thread.sleep(250);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				double data[] = { rand1.getX(), rand1.getY() };
				mm.pushUpdate(uuid1, data);
			}
		}, 500, 20);
	}
}
