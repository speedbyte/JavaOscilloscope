package gnu.io;

public abstract interface CommDriver
{
  public abstract CommPort getCommPort(String paramString, int paramInt);
  
  public abstract void initialize();
}


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommDriver
 * JD-Core Version:    0.7.0.1
 */