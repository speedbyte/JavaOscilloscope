/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.util.TooManyListenersException;
/*   8:    */ 
/*   9:    */ final class LPRPort
/*  10:    */   extends ParallelPort
/*  11:    */ {
/*  12:    */   private static final boolean debug = false;
/*  13:    */   private int fd;
/*  14:    */   
/*  15:    */   static
/*  16:    */   {
/*  17: 74 */     System.loadLibrary("rxtxParallel");
/*  18: 75 */     Initialize();
/*  19:    */   }
/*  20:    */   
/*  21:    */   public LPRPort(String paramString)
/*  22:    */     throws PortInUseException
/*  23:    */   {
/*  24: 98 */     this.fd = open(paramString);
/*  25: 99 */     this.name = paramString;
/*  26:    */   }
/*  27:    */   
/*  28:112 */   private final ParallelOutputStream out = new ParallelOutputStream();
/*  29:    */   
/*  30:    */   public OutputStream getOutputStream()
/*  31:    */   {
/*  32:113 */     return this.out;
/*  33:    */   }
/*  34:    */   
/*  35:116 */   private final ParallelInputStream in = new ParallelInputStream();
/*  36:    */   
/*  37:    */   public InputStream getInputStream()
/*  38:    */   {
/*  39:117 */     return this.in;
/*  40:    */   }
/*  41:    */   
/*  42:121 */   private int lprmode = 0;
/*  43:    */   
/*  44:    */   public int getMode()
/*  45:    */   {
/*  46:122 */     return this.lprmode;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public int setMode(int paramInt)
/*  50:    */     throws UnsupportedCommOperationException
/*  51:    */   {
/*  52:    */     try
/*  53:    */     {
/*  54:126 */       setLPRMode(paramInt);
/*  55:    */     }
/*  56:    */     catch (UnsupportedCommOperationException localUnsupportedCommOperationException)
/*  57:    */     {
/*  58:128 */       localUnsupportedCommOperationException.printStackTrace();
/*  59:129 */       return -1;
/*  60:    */     }
/*  61:131 */     this.lprmode = paramInt;
/*  62:132 */     return 0;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void restart()
/*  66:    */   {
/*  67:136 */     System.out.println("restart() is not implemented");
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void suspend()
/*  71:    */   {
/*  72:140 */     System.out.println("suspend() is not implemented");
/*  73:    */   }
/*  74:    */   
/*  75:    */   public synchronized void close()
/*  76:    */   {
/*  77:155 */     if (this.fd < 0) {
/*  78:155 */       return;
/*  79:    */     }
/*  80:156 */     nativeClose();
/*  81:157 */     super.close();
/*  82:158 */     removeEventListener();
/*  83:    */     
/*  84:160 */     this.fd = 0;
/*  85:161 */     Runtime.getRuntime().gc();
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void enableReceiveFraming(int paramInt)
/*  89:    */     throws UnsupportedCommOperationException
/*  90:    */   {
/*  91:167 */     throw new UnsupportedCommOperationException("Not supported");
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean isReceiveFramingEnabled()
/*  95:    */   {
/*  96:170 */     return false;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public int getReceiveFramingByte()
/* 100:    */   {
/* 101:171 */     return 0;
/* 102:    */   }
/* 103:    */   
/* 104:174 */   private int timeout = 0;
/* 105:    */   
/* 106:    */   public void enableReceiveTimeout(int paramInt)
/* 107:    */   {
/* 108:177 */     if (paramInt > 0) {
/* 109:177 */       this.timeout = paramInt;
/* 110:    */     } else {
/* 111:178 */       this.timeout = 0;
/* 112:    */     }
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void disableReceiveTimeout()
/* 116:    */   {
/* 117:180 */     this.timeout = 0;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public boolean isReceiveTimeoutEnabled()
/* 121:    */   {
/* 122:181 */     return this.timeout > 0;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public int getReceiveTimeout()
/* 126:    */   {
/* 127:182 */     return this.timeout;
/* 128:    */   }
/* 129:    */   
/* 130:185 */   private int threshold = 1;
/* 131:    */   private ParallelPortEventListener PPEventListener;
/* 132:    */   private MonitorThread monThread;
/* 133:    */   
/* 134:    */   public void enableReceiveThreshold(int paramInt)
/* 135:    */   {
/* 136:188 */     if (paramInt > 1) {
/* 137:188 */       this.threshold = paramInt;
/* 138:    */     } else {
/* 139:189 */       this.threshold = 1;
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void disableReceiveThreshold()
/* 144:    */   {
/* 145:191 */     this.threshold = 1;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public int getReceiveThreshold()
/* 149:    */   {
/* 150:192 */     return this.threshold;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public boolean isReceiveThresholdEnabled()
/* 154:    */   {
/* 155:193 */     return this.threshold > 1;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public boolean checkMonitorThread()
/* 159:    */   {
/* 160:229 */     if (this.monThread != null) {
/* 161:230 */       return this.monThread.isInterrupted();
/* 162:    */     }
/* 163:231 */     return true;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public synchronized boolean sendEvent(int paramInt, boolean paramBoolean)
/* 167:    */   {
/* 168:238 */     if ((this.fd == 0) || (this.PPEventListener == null) || (this.monThread == null)) {
/* 169:240 */       return true;
/* 170:    */     }
/* 171:243 */     switch (paramInt)
/* 172:    */     {
/* 173:    */     case 2: 
/* 174:246 */       if (!this.monThread.monBuffer) {
/* 175:247 */         return false;
/* 176:    */       }
/* 177:    */       break;
/* 178:    */     case 1: 
/* 179:249 */       if (!this.monThread.monError) {
/* 180:250 */         return false;
/* 181:    */       }
/* 182:    */       break;
/* 183:    */     default: 
/* 184:252 */       System.err.println("unknown event:" + paramInt);
/* 185:253 */       return false;
/* 186:    */     }
/* 187:255 */     ParallelPortEvent localParallelPortEvent = new ParallelPortEvent(this, paramInt, !paramBoolean, paramBoolean);
/* 188:257 */     if (this.PPEventListener != null) {
/* 189:258 */       this.PPEventListener.parallelEvent(localParallelPortEvent);
/* 190:    */     }
/* 191:259 */     if ((this.fd == 0) || (this.PPEventListener == null) || (this.monThread == null)) {
/* 192:261 */       return true;
/* 193:    */     }
/* 194:    */     try
/* 195:    */     {
/* 196:265 */       Thread.sleep(50L);
/* 197:    */     }
/* 198:    */     catch (Exception localException) {}
/* 199:266 */     return false;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public synchronized void addEventListener(ParallelPortEventListener paramParallelPortEventListener)
/* 203:    */     throws TooManyListenersException
/* 204:    */   {
/* 205:275 */     if (this.PPEventListener != null) {
/* 206:276 */       throw new TooManyListenersException();
/* 207:    */     }
/* 208:277 */     this.PPEventListener = paramParallelPortEventListener;
/* 209:278 */     this.monThread = new MonitorThread();
/* 210:279 */     this.monThread.start();
/* 211:    */   }
/* 212:    */   
/* 213:    */   public synchronized void removeEventListener()
/* 214:    */   {
/* 215:285 */     this.PPEventListener = null;
/* 216:286 */     if (this.monThread != null)
/* 217:    */     {
/* 218:288 */       this.monThread.interrupt();
/* 219:289 */       this.monThread = null;
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   public synchronized void notifyOnError(boolean paramBoolean)
/* 224:    */   {
/* 225:298 */     System.out.println("notifyOnError is not implemented yet");
/* 226:299 */     this.monThread.monError = paramBoolean;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public synchronized void notifyOnBuffer(boolean paramBoolean)
/* 230:    */   {
/* 231:303 */     System.out.println("notifyOnBuffer is not implemented yet");
/* 232:304 */     this.monThread.monBuffer = paramBoolean;
/* 233:    */   }
/* 234:    */   
/* 235:    */   protected void finalize()
/* 236:    */   {
/* 237:311 */     if (this.fd > 0) {
/* 238:311 */       close();
/* 239:    */     }
/* 240:    */   }
/* 241:    */   
/* 242:    */   private static native void Initialize();
/* 243:    */   
/* 244:    */   private synchronized native int open(String paramString)
/* 245:    */     throws PortInUseException;
/* 246:    */   
/* 247:    */   public native boolean setLPRMode(int paramInt)
/* 248:    */     throws UnsupportedCommOperationException;
/* 249:    */   
/* 250:    */   public native boolean isPaperOut();
/* 251:    */   
/* 252:    */   public native boolean isPrinterBusy();
/* 253:    */   
/* 254:    */   public native boolean isPrinterError();
/* 255:    */   
/* 256:    */   public native boolean isPrinterSelected();
/* 257:    */   
/* 258:    */   public native boolean isPrinterTimedOut();
/* 259:    */   
/* 260:    */   private native void nativeClose();
/* 261:    */   
/* 262:    */   public void disableReceiveFraming() {}
/* 263:    */   
/* 264:    */   public native void setInputBufferSize(int paramInt);
/* 265:    */   
/* 266:    */   public native int getInputBufferSize();
/* 267:    */   
/* 268:    */   public native void setOutputBufferSize(int paramInt);
/* 269:    */   
/* 270:    */   public native int getOutputBufferSize();
/* 271:    */   
/* 272:    */   public native int getOutputBufferFree();
/* 273:    */   
/* 274:    */   protected native void writeByte(int paramInt)
/* 275:    */     throws IOException;
/* 276:    */   
/* 277:    */   protected native void writeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 278:    */     throws IOException;
/* 279:    */   
/* 280:    */   protected native void drain()
/* 281:    */     throws IOException;
/* 282:    */   
/* 283:    */   protected native int nativeavailable()
/* 284:    */     throws IOException;
/* 285:    */   
/* 286:    */   protected native int readByte()
/* 287:    */     throws IOException;
/* 288:    */   
/* 289:    */   protected native int readArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 290:    */     throws IOException;
/* 291:    */   
/* 292:    */   native void eventLoop();
/* 293:    */   
/* 294:    */   class ParallelOutputStream
/* 295:    */     extends OutputStream
/* 296:    */   {
/* 297:    */     ParallelOutputStream() {}
/* 298:    */     
/* 299:    */     public synchronized void write(int paramInt)
/* 300:    */       throws IOException
/* 301:    */     {
/* 302:319 */       if (LPRPort.this.fd == 0) {
/* 303:319 */         throw new IOException();
/* 304:    */       }
/* 305:320 */       LPRPort.this.writeByte(paramInt);
/* 306:    */     }
/* 307:    */     
/* 308:    */     public synchronized void write(byte[] paramArrayOfByte)
/* 309:    */       throws IOException
/* 310:    */     {
/* 311:324 */       if (LPRPort.this.fd == 0) {
/* 312:324 */         throw new IOException();
/* 313:    */       }
/* 314:325 */       LPRPort.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/* 315:    */     }
/* 316:    */     
/* 317:    */     public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 318:    */       throws IOException
/* 319:    */     {
/* 320:330 */       if (LPRPort.this.fd == 0) {
/* 321:330 */         throw new IOException();
/* 322:    */       }
/* 323:331 */       LPRPort.this.writeArray(paramArrayOfByte, paramInt1, paramInt2);
/* 324:    */     }
/* 325:    */     
/* 326:    */     public synchronized void flush()
/* 327:    */       throws IOException
/* 328:    */     {
/* 329:335 */       if (LPRPort.this.fd == 0) {
/* 330:335 */         throw new IOException();
/* 331:    */       }
/* 332:    */     }
/* 333:    */   }
/* 334:    */   
/* 335:    */   class ParallelInputStream
/* 336:    */     extends InputStream
/* 337:    */   {
/* 338:    */     ParallelInputStream() {}
/* 339:    */     
/* 340:    */     public int read()
/* 341:    */       throws IOException
/* 342:    */     {
/* 343:345 */       if (LPRPort.this.fd == 0) {
/* 344:345 */         throw new IOException();
/* 345:    */       }
/* 346:346 */       return LPRPort.this.readByte();
/* 347:    */     }
/* 348:    */     
/* 349:    */     public int read(byte[] paramArrayOfByte)
/* 350:    */       throws IOException
/* 351:    */     {
/* 352:350 */       if (LPRPort.this.fd == 0) {
/* 353:350 */         throw new IOException();
/* 354:    */       }
/* 355:351 */       return LPRPort.this.readArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/* 356:    */     }
/* 357:    */     
/* 358:    */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 359:    */       throws IOException
/* 360:    */     {
/* 361:356 */       if (LPRPort.this.fd == 0) {
/* 362:356 */         throw new IOException();
/* 363:    */       }
/* 364:357 */       return LPRPort.this.readArray(paramArrayOfByte, paramInt1, paramInt2);
/* 365:    */     }
/* 366:    */     
/* 367:    */     public int available()
/* 368:    */       throws IOException
/* 369:    */     {
/* 370:361 */       if (LPRPort.this.fd == 0) {
/* 371:361 */         throw new IOException();
/* 372:    */       }
/* 373:362 */       return LPRPort.this.nativeavailable();
/* 374:    */     }
/* 375:    */   }
/* 376:    */   
/* 377:    */   class MonitorThread
/* 378:    */     extends Thread
/* 379:    */   {
/* 380:367 */     private boolean monError = false;
/* 381:368 */     private boolean monBuffer = false;
/* 382:    */     
/* 383:    */     MonitorThread() {}
/* 384:    */     
/* 385:    */     public void run()
/* 386:    */     {
/* 387:372 */       LPRPort.this.eventLoop();
/* 388:373 */       yield();
/* 389:    */     }
/* 390:    */   }
/* 391:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.LPRPort
 * JD-Core Version:    0.7.0.1
 */