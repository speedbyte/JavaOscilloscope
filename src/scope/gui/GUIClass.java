package scope.gui;

import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.jfree.ui.RefineryUtilities;

import scope.data.SQL;
import scope.udp.UdpJava;

public class GUIClass {

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
		//Prevents NumberAxis to show on initial startup using config file
		//XYSeriesChart.initDatasetCapacity(8);
		XYSeriesChart.setModel(mm);
		/*
		final UdpJava udpjava = new UdpJava();
		try{
			udpjava.startServer();
		} catch (Exception e){
			e.printStackTrace();
		}
		*/
		// init random data source
		final Rand rand1 = new Rand();
		// Thread sending Data
		Timer timerData = new Timer();
		timerData.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				double[] data;
				try {
					double[] data1 = {rand1.getX()};
					data = data1;
					
					if(SQL.resultSet == null){
						System.out.println("Rs is null");
					}
					
					if(SQL.resultSet != null && SQL.resultSet.next()){
						double[] data2 = {
								SQL.resultSet.getDouble("ACC_X"),
								SQL.resultSet.getDouble("ACC_Y"),
								SQL.resultSet.getDouble("ACC_Z")
						};
						data = concat(data1, data2);
						System.out.println("ACC_X: " + SQL.resultSet.getDouble("ACC_X") + " ACC_Y: " + SQL.resultSet.getDouble("ACC_Y") + " ACC_Z: " + SQL.resultSet.getDouble("ACC_Z"));
					}
					
					mm.pushDataArray(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	
	public static double[] concat(double[] a, double[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   double[] c= new double[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}
}
