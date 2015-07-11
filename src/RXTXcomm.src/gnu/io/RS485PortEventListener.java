package gnu.io;

import java.util.EventListener;

public abstract interface RS485PortEventListener
  extends EventListener
{
  public abstract void RS485Event(RS485PortEvent paramRS485PortEvent);
}


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.RS485PortEventListener
 * JD-Core Version:    0.7.0.1
 */