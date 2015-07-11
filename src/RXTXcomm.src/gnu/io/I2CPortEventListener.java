package gnu.io;

import java.util.EventListener;

public abstract interface I2CPortEventListener
  extends EventListener
{
  public abstract void I2CEvent(I2CPortEvent paramI2CPortEvent);
}


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.I2CPortEventListener
 * JD-Core Version:    0.7.0.1
 */