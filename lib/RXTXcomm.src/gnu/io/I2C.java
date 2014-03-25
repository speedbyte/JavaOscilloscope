/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.util.TooManyListenersException;
/*   8:    */ 
/*   9:    */ final class I2C
/*  10:    */   extends I2CPort
/*  11:    */ {
/*  12:    */   private int fd;
/*  13:    */   
/*  14:    */   static
/*  15:    */   {
/*  16: 77 */     System.loadLibrary("rxtxI2C");
/*  17: 78 */     Initialize();
/*  18:    */   }
/*  19:    */   
/*  20:    */   public I2C(String paramString)
/*  21:    */     throws PortInUseException
/*  22:    */   {
/*  23: 91 */     this.fd = open(paramString);
/*  24:    */   }
/*  25:    */   
/*  26:100 */   static boolean dsrFlag = false;
/*  27:103 */   private final I2COutputStream out = new I2COutputStream();
/*  28:    */   
/*  29:    */   public OutputStream getOutputStream()
/*  30:    */   {
/*  31:104 */     return this.out;
/*  32:    */   }
/*  33:    */   
/*  34:108 */   private final I2CInputStream in = new I2CInputStream();
/*  35:    */   
/*  36:    */   public InputStream getInputStream()
/*  37:    */   {
/*  38:109 */     return this.in;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setI2CPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*  42:    */     throws UnsupportedCommOperationException
/*  43:    */   {
/*  44:118 */     nativeSetI2CPortParams(paramInt1, paramInt2, paramInt3, paramInt4);
/*  45:119 */     this.speed = paramInt1;
/*  46:120 */     this.dataBits = paramInt2;
/*  47:121 */     this.stopBits = paramInt3;
/*  48:122 */     this.parity = paramInt4;
/*  49:    */   }
/*  50:    */   
/*  51:130 */   private int speed = 9600;
/*  52:    */   
/*  53:    */   public int getBaudRate()
/*  54:    */   {
/*  55:131 */     return this.speed;
/*  56:    */   }
/*  57:    */   
/*  58:134 */   private int dataBits = 8;
/*  59:    */   
/*  60:    */   public int getDataBits()
/*  61:    */   {
/*  62:135 */     return this.dataBits;
/*  63:    */   }
/*  64:    */   
/*  65:138 */   private int stopBits = 1;
/*  66:    */   
/*  67:    */   public int getStopBits()
/*  68:    */   {
/*  69:139 */     return this.stopBits;
/*  70:    */   }
/*  71:    */   
/*  72:142 */   private int parity = 0;
/*  73:    */   
/*  74:    */   public int getParity()
/*  75:    */   {
/*  76:143 */     return this.parity;
/*  77:    */   }
/*  78:    */   
/*  79:147 */   private int flowmode = 0;
/*  80:    */   
/*  81:    */   public void setFlowControlMode(int paramInt)
/*  82:    */   {
/*  83:    */     try
/*  84:    */     {
/*  85:149 */       setflowcontrol(paramInt);
/*  86:    */     }
/*  87:    */     catch (IOException localIOException)
/*  88:    */     {
/*  89:151 */       localIOException.printStackTrace();
/*  90:152 */       return;
/*  91:    */     }
/*  92:154 */     this.flowmode = paramInt;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public int getFlowControlMode()
/*  96:    */   {
/*  97:156 */     return this.flowmode;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void enableReceiveFraming(int paramInt)
/* 101:    */     throws UnsupportedCommOperationException
/* 102:    */   {
/* 103:169 */     throw new UnsupportedCommOperationException("Not supported");
/* 104:    */   }
/* 105:    */   
/* 106:    */   public boolean isReceiveFramingEnabled()
/* 107:    */   {
/* 108:172 */     return false;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public int getReceiveFramingByte()
/* 112:    */   {
/* 113:173 */     return 0;
/* 114:    */   }
/* 115:    */   
/* 116:177 */   private int timeout = 0;
/* 117:    */   
/* 118:    */   public void disableReceiveTimeout()
/* 119:    */   {
/* 120:183 */     enableReceiveTimeout(0);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void enableReceiveTimeout(int paramInt)
/* 124:    */   {
/* 125:186 */     if (paramInt >= 0)
/* 126:    */     {
/* 127:187 */       this.timeout = paramInt;
/* 128:188 */       NativeEnableReceiveTimeoutThreshold(paramInt, this.threshold, this.InputBuffer);
/* 129:    */     }
/* 130:    */     else
/* 131:    */     {
/* 132:191 */       System.out.println("Invalid timeout");
/* 133:    */     }
/* 134:    */   }
/* 135:    */   
/* 136:    */   public boolean isReceiveTimeoutEnabled()
/* 137:    */   {
/* 138:195 */     return NativeisReceiveTimeoutEnabled();
/* 139:    */   }
/* 140:    */   
/* 141:    */   public int getReceiveTimeout()
/* 142:    */   {
/* 143:198 */     return NativegetReceiveTimeout();
/* 144:    */   }
/* 145:    */   
/* 146:203 */   private int threshold = 0;
/* 147:    */   
/* 148:    */   public void enableReceiveThreshold(int paramInt)
/* 149:    */   {
/* 150:206 */     if (paramInt >= 0)
/* 151:    */     {
/* 152:208 */       this.threshold = paramInt;
/* 153:209 */       NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
/* 154:    */     }
/* 155:    */     else
/* 156:    */     {
/* 157:213 */       System.out.println("Invalid Threshold");
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void disableReceiveThreshold()
/* 162:    */   {
/* 163:217 */     enableReceiveThreshold(0);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public int getReceiveThreshold()
/* 167:    */   {
/* 168:220 */     return this.threshold;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public boolean isReceiveThresholdEnabled()
/* 172:    */   {
/* 173:223 */     return this.threshold > 0;
/* 174:    */   }
/* 175:    */   
/* 176:233 */   private int InputBuffer = 0;
/* 177:234 */   private int OutputBuffer = 0;
/* 178:    */   private I2CPortEventListener SPEventListener;
/* 179:    */   private MonitorThread monThread;
/* 180:    */   
/* 181:    */   public void setInputBufferSize(int paramInt)
/* 182:    */   {
/* 183:237 */     this.InputBuffer = paramInt;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public int getInputBufferSize()
/* 187:    */   {
/* 188:241 */     return this.InputBuffer;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void setOutputBufferSize(int paramInt)
/* 192:    */   {
/* 193:245 */     this.OutputBuffer = paramInt;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public int getOutputBufferSize()
/* 197:    */   {
/* 198:249 */     return this.OutputBuffer;
/* 199:    */   }
/* 200:    */   
/* 201:287 */   private int dataAvailable = 0;
/* 202:    */   
/* 203:    */   public void sendEvent(int paramInt, boolean paramBoolean)
/* 204:    */   {
/* 205:289 */     switch (paramInt)
/* 206:    */     {
/* 207:    */     case 1: 
/* 208:291 */       this.dataAvailable = 1;
/* 209:292 */       if (!this.monThread.Data) {
/* 210:    */         return;
/* 211:    */       }
/* 212:    */       break;
/* 213:    */     case 2: 
/* 214:295 */       if (!this.monThread.Output) {
/* 215:    */         return;
/* 216:    */       }
/* 217:    */       break;
/* 218:    */     case 3: 
/* 219:315 */       if (!this.monThread.CTS) {
/* 220:    */         return;
/* 221:    */       }
/* 222:    */       break;
/* 223:    */     case 4: 
/* 224:318 */       if (!this.monThread.DSR) {
/* 225:    */         return;
/* 226:    */       }
/* 227:    */       break;
/* 228:    */     case 5: 
/* 229:321 */       if (!this.monThread.RI) {
/* 230:    */         return;
/* 231:    */       }
/* 232:    */       break;
/* 233:    */     case 6: 
/* 234:324 */       if (!this.monThread.CD) {
/* 235:    */         return;
/* 236:    */       }
/* 237:    */       break;
/* 238:    */     case 7: 
/* 239:327 */       if (!this.monThread.OE) {
/* 240:    */         return;
/* 241:    */       }
/* 242:    */       break;
/* 243:    */     case 8: 
/* 244:330 */       if (!this.monThread.PE) {
/* 245:    */         return;
/* 246:    */       }
/* 247:    */       break;
/* 248:    */     case 9: 
/* 249:333 */       if (!this.monThread.FE) {
/* 250:    */         return;
/* 251:    */       }
/* 252:    */       break;
/* 253:    */     case 10: 
/* 254:336 */       if (!this.monThread.BI) {
/* 255:    */         return;
/* 256:    */       }
/* 257:    */       break;
/* 258:    */     default: 
/* 259:339 */       System.err.println("unknown event:" + paramInt);
/* 260:340 */       return;
/* 261:    */     }
/* 262:342 */     I2CPortEvent localI2CPortEvent = new I2CPortEvent(this, paramInt, !paramBoolean, paramBoolean);
/* 263:343 */     if (this.SPEventListener != null) {
/* 264:343 */       this.SPEventListener.I2CEvent(localI2CPortEvent);
/* 265:    */     }
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void addEventListener(I2CPortEventListener paramI2CPortEventListener)
/* 269:    */     throws TooManyListenersException
/* 270:    */   {
/* 271:350 */     if (this.SPEventListener != null) {
/* 272:350 */       throw new TooManyListenersException();
/* 273:    */     }
/* 274:351 */     this.SPEventListener = paramI2CPortEventListener;
/* 275:352 */     this.monThread = new MonitorThread();
/* 276:353 */     this.monThread.start();
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void removeEventListener()
/* 280:    */   {
/* 281:357 */     this.SPEventListener = null;
/* 282:358 */     if (this.monThread != null)
/* 283:    */     {
/* 284:359 */       this.monThread.interrupt();
/* 285:360 */       this.monThread = null;
/* 286:    */     }
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void notifyOnDataAvailable(boolean paramBoolean)
/* 290:    */   {
/* 291:364 */     this.monThread.Data = paramBoolean;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void notifyOnOutputEmpty(boolean paramBoolean)
/* 295:    */   {
/* 296:366 */     this.monThread.Output = paramBoolean;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public void notifyOnCTS(boolean paramBoolean)
/* 300:    */   {
/* 301:368 */     this.monThread.CTS = paramBoolean;
/* 302:    */   }
/* 303:    */   
/* 304:    */   public void notifyOnDSR(boolean paramBoolean)
/* 305:    */   {
/* 306:369 */     this.monThread.DSR = paramBoolean;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public void notifyOnRingIndicator(boolean paramBoolean)
/* 310:    */   {
/* 311:370 */     this.monThread.RI = paramBoolean;
/* 312:    */   }
/* 313:    */   
/* 314:    */   public void notifyOnCarrierDetect(boolean paramBoolean)
/* 315:    */   {
/* 316:371 */     this.monThread.CD = paramBoolean;
/* 317:    */   }
/* 318:    */   
/* 319:    */   public void notifyOnOverrunError(boolean paramBoolean)
/* 320:    */   {
/* 321:372 */     this.monThread.OE = paramBoolean;
/* 322:    */   }
/* 323:    */   
/* 324:    */   public void notifyOnParityError(boolean paramBoolean)
/* 325:    */   {
/* 326:373 */     this.monThread.PE = paramBoolean;
/* 327:    */   }
/* 328:    */   
/* 329:    */   public void notifyOnFramingError(boolean paramBoolean)
/* 330:    */   {
/* 331:374 */     this.monThread.FE = paramBoolean;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public void notifyOnBreakInterrupt(boolean paramBoolean)
/* 335:    */   {
/* 336:375 */     this.monThread.BI = paramBoolean;
/* 337:    */   }
/* 338:    */   
/* 339:    */   public void close()
/* 340:    */   {
/* 341:381 */     setDTR(false);
/* 342:382 */     setDSR(false);
/* 343:383 */     nativeClose();
/* 344:384 */     super.close();
/* 345:385 */     this.fd = 0;
/* 346:    */   }
/* 347:    */   
/* 348:    */   protected void finalize()
/* 349:    */   {
/* 350:391 */     if (this.fd > 0) {
/* 351:391 */       close();
/* 352:    */     }
/* 353:    */   }
/* 354:    */   
/* 355:    */   private static native void Initialize();
/* 356:    */   
/* 357:    */   private native int open(String paramString)
/* 358:    */     throws PortInUseException;
/* 359:    */   
/* 360:    */   private native void nativeSetI2CPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
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
/* 416:    */   class I2COutputStream
/* 417:    */     extends OutputStream
/* 418:    */   {
/* 419:    */     I2COutputStream() {}
/* 420:    */     
/* 421:    */     public void write(int paramInt)
/* 422:    */       throws IOException
/* 423:    */     {
/* 424:398 */       I2C.this.writeByte(paramInt);
/* 425:    */     }
/* 426:    */     
/* 427:    */     public void write(byte[] paramArrayOfByte)
/* 428:    */       throws IOException
/* 429:    */     {
/* 430:401 */       I2C.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/* 431:    */     }
/* 432:    */     
/* 433:    */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 434:    */       throws IOException
/* 435:    */     {
/* 436:404 */       I2C.this.writeArray(paramArrayOfByte, paramInt1, paramInt2);
/* 437:    */     }
/* 438:    */     
/* 439:    */     public void flush()
/* 440:    */       throws IOException
/* 441:    */     {
/* 442:407 */       I2C.this.drain();
/* 443:    */     }
/* 444:    */   }
/* 445:    */   
/* 446:    */   class I2CInputStream
/* 447:    */     extends InputStream
/* 448:    */   {
/* 449:    */     I2CInputStream() {}
/* 450:    */     
/* 451:    */     public int read()
/* 452:    */       throws IOException
/* 453:    */     {
/* 454:414 */       I2C.this.dataAvailable = 0;
/* 455:415 */       return I2C.this.readByte();
/* 456:    */     }
/* 457:    */     
/* 458:    */     public int read(byte[] paramArrayOfByte)
/* 459:    */       throws IOException
/* 460:    */     {
/* 461:419 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/* 462:    */     }
/* 463:    */     
/* 464:    */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 465:    */       throws IOException
/* 466:    */     {
/* 467:423 */       I2C.this.dataAvailable = 0;
/* 468:424 */       int i = 0;int j = 0;
/* 469:425 */       int[] arrayOfInt = { paramArrayOfByte.length, I2C.this.InputBuffer, paramInt2 };
/* 470:437 */       while ((arrayOfInt[i] == 0) && (i < arrayOfInt.length)) {
/* 471:437 */         i++;
/* 472:    */       }
/* 473:438 */       j = arrayOfInt[i];
/* 474:439 */       while (i < arrayOfInt.length)
/* 475:    */       {
/* 476:441 */         if (arrayOfInt[i] > 0) {
/* 477:443 */           j = Math.min(j, arrayOfInt[i]);
/* 478:    */         }
/* 479:445 */         i++;
/* 480:    */       }
/* 481:447 */       j = Math.min(j, I2C.this.threshold);
/* 482:448 */       if (j == 0) {
/* 483:448 */         j = 1;
/* 484:    */       }
/* 485:449 */       int k = available();
/* 486:450 */       int m = I2C.this.readArray(paramArrayOfByte, paramInt1, j);
/* 487:451 */       return m;
/* 488:    */     }
/* 489:    */     
/* 490:    */     public int available()
/* 491:    */       throws IOException
/* 492:    */     {
/* 493:454 */       return I2C.this.nativeavailable();
/* 494:    */     }
/* 495:    */   }
/* 496:    */   
/* 497:    */   class MonitorThread
/* 498:    */     extends Thread
/* 499:    */   {
/* 500:461 */     private boolean CTS = false;
/* 501:462 */     private boolean DSR = false;
/* 502:463 */     private boolean RI = false;
/* 503:464 */     private boolean CD = false;
/* 504:465 */     private boolean OE = false;
/* 505:466 */     private boolean PE = false;
/* 506:467 */     private boolean FE = false;
/* 507:468 */     private boolean BI = false;
/* 508:469 */     private boolean Data = false;
/* 509:470 */     private boolean Output = false;
/* 510:    */     
/* 511:    */     MonitorThread() {}
/* 512:    */     
/* 513:    */     public void run()
/* 514:    */     {
/* 515:473 */       I2C.this.eventLoop();
/* 516:    */     }
/* 517:    */   }
/* 518:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.I2C
 * JD-Core Version:    0.7.0.1
 */