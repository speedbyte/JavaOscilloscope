package gnu.io;

import java.util.EventListener;

public abstract interface SerialPortEventListener
  extends EventListener
{
  public abstract void serialEvent(SerialPortEvent paramSerialPortEvent);
}


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.SerialPortEventListener
 * JD-Core Version:    0.7.0.1
 */