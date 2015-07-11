package gnu.io;

import java.util.EventListener;

public abstract interface RawPortEventListener
  extends EventListener
{
  public abstract void RawEvent(RawPortEvent paramRawPortEvent);
}


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.RawPortEventListener
 * JD-Core Version:    0.7.0.1
 */