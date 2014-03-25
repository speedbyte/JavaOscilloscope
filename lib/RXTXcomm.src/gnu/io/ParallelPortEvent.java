/*  1:   */ package gnu.io;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ 
/*  5:   */ public class ParallelPortEvent
/*  6:   */   extends EventObject
/*  7:   */ {
/*  8:   */   public static final int PAR_EV_ERROR = 1;
/*  9:   */   public static final int PAR_EV_BUFFER = 2;
/* 10:   */   private boolean OldValue;
/* 11:   */   private boolean NewValue;
/* 12:   */   private int eventType;
/* 13:   */   
/* 14:   */   public ParallelPortEvent(ParallelPort paramParallelPort, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/* 15:   */   {
/* 16:81 */     super(paramParallelPort);
/* 17:82 */     this.OldValue = paramBoolean1;
/* 18:83 */     this.NewValue = paramBoolean2;
/* 19:84 */     this.eventType = paramInt;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public int getEventType()
/* 23:   */   {
/* 24:88 */     return this.eventType;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public boolean getNewValue()
/* 28:   */   {
/* 29:92 */     return this.NewValue;
/* 30:   */   }
/* 31:   */   
/* 32:   */   public boolean getOldValue()
/* 33:   */   {
/* 34:96 */     return this.OldValue;
/* 35:   */   }
/* 36:   */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.ParallelPortEvent
 * JD-Core Version:    0.7.0.1
 */