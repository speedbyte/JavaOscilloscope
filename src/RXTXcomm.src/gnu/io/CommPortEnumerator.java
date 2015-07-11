/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ 
/*   5:    */ class CommPortEnumerator
/*   6:    */   implements Enumeration
/*   7:    */ {
/*   8:    */   private CommPortIdentifier index;
/*   9:    */   private static final boolean debug = false;
/*  10:    */   
/*  11:    */   public Object nextElement()
/*  12:    */   {
/*  13: 93 */     synchronized (CommPortIdentifier.Sync)
/*  14:    */     {
/*  15: 95 */       if (this.index != null) {
/*  16: 95 */         this.index = this.index.next;
/*  17:    */       } else {
/*  18: 96 */         this.index = CommPortIdentifier.CommPortIndex;
/*  19:    */       }
/*  20: 97 */       return this.index;
/*  21:    */     }
/*  22:    */   }
/*  23:    */   
/*  24:    */   public boolean hasMoreElements()
/*  25:    */   {
/*  26:111 */     synchronized (CommPortIdentifier.Sync)
/*  27:    */     {
/*  28:113 */       if (this.index != null) {
/*  29:113 */         return this.index.next != null;
/*  30:    */       }
/*  31:114 */       return CommPortIdentifier.CommPortIndex != null;
/*  32:    */     }
/*  33:    */   }
/*  34:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommPortEnumerator
 * JD-Core Version:    0.7.0.1
 */