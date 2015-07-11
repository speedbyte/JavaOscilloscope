/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ 
/*   7:    */ public abstract class CommPort
/*   8:    */ {
/*   9:    */   protected String name;
/*  10:    */   private static final boolean debug = false;
/*  11:    */   
/*  12:    */   public abstract void enableReceiveFraming(int paramInt)
/*  13:    */     throws UnsupportedCommOperationException;
/*  14:    */   
/*  15:    */   public abstract void disableReceiveFraming();
/*  16:    */   
/*  17:    */   public abstract boolean isReceiveFramingEnabled();
/*  18:    */   
/*  19:    */   public abstract int getReceiveFramingByte();
/*  20:    */   
/*  21:    */   public abstract void disableReceiveTimeout();
/*  22:    */   
/*  23:    */   public abstract void enableReceiveTimeout(int paramInt)
/*  24:    */     throws UnsupportedCommOperationException;
/*  25:    */   
/*  26:    */   public abstract boolean isReceiveTimeoutEnabled();
/*  27:    */   
/*  28:    */   public abstract int getReceiveTimeout();
/*  29:    */   
/*  30:    */   public abstract void enableReceiveThreshold(int paramInt)
/*  31:    */     throws UnsupportedCommOperationException;
/*  32:    */   
/*  33:    */   public abstract void disableReceiveThreshold();
/*  34:    */   
/*  35:    */   public abstract int getReceiveThreshold();
/*  36:    */   
/*  37:    */   public abstract boolean isReceiveThresholdEnabled();
/*  38:    */   
/*  39:    */   public abstract void setInputBufferSize(int paramInt);
/*  40:    */   
/*  41:    */   public abstract int getInputBufferSize();
/*  42:    */   
/*  43:    */   public abstract void setOutputBufferSize(int paramInt);
/*  44:    */   
/*  45:    */   public abstract int getOutputBufferSize();
/*  46:    */   
/*  47:    */   public void close()
/*  48:    */   {
/*  49:    */     try
/*  50:    */     {
/*  51:103 */       CommPortIdentifier localCommPortIdentifier = CommPortIdentifier.getPortIdentifier(this);
/*  52:105 */       if (localCommPortIdentifier != null) {
/*  53:106 */         CommPortIdentifier.getPortIdentifier(this).internalClosePort();
/*  54:    */       }
/*  55:    */     }
/*  56:    */     catch (NoSuchPortException localNoSuchPortException) {}
/*  57:    */   }
/*  58:    */   
/*  59:    */   public abstract InputStream getInputStream()
/*  60:    */     throws IOException;
/*  61:    */   
/*  62:    */   public abstract OutputStream getOutputStream()
/*  63:    */     throws IOException;
/*  64:    */   
/*  65:    */   public String getName()
/*  66:    */   {
/*  67:119 */     return this.name;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String toString()
/*  71:    */   {
/*  72:124 */     return this.name;
/*  73:    */   }
/*  74:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommPort
 * JD-Core Version:    0.7.0.1
 */