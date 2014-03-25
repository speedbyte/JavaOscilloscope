/*  1:   */ package gnu.io;
/*  2:   */ 
/*  3:   */ public class PortInUseException
/*  4:   */   extends Exception
/*  5:   */ {
/*  6:   */   public String currentOwner;
/*  7:   */   
/*  8:   */   PortInUseException(String paramString)
/*  9:   */   {
/* 10:82 */     super(paramString);
/* 11:83 */     this.currentOwner = paramString;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public PortInUseException() {}
/* 15:   */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.PortInUseException
 * JD-Core Version:    0.7.0.1
 */