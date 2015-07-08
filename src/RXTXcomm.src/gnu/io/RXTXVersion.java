/*  1:   */ package gnu.io;
/*  2:   */ 
/*  3:   */ public class RXTXVersion
/*  4:   */ {
/*  5:   */   static
/*  6:   */   {
/*  7:78 */     System.loadLibrary("rxtxSerial");
/*  8:   */   }
/*  9:   */   
/* 10:79 */   private static String Version = "RXTX-2.2-20081207 Cloudhopper Build rxtx.cloudhopper.net";
/* 11:   */   
/* 12:   */   public static native String nativeGetVersion();
/* 13:   */   
/* 14:   */   public static String getVersion()
/* 15:   */   {
/* 16:88 */     return Version;
/* 17:   */   }
/* 18:   */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.RXTXVersion
 * JD-Core Version:    0.7.0.1
 */