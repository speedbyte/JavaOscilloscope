package gnu.io;

import java.util.EventListener;

public abstract interface ParallelPortEventListener
  extends EventListener
{
  public abstract void parallelEvent(ParallelPortEvent paramParallelPortEvent);
}


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.ParallelPortEventListener
 * JD-Core Version:    0.7.0.1
 */