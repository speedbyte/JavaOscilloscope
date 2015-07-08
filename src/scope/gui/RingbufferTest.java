package scope.gui;

import java.util.Timer;
import java.util.TimerTask;

public class RingbufferTest {

	public static void main(String[] args) {
		Ringbuffer<double[]> buffer = new Ringbuffer<>(10);
		double[] array = {(double) 1, (double) 2, (double) 3, (double) 4, (double) 5};
//		buffer.addItem(new Double[] {(double) 1, 2, 3, 4, 5});
		
//		final Ringbuffer<Integer> ringbuffer = new Ringbuffer<>(10);
//		System.out.println(ringbuffer);
//		
//		Timer writeThread = new Timer();
//		writeThread.scheduleAtFixedRate(new TimerTask() {
//			int i = 0;
//			@Override
//			public void run() {
////				System.out.println("Write: "+i+"	"+ringbuffer);
//				ringbuffer.addItem(i++);
//			};
//		}, 500, 1);
//		
//		Timer writeThread2 = new Timer();
//		writeThread2.scheduleAtFixedRate(new TimerTask() {
//			int i = 0;
//			@Override
//			public void run() {
////				System.out.println("Write2: "+i+"	"+ringbuffer);
//				ringbuffer.addItem(i++);
//			};
//		}, 500, 1);
//
//		Timer readThread = new Timer();
//		readThread.scheduleAtFixedRate(new TimerTask() {
//			@Override
//			public void run() {
//				System.out.println("Read: "+ringbuffer.getItem()+"	"+ringbuffer);
//			}
//		}, 1000, 1);
//		
////		for (int i = 0; i < 100; i++){
////			ringbuffer.addItem(i);
////			System.out.println(ringbuffer);			
////		}
//
	}
}
