package gnu.io;

import java.util.EventListener;

public abstract interface CommPortOwnershipListener
  extends EventListener
{
  public static final int PORT_OWNED = 1;
  public static final int PORT_UNOWNED = 2;
  public static final int PORT_OWNERSHIP_REQUESTED = 3;
  
  public abstract void ownershipChange(int paramInt);
}


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommPortOwnershipListener
 * JD-Core Version:    0.7.0.1
 */