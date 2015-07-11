/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ 
/*   5:    */ public class SerialPortEvent
/*   6:    */   extends EventObject
/*   7:    */ {
/*   8:    */   public static final int DATA_AVAILABLE = 1;
/*   9:    */   public static final int OUTPUT_BUFFER_EMPTY = 2;
/*  10:    */   public static final int CTS = 3;
/*  11:    */   public static final int DSR = 4;
/*  12:    */   public static final int RI = 5;
/*  13:    */   public static final int CD = 6;
/*  14:    */   public static final int OE = 7;
/*  15:    */   public static final int PE = 8;
/*  16:    */   public static final int FE = 9;
/*  17:    */   public static final int BI = 10;
/*  18:    */   private boolean OldValue;
/*  19:    */   private boolean NewValue;
/*  20:    */   private int eventType;
/*  21:    */   
/*  22:    */   public SerialPortEvent(SerialPort paramSerialPort, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*  23:    */   {
/*  24: 87 */     super(paramSerialPort);
/*  25: 88 */     this.OldValue = paramBoolean1;
/*  26: 89 */     this.NewValue = paramBoolean2;
/*  27: 90 */     this.eventType = paramInt;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public int getEventType()
/*  31:    */   {
/*  32: 94 */     return this.eventType;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean getNewValue()
/*  36:    */   {
/*  37: 98 */     return this.NewValue;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean getOldValue()
/*  41:    */   {
/*  42:102 */     return this.OldValue;
/*  43:    */   }
/*  44:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.SerialPortEvent
 * JD-Core Version:    0.7.0.1
 */