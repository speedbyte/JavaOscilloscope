/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.util.TooManyListenersException;
/*   8:    */ 
/*   9:    */ final class Raw
/*  10:    */   extends RawPort
/*  11:    */ {
/*  12:    */   private int ciAddress;
/*  13:    */   
/*  14:    */   static
/*  15:    */   {
/*  16: 74 */     System.loadLibrary("rxtxRaw");
/*  17: 75 */     Initialize();
/*  18:    */   }
/*  19:    */   
/*  20:    */   public Raw(String paramString)
/*  21:    */     throws PortInUseException
/*  22:    */   {
/*  23: 88 */     this.ciAddress = Integer.parseInt(paramString);
/*  24: 89 */     open(this.ciAddress);
/*  25:    */   }
/*  26:    */   
/*  27: 98 */   static boolean dsrFlag = false;
/*  28:101 */   private final RawOutputStream out = new RawOutputStream();
/*  29:    */   
/*  30:    */   public OutputStream getOutputStream()
/*  31:    */   {
/*  32:102 */     return this.out;
/*  33:    */   }
/*  34:    */   
/*  35:106 */   private final RawInputStream in = new RawInputStream();
/*  36:    */   
/*  37:    */   public InputStream getInputStream()
/*  38:    */   {
/*  39:107 */     return this.in;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setRawPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*  43:    */     throws UnsupportedCommOperationException
/*  44:    */   {
/*  45:116 */     nativeSetRawPortParams(paramInt1, paramInt2, paramInt3, paramInt4);
/*  46:117 */     this.speed = paramInt1;
/*  47:118 */     this.dataBits = paramInt2;
/*  48:119 */     this.stopBits = paramInt3;
/*  49:120 */     this.parity = paramInt4;
/*  50:    */   }
/*  51:    */   
/*  52:128 */   private int speed = 9600;
/*  53:    */   
/*  54:    */   public int getBaudRate()
/*  55:    */   {
/*  56:129 */     return this.speed;
/*  57:    */   }
/*  58:    */   
/*  59:132 */   private int dataBits = 8;
/*  60:    */   
/*  61:    */   public int getDataBits()
/*  62:    */   {
/*  63:133 */     return this.dataBits;
/*  64:    */   }
/*  65:    */   
/*  66:136 */   private int stopBits = 1;
/*  67:    */   
/*  68:    */   public int getStopBits()
/*  69:    */   {
/*  70:137 */     return this.stopBits;
/*  71:    */   }
/*  72:    */   
/*  73:140 */   private int parity = 0;
/*  74:    */   
/*  75:    */   public int getParity()
/*  76:    */   {
/*  77:141 */     return this.parity;
/*  78:    */   }
/*  79:    */   
/*  80:145 */   private int flowmode = 0;
/*  81:    */   
/*  82:    */   public void setFlowControlMode(int paramInt)
/*  83:    */   {
/*  84:    */     try
/*  85:    */     {
/*  86:147 */       setflowcontrol(paramInt);
/*  87:    */     }
/*  88:    */     catch (IOException localIOException)
/*  89:    */     {
/*  90:149 */       localIOException.printStackTrace();
/*  91:150 */       return;
/*  92:    */     }
/*  93:152 */     this.flowmode = paramInt;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public int getFlowControlMode()
/*  97:    */   {
/*  98:154 */     return this.flowmode;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void enableReceiveFraming(int paramInt)
/* 102:    */     throws UnsupportedCommOperationException
/* 103:    */   {
/* 104:167 */     throw new UnsupportedCommOperationException("Not supported");
/* 105:    */   }
/* 106:    */   
/* 107:    */   public boolean isReceiveFramingEnabled()
/* 108:    */   {
/* 109:170 */     return false;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public int getReceiveFramingByte()
/* 113:    */   {
/* 114:171 */     return 0;
/* 115:    */   }
/* 116:    */   
/* 117:175 */   private int timeout = 0;
/* 118:    */   
/* 119:    */   public void disableReceiveTimeout()
/* 120:    */   {
/* 121:181 */     enableReceiveTimeout(0);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void enableReceiveTimeout(int paramInt)
/* 125:    */   {
/* 126:184 */     if (paramInt >= 0)
/* 127:    */     {
/* 128:185 */       this.timeout = paramInt;
/* 129:186 */       NativeEnableReceiveTimeoutThreshold(paramInt, this.threshold, this.InputBuffer);
/* 130:    */     }
/* 131:    */     else
/* 132:    */     {
/* 133:189 */       System.out.println("Invalid timeout");
/* 134:    */     }
/* 135:    */   }
/* 136:    */   
/* 137:    */   public boolean isReceiveTimeoutEnabled()
/* 138:    */   {
/* 139:193 */     return NativeisReceiveTimeoutEnabled();
/* 140:    */   }
/* 141:    */   
/* 142:    */   public int getReceiveTimeout()
/* 143:    */   {
/* 144:196 */     return NativegetReceiveTimeout();
/* 145:    */   }
/* 146:    */   
/* 147:201 */   private int threshold = 0;
/* 148:    */   
/* 149:    */   public void enableReceiveThreshold(int paramInt)
/* 150:    */   {
/* 151:204 */     if (paramInt >= 0)
/* 152:    */     {
/* 153:206 */       this.threshold = paramInt;
/* 154:207 */       NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
/* 155:    */     }
/* 156:    */     else
/* 157:    */     {
/* 158:211 */       System.out.println("Invalid Threshold");
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void disableReceiveThreshold()
/* 163:    */   {
/* 164:215 */     enableReceiveThreshold(0);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public int getReceiveThreshold()
/* 168:    */   {
/* 169:218 */     return this.threshold;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public boolean isReceiveThresholdEnabled()
/* 173:    */   {
/* 174:221 */     return this.threshold > 0;
/* 175:    */   }
/* 176:    */   
/* 177:231 */   private int InputBuffer = 0;
/* 178:232 */   private int OutputBuffer = 0;
/* 179:    */   private RawPortEventListener SPEventListener;
/* 180:    */   private MonitorThread monThread;
/* 181:    */   
/* 182:    */   public void setInputBufferSize(int paramInt)
/* 183:    */   {
/* 184:235 */     this.InputBuffer = paramInt;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public int getInputBufferSize()
/* 188:    */   {
/* 189:239 */     return this.InputBuffer;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void setOutputBufferSize(int paramInt)
/* 193:    */   {
/* 194:243 */     this.OutputBuffer = paramInt;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public int getOutputBufferSize()
/* 198:    */   {
/* 199:247 */     return this.OutputBuffer;
/* 200:    */   }
/* 201:    */   
/* 202:285 */   private int dataAvailable = 0;
/* 203:    */   
/* 204:    */   public void sendEvent(int paramInt, boolean paramBoolean)
/* 205:    */   {
/* 206:287 */     switch (paramInt)
/* 207:    */     {
/* 208:    */     case 1: 
/* 209:289 */       this.dataAvailable = 1;
/* 210:290 */       if (!this.monThread.Data) {
/* 211:    */         return;
/* 212:    */       }
/* 213:    */       break;
/* 214:    */     case 2: 
/* 215:293 */       if (!this.monThread.Output) {
/* 216:    */         return;
/* 217:    */       }
/* 218:    */       break;
/* 219:    */     case 3: 
/* 220:313 */       if (!this.monThread.CTS) {
/* 221:    */         return;
/* 222:    */       }
/* 223:    */       break;
/* 224:    */     case 4: 
/* 225:316 */       if (!this.monThread.DSR) {
/* 226:    */         return;
/* 227:    */       }
/* 228:    */       break;
/* 229:    */     case 5: 
/* 230:319 */       if (!this.monThread.RI) {
/* 231:    */         return;
/* 232:    */       }
/* 233:    */       break;
/* 234:    */     case 6: 
/* 235:322 */       if (!this.monThread.CD) {
/* 236:    */         return;
/* 237:    */       }
/* 238:    */       break;
/* 239:    */     case 7: 
/* 240:325 */       if (!this.monThread.OE) {
/* 241:    */         return;
/* 242:    */       }
/* 243:    */       break;
/* 244:    */     case 8: 
/* 245:328 */       if (!this.monThread.PE) {
/* 246:    */         return;
/* 247:    */       }
/* 248:    */       break;
/* 249:    */     case 9: 
/* 250:331 */       if (!this.monThread.FE) {
/* 251:    */         return;
/* 252:    */       }
/* 253:    */       break;
/* 254:    */     case 10: 
/* 255:334 */       if (!this.monThread.BI) {
/* 256:    */         return;
/* 257:    */       }
/* 258:    */       break;
/* 259:    */     default: 
/* 260:337 */       System.err.println("unknown event:" + paramInt);
/* 261:338 */       return;
/* 262:    */     }
/* 263:340 */     RawPortEvent localRawPortEvent = new RawPortEvent(this, paramInt, !paramBoolean, paramBoolean);
/* 264:341 */     if (this.SPEventListener != null) {
/* 265:341 */       this.SPEventListener.RawEvent(localRawPortEvent);
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void addEventListener(RawPortEventListener paramRawPortEventListener)
/* 270:    */     throws TooManyListenersException
/* 271:    */   {
/* 272:348 */     if (this.SPEventListener != null) {
/* 273:348 */       throw new TooManyListenersException();
/* 274:    */     }
/* 275:349 */     this.SPEventListener = paramRawPortEventListener;
/* 276:350 */     this.monThread = new MonitorThread();
/* 277:351 */     this.monThread.start();
/* 278:    */   }
/* 279:    */   
/* 280:    */   public void removeEventListener()
/* 281:    */   {
/* 282:355 */     this.SPEventListener = null;
/* 283:356 */     if (this.monThread != null)
/* 284:    */     {
/* 285:357 */       this.monThread.interrupt();
/* 286:358 */       this.monThread = null;
/* 287:    */     }
/* 288:    */   }
/* 289:    */   
/* 290:    */   public void notifyOnDataAvailable(boolean paramBoolean)
/* 291:    */   {
/* 292:362 */     this.monThread.Data = paramBoolean;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public void notifyOnOutputEmpty(boolean paramBoolean)
/* 296:    */   {
/* 297:364 */     this.monThread.Output = paramBoolean;
/* 298:    */   }
/* 299:    */   
/* 300:    */   public void notifyOnCTS(boolean paramBoolean)
/* 301:    */   {
/* 302:366 */     this.monThread.CTS = paramBoolean;
/* 303:    */   }
/* 304:    */   
/* 305:    */   public void notifyOnDSR(boolean paramBoolean)
/* 306:    */   {
/* 307:367 */     this.monThread.DSR = paramBoolean;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public void notifyOnRingIndicator(boolean paramBoolean)
/* 311:    */   {
/* 312:368 */     this.monThread.RI = paramBoolean;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public void notifyOnCarrierDetect(boolean paramBoolean)
/* 316:    */   {
/* 317:369 */     this.monThread.CD = paramBoolean;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public void notifyOnOverrunError(boolean paramBoolean)
/* 321:    */   {
/* 322:370 */     this.monThread.OE = paramBoolean;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public void notifyOnParityError(boolean paramBoolean)
/* 326:    */   {
/* 327:371 */     this.monThread.PE = paramBoolean;
/* 328:    */   }
/* 329:    */   
/* 330:    */   public void notifyOnFramingError(boolean paramBoolean)
/* 331:    */   {
/* 332:372 */     this.monThread.FE = paramBoolean;
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void notifyOnBreakInterrupt(boolean paramBoolean)
/* 336:    */   {
/* 337:373 */     this.monThread.BI = paramBoolean;
/* 338:    */   }
/* 339:    */   
/* 340:    */   public void close()
/* 341:    */   {
/* 342:379 */     setDTR(false);
/* 343:380 */     setDSR(false);
/* 344:381 */     nativeClose();
/* 345:382 */     super.close();
/* 346:383 */     this.ciAddress = 0;
/* 347:    */   }
/* 348:    */   
/* 349:    */   protected void finalize()
/* 350:    */   {
/* 351:389 */     close();
/* 352:    */   }
/* 353:    */   
/* 354:    */   class RawOutputStream
/* 355:    */     extends OutputStream
/* 356:    */   {
/* 357:    */     RawOutputStream() {}
/* 358:    */     
/* 359:    */     public void write(int paramInt)
/* 360:    */       throws IOException
/* 361:    */     {
/* 362:396 */       Raw.this.writeByte(paramInt);
/* 363:    */     }
/* 364:    */     
/* 365:    */     public void write(byte[] paramArrayOfByte)
/* 366:    */       throws IOException
/* 367:    */     {
/* 368:399 */       Raw.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/* 369:    */     }
/* 370:    */     
/* 371:    */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 372:    */       throws IOException
/* 373:    */     {
/* 374:402 */       Raw.this.writeArray(paramArrayOfByte, paramInt1, paramInt2);
/* 375:    */     }
/* 376:    */     
/* 377:    */     public void flush()
/* 378:    */       throws IOException
/* 379:    */     {
/* 380:405 */       Raw.this.drain();
/* 381:    */     }
/* 382:    */   }
/* 383:    */   
/* 384:    */   class RawInputStream
/* 385:    */     extends InputStream
/* 386:    */   {
/* 387:    */     RawInputStream() {}
/* 388:    */     
/* 389:    */     public int read()
/* 390:    */       throws IOException
/* 391:    */     {
/* 392:412 */       Raw.this.dataAvailable = 0;
/* 393:413 */       return Raw.this.readByte();
/* 394:    */     }
/* 395:    */     
/* 396:    */     public int read(byte[] paramArrayOfByte)
/* 397:    */       throws IOException
/* 398:    */     {
/* 399:417 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/* 400:    */     }
/* 401:    */     
/* 402:    */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 403:    */       throws IOException
/* 404:    */     {
/* 405:421 */       Raw.this.dataAvailable = 0;
/* 406:422 */       int i = 0;int j = 0;
/* 407:423 */       int[] arrayOfInt = { paramArrayOfByte.length, Raw.this.InputBuffer, paramInt2 };
/* 408:435 */       while ((arrayOfInt[i] == 0) && (i < arrayOfInt.length)) {
/* 409:435 */         i++;
/* 410:    */       }
/* 411:436 */       j = arrayOfInt[i];
/* 412:437 */       while (i < arrayOfInt.length)
/* 413:    */       {
/* 414:439 */         if (arrayOfInt[i] > 0) {
/* 415:441 */           j = Math.min(j, arrayOfInt[i]);
/* 416:    */         }
/* 417:443 */         i++;
/* 418:    */       }
/* 419:445 */       j = Math.min(j, Raw.this.threshold);
/* 420:446 */       if (j == 0) {
/* 421:446 */         j = 1;
/* 422:    */       }
/* 423:447 */       int k = available();
/* 424:448 */       int m = Raw.this.readArray(paramArrayOfByte, paramInt1, j);
/* 425:449 */       return m;
/* 426:    */     }
/* 427:    */     
/* 428:    */     public int available()
/* 429:    */       throws IOException
/* 430:    */     {
/* 431:452 */       return Raw.this.nativeavailable();
/* 432:    */     }
/* 433:    */   }
/* 434:    */   
/* 435:    */   class MonitorThread
/* 436:    */     extends Thread
/* 437:    */   {
/* 438:459 */     private boolean CTS = false;
/* 439:460 */     private boolean DSR = false;
/* 440:461 */     private boolean RI = false;
/* 441:462 */     private boolean CD = false;
/* 442:463 */     private boolean OE = false;
/* 443:464 */     private boolean PE = false;
/* 444:465 */     private boolean FE = false;
/* 445:466 */     private boolean BI = false;
/* 446:467 */     private boolean Data = false;
/* 447:468 */     private boolean Output = false;
/* 448:    */     
/* 449:    */     MonitorThread() {}
/* 450:    */     
/* 451:    */     public void run()
/* 452:    */     {
/* 453:471 */       Raw.this.eventLoop();
/* 454:    */     }
/* 455:    */   }
/* 456:    */   
/* 457:    */   public String getVersion()
/* 458:    */   {
/* 459:476 */     String str = "$Id: Raw.java,v 1.1.2.17 2008-09-14 22:29:30 jarvi Exp $";
/* 460:477 */     return str;
/* 461:    */   }
/* 462:    */   
/* 463:    */   private static native void Initialize();
/* 464:    */   
/* 465:    */   private native int open(int paramInt)
/* 466:    */     throws PortInUseException;
/* 467:    */   
/* 468:    */   private native void nativeSetRawPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/* 469:    */     throws UnsupportedCommOperationException;
/* 470:    */   
/* 471:    */   native void setflowcontrol(int paramInt)
/* 472:    */     throws IOException;
/* 473:    */   
/* 474:    */   public void disableReceiveFraming() {}
/* 475:    */   
/* 476:    */   public native int NativegetReceiveTimeout();
/* 477:    */   
/* 478:    */   public native boolean NativeisReceiveTimeoutEnabled();
/* 479:    */   
/* 480:    */   public native void NativeEnableReceiveTimeoutThreshold(int paramInt1, int paramInt2, int paramInt3);
/* 481:    */   
/* 482:    */   public native boolean isDTR();
/* 483:    */   
/* 484:    */   public native void setDTR(boolean paramBoolean);
/* 485:    */   
/* 486:    */   public native void setRTS(boolean paramBoolean);
/* 487:    */   
/* 488:    */   private native void setDSR(boolean paramBoolean);
/* 489:    */   
/* 490:    */   public native boolean isCTS();
/* 491:    */   
/* 492:    */   public native boolean isDSR();
/* 493:    */   
/* 494:    */   public native boolean isCD();
/* 495:    */   
/* 496:    */   public native boolean isRI();
/* 497:    */   
/* 498:    */   public native boolean isRTS();
/* 499:    */   
/* 500:    */   public native void sendBreak(int paramInt);
/* 501:    */   
/* 502:    */   private native void writeByte(int paramInt)
/* 503:    */     throws IOException;
/* 504:    */   
/* 505:    */   private native void writeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 506:    */     throws IOException;
/* 507:    */   
/* 508:    */   private native void drain()
/* 509:    */     throws IOException;
/* 510:    */   
/* 511:    */   private native int nativeavailable()
/* 512:    */     throws IOException;
/* 513:    */   
/* 514:    */   private native int readByte()
/* 515:    */     throws IOException;
/* 516:    */   
/* 517:    */   private native int readArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 518:    */     throws IOException;
/* 519:    */   
/* 520:    */   native void eventLoop();
/* 521:    */   
/* 522:    */   private native int nativeClose();
/* 523:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.Raw
 * JD-Core Version:    0.7.0.1
 */