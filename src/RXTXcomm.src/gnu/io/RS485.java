/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.util.TooManyListenersException;
/*   8:    */ 
/*   9:    */ final class RS485
/*  10:    */   extends RS485Port
/*  11:    */ {
/*  12:    */   private int fd;
/*  13:    */   
/*  14:    */   static
/*  15:    */   {
/*  16: 74 */     System.loadLibrary("rxtxRS485");
/*  17: 75 */     Initialize();
/*  18:    */   }
/*  19:    */   
/*  20:    */   public RS485(String paramString)
/*  21:    */     throws PortInUseException
/*  22:    */   {
/*  23: 88 */     this.fd = open(paramString);
/*  24:    */   }
/*  25:    */   
/*  26: 97 */   static boolean dsrFlag = false;
/*  27:100 */   private final RS485OutputStream out = new RS485OutputStream();
/*  28:    */   
/*  29:    */   public OutputStream getOutputStream()
/*  30:    */   {
/*  31:101 */     return this.out;
/*  32:    */   }
/*  33:    */   
/*  34:105 */   private final RS485InputStream in = new RS485InputStream();
/*  35:    */   
/*  36:    */   public InputStream getInputStream()
/*  37:    */   {
/*  38:106 */     return this.in;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setRS485PortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*  42:    */     throws UnsupportedCommOperationException
/*  43:    */   {
/*  44:115 */     nativeSetRS485PortParams(paramInt1, paramInt2, paramInt3, paramInt4);
/*  45:116 */     this.speed = paramInt1;
/*  46:117 */     this.dataBits = paramInt2;
/*  47:118 */     this.stopBits = paramInt3;
/*  48:119 */     this.parity = paramInt4;
/*  49:    */   }
/*  50:    */   
/*  51:127 */   private int speed = 9600;
/*  52:    */   
/*  53:    */   public int getBaudRate()
/*  54:    */   {
/*  55:128 */     return this.speed;
/*  56:    */   }
/*  57:    */   
/*  58:131 */   private int dataBits = 8;
/*  59:    */   
/*  60:    */   public int getDataBits()
/*  61:    */   {
/*  62:132 */     return this.dataBits;
/*  63:    */   }
/*  64:    */   
/*  65:135 */   private int stopBits = 1;
/*  66:    */   
/*  67:    */   public int getStopBits()
/*  68:    */   {
/*  69:136 */     return this.stopBits;
/*  70:    */   }
/*  71:    */   
/*  72:139 */   private int parity = 0;
/*  73:    */   
/*  74:    */   public int getParity()
/*  75:    */   {
/*  76:140 */     return this.parity;
/*  77:    */   }
/*  78:    */   
/*  79:144 */   private int flowmode = 0;
/*  80:    */   
/*  81:    */   public void setFlowControlMode(int paramInt)
/*  82:    */   {
/*  83:    */     try
/*  84:    */     {
/*  85:146 */       setflowcontrol(paramInt);
/*  86:    */     }
/*  87:    */     catch (IOException localIOException)
/*  88:    */     {
/*  89:148 */       localIOException.printStackTrace();
/*  90:149 */       return;
/*  91:    */     }
/*  92:151 */     this.flowmode = paramInt;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public int getFlowControlMode()
/*  96:    */   {
/*  97:153 */     return this.flowmode;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void enableReceiveFraming(int paramInt)
/* 101:    */     throws UnsupportedCommOperationException
/* 102:    */   {
/* 103:166 */     throw new UnsupportedCommOperationException("Not supported");
/* 104:    */   }
/* 105:    */   
/* 106:    */   public boolean isReceiveFramingEnabled()
/* 107:    */   {
/* 108:169 */     return false;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public int getReceiveFramingByte()
/* 112:    */   {
/* 113:170 */     return 0;
/* 114:    */   }
/* 115:    */   
/* 116:174 */   private int timeout = 0;
/* 117:    */   
/* 118:    */   public void disableReceiveTimeout()
/* 119:    */   {
/* 120:180 */     enableReceiveTimeout(0);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void enableReceiveTimeout(int paramInt)
/* 124:    */   {
/* 125:183 */     if (paramInt >= 0)
/* 126:    */     {
/* 127:184 */       this.timeout = paramInt;
/* 128:185 */       NativeEnableReceiveTimeoutThreshold(paramInt, this.threshold, this.InputBuffer);
/* 129:    */     }
/* 130:    */     else
/* 131:    */     {
/* 132:188 */       System.out.println("Invalid timeout");
/* 133:    */     }
/* 134:    */   }
/* 135:    */   
/* 136:    */   public boolean isReceiveTimeoutEnabled()
/* 137:    */   {
/* 138:192 */     return NativeisReceiveTimeoutEnabled();
/* 139:    */   }
/* 140:    */   
/* 141:    */   public int getReceiveTimeout()
/* 142:    */   {
/* 143:195 */     return NativegetReceiveTimeout();
/* 144:    */   }
/* 145:    */   
/* 146:200 */   private int threshold = 0;
/* 147:    */   
/* 148:    */   public void enableReceiveThreshold(int paramInt)
/* 149:    */   {
/* 150:203 */     if (paramInt >= 0)
/* 151:    */     {
/* 152:205 */       this.threshold = paramInt;
/* 153:206 */       NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
/* 154:    */     }
/* 155:    */     else
/* 156:    */     {
/* 157:210 */       System.out.println("Invalid Threshold");
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void disableReceiveThreshold()
/* 162:    */   {
/* 163:214 */     enableReceiveThreshold(0);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public int getReceiveThreshold()
/* 167:    */   {
/* 168:217 */     return this.threshold;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public boolean isReceiveThresholdEnabled()
/* 172:    */   {
/* 173:220 */     return this.threshold > 0;
/* 174:    */   }
/* 175:    */   
/* 176:230 */   private int InputBuffer = 0;
/* 177:231 */   private int OutputBuffer = 0;
/* 178:    */   private RS485PortEventListener SPEventListener;
/* 179:    */   private MonitorThread monThread;
/* 180:    */   
/* 181:    */   public void setInputBufferSize(int paramInt)
/* 182:    */   {
/* 183:234 */     this.InputBuffer = paramInt;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public int getInputBufferSize()
/* 187:    */   {
/* 188:238 */     return this.InputBuffer;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void setOutputBufferSize(int paramInt)
/* 192:    */   {
/* 193:242 */     this.OutputBuffer = paramInt;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public int getOutputBufferSize()
/* 197:    */   {
/* 198:246 */     return this.OutputBuffer;
/* 199:    */   }
/* 200:    */   
/* 201:284 */   private int dataAvailable = 0;
/* 202:    */   
/* 203:    */   public void sendEvent(int paramInt, boolean paramBoolean)
/* 204:    */   {
/* 205:286 */     switch (paramInt)
/* 206:    */     {
/* 207:    */     case 1: 
/* 208:288 */       this.dataAvailable = 1;
/* 209:289 */       if (!this.monThread.Data) {
/* 210:    */         return;
/* 211:    */       }
/* 212:    */       break;
/* 213:    */     case 2: 
/* 214:292 */       if (!this.monThread.Output) {
/* 215:    */         return;
/* 216:    */       }
/* 217:    */       break;
/* 218:    */     case 3: 
/* 219:312 */       if (!this.monThread.CTS) {
/* 220:    */         return;
/* 221:    */       }
/* 222:    */       break;
/* 223:    */     case 4: 
/* 224:315 */       if (!this.monThread.DSR) {
/* 225:    */         return;
/* 226:    */       }
/* 227:    */       break;
/* 228:    */     case 5: 
/* 229:318 */       if (!this.monThread.RI) {
/* 230:    */         return;
/* 231:    */       }
/* 232:    */       break;
/* 233:    */     case 6: 
/* 234:321 */       if (!this.monThread.CD) {
/* 235:    */         return;
/* 236:    */       }
/* 237:    */       break;
/* 238:    */     case 7: 
/* 239:324 */       if (!this.monThread.OE) {
/* 240:    */         return;
/* 241:    */       }
/* 242:    */       break;
/* 243:    */     case 8: 
/* 244:327 */       if (!this.monThread.PE) {
/* 245:    */         return;
/* 246:    */       }
/* 247:    */       break;
/* 248:    */     case 9: 
/* 249:330 */       if (!this.monThread.FE) {
/* 250:    */         return;
/* 251:    */       }
/* 252:    */       break;
/* 253:    */     case 10: 
/* 254:333 */       if (!this.monThread.BI) {
/* 255:    */         return;
/* 256:    */       }
/* 257:    */       break;
/* 258:    */     default: 
/* 259:336 */       System.err.println("unknown event:" + paramInt);
/* 260:337 */       return;
/* 261:    */     }
/* 262:339 */     RS485PortEvent localRS485PortEvent = new RS485PortEvent(this, paramInt, !paramBoolean, paramBoolean);
/* 263:340 */     if (this.SPEventListener != null) {
/* 264:340 */       this.SPEventListener.RS485Event(localRS485PortEvent);
/* 265:    */     }
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void addEventListener(RS485PortEventListener paramRS485PortEventListener)
/* 269:    */     throws TooManyListenersException
/* 270:    */   {
/* 271:347 */     if (this.SPEventListener != null) {
/* 272:347 */       throw new TooManyListenersException();
/* 273:    */     }
/* 274:348 */     this.SPEventListener = paramRS485PortEventListener;
/* 275:349 */     this.monThread = new MonitorThread();
/* 276:350 */     this.monThread.start();
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void removeEventListener()
/* 280:    */   {
/* 281:354 */     this.SPEventListener = null;
/* 282:355 */     if (this.monThread != null)
/* 283:    */     {
/* 284:356 */       this.monThread.interrupt();
/* 285:357 */       this.monThread = null;
/* 286:    */     }
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void notifyOnDataAvailable(boolean paramBoolean)
/* 290:    */   {
/* 291:361 */     this.monThread.Data = paramBoolean;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void notifyOnOutputEmpty(boolean paramBoolean)
/* 295:    */   {
/* 296:363 */     this.monThread.Output = paramBoolean;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public void notifyOnCTS(boolean paramBoolean)
/* 300:    */   {
/* 301:365 */     this.monThread.CTS = paramBoolean;
/* 302:    */   }
/* 303:    */   
/* 304:    */   public void notifyOnDSR(boolean paramBoolean)
/* 305:    */   {
/* 306:366 */     this.monThread.DSR = paramBoolean;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public void notifyOnRingIndicator(boolean paramBoolean)
/* 310:    */   {
/* 311:367 */     this.monThread.RI = paramBoolean;
/* 312:    */   }
/* 313:    */   
/* 314:    */   public void notifyOnCarrierDetect(boolean paramBoolean)
/* 315:    */   {
/* 316:368 */     this.monThread.CD = paramBoolean;
/* 317:    */   }
/* 318:    */   
/* 319:    */   public void notifyOnOverrunError(boolean paramBoolean)
/* 320:    */   {
/* 321:369 */     this.monThread.OE = paramBoolean;
/* 322:    */   }
/* 323:    */   
/* 324:    */   public void notifyOnParityError(boolean paramBoolean)
/* 325:    */   {
/* 326:370 */     this.monThread.PE = paramBoolean;
/* 327:    */   }
/* 328:    */   
/* 329:    */   public void notifyOnFramingError(boolean paramBoolean)
/* 330:    */   {
/* 331:371 */     this.monThread.FE = paramBoolean;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public void notifyOnBreakInterrupt(boolean paramBoolean)
/* 335:    */   {
/* 336:372 */     this.monThread.BI = paramBoolean;
/* 337:    */   }
/* 338:    */   
/* 339:    */   public void close()
/* 340:    */   {
/* 341:378 */     setDTR(false);
/* 342:379 */     setDSR(false);
/* 343:380 */     nativeClose();
/* 344:381 */     super.close();
/* 345:382 */     this.fd = 0;
/* 346:    */   }
/* 347:    */   
/* 348:    */   protected void finalize()
/* 349:    */   {
/* 350:388 */     if (this.fd > 0) {
/* 351:388 */       close();
/* 352:    */     }
/* 353:    */   }
/* 354:    */   
/* 355:    */   private static native void Initialize();
/* 356:    */   
/* 357:    */   private native int open(String paramString)
/* 358:    */     throws PortInUseException;
/* 359:    */   
/* 360:    */   private native void nativeSetRS485PortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/* 361:    */     throws UnsupportedCommOperationException;
/* 362:    */   
/* 363:    */   native void setflowcontrol(int paramInt)
/* 364:    */     throws IOException;
/* 365:    */   
/* 366:    */   public void disableReceiveFraming() {}
/* 367:    */   
/* 368:    */   public native int NativegetReceiveTimeout();
/* 369:    */   
/* 370:    */   public native boolean NativeisReceiveTimeoutEnabled();
/* 371:    */   
/* 372:    */   public native void NativeEnableReceiveTimeoutThreshold(int paramInt1, int paramInt2, int paramInt3);
/* 373:    */   
/* 374:    */   public native boolean isDTR();
/* 375:    */   
/* 376:    */   public native void setDTR(boolean paramBoolean);
/* 377:    */   
/* 378:    */   public native void setRTS(boolean paramBoolean);
/* 379:    */   
/* 380:    */   private native void setDSR(boolean paramBoolean);
/* 381:    */   
/* 382:    */   public native boolean isCTS();
/* 383:    */   
/* 384:    */   public native boolean isDSR();
/* 385:    */   
/* 386:    */   public native boolean isCD();
/* 387:    */   
/* 388:    */   public native boolean isRI();
/* 389:    */   
/* 390:    */   public native boolean isRTS();
/* 391:    */   
/* 392:    */   public native void sendBreak(int paramInt);
/* 393:    */   
/* 394:    */   private native void writeByte(int paramInt)
/* 395:    */     throws IOException;
/* 396:    */   
/* 397:    */   private native void writeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 398:    */     throws IOException;
/* 399:    */   
/* 400:    */   private native void drain()
/* 401:    */     throws IOException;
/* 402:    */   
/* 403:    */   private native int nativeavailable()
/* 404:    */     throws IOException;
/* 405:    */   
/* 406:    */   private native int readByte()
/* 407:    */     throws IOException;
/* 408:    */   
/* 409:    */   private native int readArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 410:    */     throws IOException;
/* 411:    */   
/* 412:    */   native void eventLoop();
/* 413:    */   
/* 414:    */   private native void nativeClose();
/* 415:    */   
/* 416:    */   class RS485OutputStream
/* 417:    */     extends OutputStream
/* 418:    */   {
/* 419:    */     RS485OutputStream() {}
/* 420:    */     
/* 421:    */     public void write(int paramInt)
/* 422:    */       throws IOException
/* 423:    */     {
/* 424:395 */       RS485.this.writeByte(paramInt);
/* 425:    */     }
/* 426:    */     
/* 427:    */     public void write(byte[] paramArrayOfByte)
/* 428:    */       throws IOException
/* 429:    */     {
/* 430:398 */       RS485.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/* 431:    */     }
/* 432:    */     
/* 433:    */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 434:    */       throws IOException
/* 435:    */     {
/* 436:401 */       RS485.this.writeArray(paramArrayOfByte, paramInt1, paramInt2);
/* 437:    */     }
/* 438:    */     
/* 439:    */     public void flush()
/* 440:    */       throws IOException
/* 441:    */     {
/* 442:404 */       RS485.this.drain();
/* 443:    */     }
/* 444:    */   }
/* 445:    */   
/* 446:    */   class RS485InputStream
/* 447:    */     extends InputStream
/* 448:    */   {
/* 449:    */     RS485InputStream() {}
/* 450:    */     
/* 451:    */     public int read()
/* 452:    */       throws IOException
/* 453:    */     {
/* 454:411 */       RS485.this.dataAvailable = 0;
/* 455:412 */       return RS485.this.readByte();
/* 456:    */     }
/* 457:    */     
/* 458:    */     public int read(byte[] paramArrayOfByte)
/* 459:    */       throws IOException
/* 460:    */     {
/* 461:416 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/* 462:    */     }
/* 463:    */     
/* 464:    */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 465:    */       throws IOException
/* 466:    */     {
/* 467:420 */       RS485.this.dataAvailable = 0;
/* 468:421 */       int i = 0;int j = 0;
/* 469:422 */       int[] arrayOfInt = { paramArrayOfByte.length, RS485.this.InputBuffer, paramInt2 };
/* 470:434 */       while ((arrayOfInt[i] == 0) && (i < arrayOfInt.length)) {
/* 471:434 */         i++;
/* 472:    */       }
/* 473:435 */       j = arrayOfInt[i];
/* 474:436 */       while (i < arrayOfInt.length)
/* 475:    */       {
/* 476:438 */         if (arrayOfInt[i] > 0) {
/* 477:440 */           j = Math.min(j, arrayOfInt[i]);
/* 478:    */         }
/* 479:442 */         i++;
/* 480:    */       }
/* 481:444 */       j = Math.min(j, RS485.this.threshold);
/* 482:445 */       if (j == 0) {
/* 483:445 */         j = 1;
/* 484:    */       }
/* 485:446 */       int k = available();
/* 486:447 */       int m = RS485.this.readArray(paramArrayOfByte, paramInt1, j);
/* 487:448 */       return m;
/* 488:    */     }
/* 489:    */     
/* 490:    */     public int available()
/* 491:    */       throws IOException
/* 492:    */     {
/* 493:451 */       return RS485.this.nativeavailable();
/* 494:    */     }
/* 495:    */   }
/* 496:    */   
/* 497:    */   class MonitorThread
/* 498:    */     extends Thread
/* 499:    */   {
/* 500:458 */     private boolean CTS = false;
/* 501:459 */     private boolean DSR = false;
/* 502:460 */     private boolean RI = false;
/* 503:461 */     private boolean CD = false;
/* 504:462 */     private boolean OE = false;
/* 505:463 */     private boolean PE = false;
/* 506:464 */     private boolean FE = false;
/* 507:465 */     private boolean BI = false;
/* 508:466 */     private boolean Data = false;
/* 509:467 */     private boolean Output = false;
/* 510:    */     
/* 511:    */     MonitorThread() {}
/* 512:    */     
/* 513:    */     public void run()
/* 514:    */     {
/* 515:470 */       RS485.this.eventLoop();
/* 516:    */     }
/* 517:    */   }
/* 518:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.RS485
 * JD-Core Version:    0.7.0.1
 */