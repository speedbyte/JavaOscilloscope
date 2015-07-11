/*    1:     */ package gnu.io;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.OutputStream;
/*    6:     */ import java.io.PrintStream;
/*    7:     */ import java.util.TooManyListenersException;
/*    8:     */ 
/*    9:     */ public final class RXTXPort
/*   10:     */   extends SerialPort
/*   11:     */ {
/*   12:     */   protected static final boolean debug = false;
/*   13:     */   protected static final boolean debug_read = false;
/*   14:     */   protected static final boolean debug_read_results = false;
/*   15:     */   protected static final boolean debug_write = false;
/*   16:     */   protected static final boolean debug_events = false;
/*   17:     */   protected static final boolean debug_verbose = false;
/*   18:     */   private static Zystem z;
/*   19:     */   
/*   20:     */   static
/*   21:     */   {
/*   22:     */     try
/*   23:     */     {
/*   24:  88 */       z = new Zystem();
/*   25:     */     }
/*   26:     */     catch (Exception localException) {}
/*   27:  93 */     System.loadLibrary("rxtxSerial");
/*   28:  94 */     Initialize();
/*   29:     */   }
/*   30:     */   
/*   31:  99 */   boolean MonitorThreadAlive = false;
/*   32:     */   
/*   33:     */   public RXTXPort(String paramString)
/*   34:     */     throws PortInUseException
/*   35:     */   {
/*   36: 123 */     this.fd = open(paramString);
/*   37: 124 */     this.name = paramString;
/*   38:     */     
/*   39: 126 */     this.MonitorThreadLock = true;
/*   40: 127 */     this.monThread = new MonitorThread();
/*   41: 128 */     this.monThread.start();
/*   42: 129 */     waitForTheNativeCodeSilly();
/*   43: 130 */     this.MonitorThreadAlive = true;
/*   44:     */     
/*   45: 132 */     this.timeout = -1;
/*   46:     */   }
/*   47:     */   
/*   48: 142 */   int IOLocked = 0;
/*   49: 143 */   Object IOLockedMutex = new Object();
/*   50: 146 */   private int fd = 0;
/*   51: 154 */   long eis = 0L;
/*   52: 156 */   int pid = 0;
/*   53: 159 */   static boolean dsrFlag = false;
/*   54: 162 */   private final SerialOutputStream out = new SerialOutputStream();
/*   55:     */   
/*   56:     */   public OutputStream getOutputStream()
/*   57:     */   {
/*   58: 171 */     return this.out;
/*   59:     */   }
/*   60:     */   
/*   61: 175 */   private final SerialInputStream in = new SerialInputStream();
/*   62:     */   
/*   63:     */   public InputStream getInputStream()
/*   64:     */   {
/*   65: 185 */     return this.in;
/*   66:     */   }
/*   67:     */   
/*   68:     */   public synchronized void setSerialPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*   69:     */     throws UnsupportedCommOperationException
/*   70:     */   {
/*   71: 210 */     if (nativeSetSerialPortParams(paramInt1, paramInt2, paramInt3, paramInt4)) {
/*   72: 211 */       throw new UnsupportedCommOperationException("Invalid Parameter");
/*   73:     */     }
/*   74: 213 */     this.speed = paramInt1;
/*   75: 214 */     if (paramInt3 == 3) {
/*   76: 214 */       this.dataBits = 5;
/*   77:     */     } else {
/*   78: 215 */       this.dataBits = paramInt2;
/*   79:     */     }
/*   80: 216 */     this.stopBits = paramInt3;
/*   81: 217 */     this.parity = paramInt4;
/*   82: 218 */     z.reportln("RXTXPort:setSerialPortParams(" + paramInt1 + " " + paramInt2 + " " + paramInt3 + " " + paramInt4 + ") returning");
/*   83:     */   }
/*   84:     */   
/*   85: 233 */   private int speed = 9600;
/*   86:     */   
/*   87:     */   public int getBaudRate()
/*   88:     */   {
/*   89: 242 */     return this.speed;
/*   90:     */   }
/*   91:     */   
/*   92: 246 */   private int dataBits = 8;
/*   93:     */   
/*   94:     */   public int getDataBits()
/*   95:     */   {
/*   96: 254 */     return this.dataBits;
/*   97:     */   }
/*   98:     */   
/*   99: 258 */   private int stopBits = 1;
/*  100:     */   
/*  101:     */   public int getStopBits()
/*  102:     */   {
/*  103: 266 */     return this.stopBits;
/*  104:     */   }
/*  105:     */   
/*  106: 270 */   private int parity = 0;
/*  107:     */   
/*  108:     */   public int getParity()
/*  109:     */   {
/*  110: 278 */     return this.parity;
/*  111:     */   }
/*  112:     */   
/*  113: 283 */   private int flowmode = 0;
/*  114:     */   private int timeout;
/*  115:     */   
/*  116:     */   public void setFlowControlMode(int paramInt)
/*  117:     */   {
/*  118: 292 */     if (this.monThreadisInterrupted) {
/*  119: 296 */       return;
/*  120:     */     }
/*  121:     */     try
/*  122:     */     {
/*  123: 299 */       setflowcontrol(paramInt);
/*  124:     */     }
/*  125:     */     catch (IOException localIOException)
/*  126:     */     {
/*  127: 303 */       localIOException.printStackTrace();
/*  128: 304 */       return;
/*  129:     */     }
/*  130: 306 */     this.flowmode = paramInt;
/*  131:     */   }
/*  132:     */   
/*  133:     */   public int getFlowControlMode()
/*  134:     */   {
/*  135: 317 */     return this.flowmode;
/*  136:     */   }
/*  137:     */   
/*  138:     */   public void enableReceiveFraming(int paramInt)
/*  139:     */     throws UnsupportedCommOperationException
/*  140:     */   {
/*  141: 336 */     throw new UnsupportedCommOperationException("Not supported");
/*  142:     */   }
/*  143:     */   
/*  144:     */   public boolean isReceiveFramingEnabled()
/*  145:     */   {
/*  146: 352 */     return false;
/*  147:     */   }
/*  148:     */   
/*  149:     */   public int getReceiveFramingByte()
/*  150:     */   {
/*  151: 361 */     return 0;
/*  152:     */   }
/*  153:     */   
/*  154:     */   public void disableReceiveTimeout()
/*  155:     */   {
/*  156: 389 */     this.timeout = -1;
/*  157: 390 */     NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
/*  158:     */   }
/*  159:     */   
/*  160:     */   public void enableReceiveTimeout(int paramInt)
/*  161:     */   {
/*  162: 401 */     if (paramInt >= 0)
/*  163:     */     {
/*  164: 403 */       this.timeout = paramInt;
/*  165: 404 */       NativeEnableReceiveTimeoutThreshold(paramInt, this.threshold, this.InputBuffer);
/*  166:     */     }
/*  167:     */     else
/*  168:     */     {
/*  169: 409 */       throw new IllegalArgumentException("Unexpected negative timeout value");
/*  170:     */     }
/*  171:     */   }
/*  172:     */   
/*  173:     */   public boolean isReceiveTimeoutEnabled()
/*  174:     */   {
/*  175: 424 */     return NativeisReceiveTimeoutEnabled();
/*  176:     */   }
/*  177:     */   
/*  178:     */   public int getReceiveTimeout()
/*  179:     */   {
/*  180: 433 */     return NativegetReceiveTimeout();
/*  181:     */   }
/*  182:     */   
/*  183: 438 */   private int threshold = 0;
/*  184:     */   
/*  185:     */   public void enableReceiveThreshold(int paramInt)
/*  186:     */   {
/*  187: 447 */     if (paramInt >= 0)
/*  188:     */     {
/*  189: 449 */       this.threshold = paramInt;
/*  190: 450 */       NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
/*  191:     */     }
/*  192:     */     else
/*  193:     */     {
/*  194: 455 */       throw new IllegalArgumentException("Unexpected negative threshold value");
/*  195:     */     }
/*  196:     */   }
/*  197:     */   
/*  198:     */   public void disableReceiveThreshold()
/*  199:     */   {
/*  200: 469 */     enableReceiveThreshold(0);
/*  201:     */   }
/*  202:     */   
/*  203:     */   public int getReceiveThreshold()
/*  204:     */   {
/*  205: 478 */     return this.threshold;
/*  206:     */   }
/*  207:     */   
/*  208:     */   public boolean isReceiveThresholdEnabled()
/*  209:     */   {
/*  210: 487 */     return this.threshold > 0;
/*  211:     */   }
/*  212:     */   
/*  213: 497 */   private int InputBuffer = 0;
/*  214: 498 */   private int OutputBuffer = 0;
/*  215:     */   private SerialPortEventListener SPEventListener;
/*  216:     */   private MonitorThread monThread;
/*  217:     */   
/*  218:     */   public void setInputBufferSize(int paramInt)
/*  219:     */   {
/*  220: 507 */     if (paramInt < 0) {
/*  221: 508 */       throw new IllegalArgumentException("Unexpected negative buffer size value");
/*  222:     */     }
/*  223: 512 */     this.InputBuffer = paramInt;
/*  224:     */   }
/*  225:     */   
/*  226:     */   public int getInputBufferSize()
/*  227:     */   {
/*  228: 523 */     return this.InputBuffer;
/*  229:     */   }
/*  230:     */   
/*  231:     */   public void setOutputBufferSize(int paramInt)
/*  232:     */   {
/*  233: 533 */     if (paramInt < 0) {
/*  234: 534 */       throw new IllegalArgumentException("Unexpected negative buffer size value");
/*  235:     */     }
/*  236: 538 */     this.OutputBuffer = paramInt;
/*  237:     */   }
/*  238:     */   
/*  239:     */   public int getOutputBufferSize()
/*  240:     */   {
/*  241: 551 */     return this.OutputBuffer;
/*  242:     */   }
/*  243:     */   
/*  244: 625 */   boolean monThreadisInterrupted = true;
/*  245:     */   
/*  246:     */   public boolean checkMonitorThread()
/*  247:     */   {
/*  248: 631 */     if (this.monThread != null) {
/*  249: 637 */       return this.monThreadisInterrupted;
/*  250:     */     }
/*  251: 641 */     return true;
/*  252:     */   }
/*  253:     */   
/*  254:     */   public boolean sendEvent(int paramInt, boolean paramBoolean)
/*  255:     */   {
/*  256: 655 */     if ((this.fd == 0) || (this.SPEventListener == null) || (this.monThread == null)) {
/*  257: 657 */       return true;
/*  258:     */     }
/*  259: 660 */     switch (paramInt)
/*  260:     */     {
/*  261:     */     case 1: 
/*  262:     */       break;
/*  263:     */     case 2: 
/*  264:     */       break;
/*  265:     */     case 3: 
/*  266:     */       break;
/*  267:     */     case 4: 
/*  268:     */       break;
/*  269:     */     case 5: 
/*  270:     */       break;
/*  271:     */     case 6: 
/*  272:     */       break;
/*  273:     */     case 7: 
/*  274:     */       break;
/*  275:     */     case 8: 
/*  276:     */       break;
/*  277:     */     case 9: 
/*  278:     */       break;
/*  279:     */     case 10: 
/*  280:     */       break;
/*  281:     */     }
/*  282: 722 */     switch (paramInt)
/*  283:     */     {
/*  284:     */     case 1: 
/*  285: 725 */       if (!this.monThread.Data) {
/*  286: 726 */         return false;
/*  287:     */       }
/*  288:     */       break;
/*  289:     */     case 2: 
/*  290: 728 */       if (!this.monThread.Output) {
/*  291: 729 */         return false;
/*  292:     */       }
/*  293:     */       break;
/*  294:     */     case 3: 
/*  295: 731 */       if (!this.monThread.CTS) {
/*  296: 732 */         return false;
/*  297:     */       }
/*  298:     */       break;
/*  299:     */     case 4: 
/*  300: 734 */       if (!this.monThread.DSR) {
/*  301: 735 */         return false;
/*  302:     */       }
/*  303:     */       break;
/*  304:     */     case 5: 
/*  305: 737 */       if (!this.monThread.RI) {
/*  306: 738 */         return false;
/*  307:     */       }
/*  308:     */       break;
/*  309:     */     case 6: 
/*  310: 740 */       if (!this.monThread.CD) {
/*  311: 741 */         return false;
/*  312:     */       }
/*  313:     */       break;
/*  314:     */     case 7: 
/*  315: 743 */       if (!this.monThread.OE) {
/*  316: 744 */         return false;
/*  317:     */       }
/*  318:     */       break;
/*  319:     */     case 8: 
/*  320: 746 */       if (!this.monThread.PE) {
/*  321: 747 */         return false;
/*  322:     */       }
/*  323:     */       break;
/*  324:     */     case 9: 
/*  325: 749 */       if (!this.monThread.FE) {
/*  326: 750 */         return false;
/*  327:     */       }
/*  328:     */       break;
/*  329:     */     case 10: 
/*  330: 752 */       if (!this.monThread.BI) {
/*  331: 753 */         return false;
/*  332:     */       }
/*  333:     */       break;
/*  334:     */     default: 
/*  335: 755 */       System.err.println("unknown event: " + paramInt);
/*  336: 756 */       return false;
/*  337:     */     }
/*  338: 760 */     SerialPortEvent localSerialPortEvent = new SerialPortEvent(this, paramInt, !paramBoolean, paramBoolean);
/*  339: 764 */     if (this.monThreadisInterrupted) {
/*  340: 768 */       return true;
/*  341:     */     }
/*  342: 770 */     if (this.SPEventListener != null) {
/*  343: 772 */       this.SPEventListener.serialEvent(localSerialPortEvent);
/*  344:     */     }
/*  345: 778 */     if ((this.fd == 0) || (this.SPEventListener == null) || (this.monThread == null)) {
/*  346: 780 */       return true;
/*  347:     */     }
/*  348: 784 */     return false;
/*  349:     */   }
/*  350:     */   
/*  351: 794 */   boolean MonitorThreadLock = true;
/*  352:     */   
/*  353:     */   public void addEventListener(SerialPortEventListener paramSerialPortEventListener)
/*  354:     */     throws TooManyListenersException
/*  355:     */   {
/*  356: 805 */     if (this.SPEventListener != null) {
/*  357: 807 */       throw new TooManyListenersException();
/*  358:     */     }
/*  359: 809 */     this.SPEventListener = paramSerialPortEventListener;
/*  360: 810 */     if (!this.MonitorThreadAlive)
/*  361:     */     {
/*  362: 812 */       this.MonitorThreadLock = true;
/*  363: 813 */       this.monThread = new MonitorThread();
/*  364: 814 */       this.monThread.start();
/*  365: 815 */       waitForTheNativeCodeSilly();
/*  366: 816 */       this.MonitorThreadAlive = true;
/*  367:     */     }
/*  368:     */   }
/*  369:     */   
/*  370:     */   public void removeEventListener()
/*  371:     */   {
/*  372: 828 */     waitForTheNativeCodeSilly();
/*  373: 830 */     if (this.monThreadisInterrupted == true)
/*  374:     */     {
/*  375: 832 */       z.reportln("\tRXTXPort:removeEventListener() already interrupted");
/*  376: 833 */       this.monThread = null;
/*  377: 834 */       this.SPEventListener = null;
/*  378: 835 */       return;
/*  379:     */     }
/*  380: 837 */     if ((this.monThread != null) && (this.monThread.isAlive()))
/*  381:     */     {
/*  382: 841 */       this.monThreadisInterrupted = true;
/*  383:     */       
/*  384:     */ 
/*  385:     */ 
/*  386:     */ 
/*  387:     */ 
/*  388:     */ 
/*  389:     */ 
/*  390: 849 */       interruptEventLoop();
/*  391:     */       try
/*  392:     */       {
/*  393: 856 */         this.monThread.join(3000L);
/*  394:     */       }
/*  395:     */       catch (InterruptedException localInterruptedException)
/*  396:     */       {
/*  397: 860 */         Thread.currentThread().interrupt();
/*  398: 861 */         return;
/*  399:     */       }
/*  400:     */     }
/*  401: 871 */     this.monThread = null;
/*  402: 872 */     this.SPEventListener = null;
/*  403: 873 */     this.MonitorThreadLock = false;
/*  404: 874 */     this.MonitorThreadAlive = false;
/*  405: 875 */     this.monThreadisInterrupted = true;
/*  406: 876 */     z.reportln("RXTXPort:removeEventListener() returning");
/*  407:     */   }
/*  408:     */   
/*  409:     */   protected void waitForTheNativeCodeSilly()
/*  410:     */   {
/*  411: 889 */     while (this.MonitorThreadLock) {
/*  412:     */       try
/*  413:     */       {
/*  414: 892 */         Thread.sleep(5L);
/*  415:     */       }
/*  416:     */       catch (Exception localException) {}
/*  417:     */     }
/*  418:     */   }
/*  419:     */   
/*  420:     */   public void notifyOnDataAvailable(boolean paramBoolean)
/*  421:     */   {
/*  422: 907 */     waitForTheNativeCodeSilly();
/*  423:     */     
/*  424: 909 */     this.MonitorThreadLock = true;
/*  425: 910 */     nativeSetEventFlag(this.fd, 1, paramBoolean);
/*  426:     */     
/*  427: 912 */     this.monThread.Data = paramBoolean;
/*  428: 913 */     this.MonitorThreadLock = false;
/*  429:     */   }
/*  430:     */   
/*  431:     */   public void notifyOnOutputEmpty(boolean paramBoolean)
/*  432:     */   {
/*  433: 924 */     waitForTheNativeCodeSilly();
/*  434: 925 */     this.MonitorThreadLock = true;
/*  435: 926 */     nativeSetEventFlag(this.fd, 2, paramBoolean);
/*  436:     */     
/*  437: 928 */     this.monThread.Output = paramBoolean;
/*  438: 929 */     this.MonitorThreadLock = false;
/*  439:     */   }
/*  440:     */   
/*  441:     */   public void notifyOnCTS(boolean paramBoolean)
/*  442:     */   {
/*  443: 940 */     waitForTheNativeCodeSilly();
/*  444: 941 */     this.MonitorThreadLock = true;
/*  445: 942 */     nativeSetEventFlag(this.fd, 3, paramBoolean);
/*  446: 943 */     this.monThread.CTS = paramBoolean;
/*  447: 944 */     this.MonitorThreadLock = false;
/*  448:     */   }
/*  449:     */   
/*  450:     */   public void notifyOnDSR(boolean paramBoolean)
/*  451:     */   {
/*  452: 954 */     waitForTheNativeCodeSilly();
/*  453: 955 */     this.MonitorThreadLock = true;
/*  454: 956 */     nativeSetEventFlag(this.fd, 4, paramBoolean);
/*  455: 957 */     this.monThread.DSR = paramBoolean;
/*  456: 958 */     this.MonitorThreadLock = false;
/*  457:     */   }
/*  458:     */   
/*  459:     */   public void notifyOnRingIndicator(boolean paramBoolean)
/*  460:     */   {
/*  461: 968 */     waitForTheNativeCodeSilly();
/*  462: 969 */     this.MonitorThreadLock = true;
/*  463: 970 */     nativeSetEventFlag(this.fd, 5, paramBoolean);
/*  464: 971 */     this.monThread.RI = paramBoolean;
/*  465: 972 */     this.MonitorThreadLock = false;
/*  466:     */   }
/*  467:     */   
/*  468:     */   public void notifyOnCarrierDetect(boolean paramBoolean)
/*  469:     */   {
/*  470: 982 */     waitForTheNativeCodeSilly();
/*  471: 983 */     this.MonitorThreadLock = true;
/*  472: 984 */     nativeSetEventFlag(this.fd, 6, paramBoolean);
/*  473: 985 */     this.monThread.CD = paramBoolean;
/*  474: 986 */     this.MonitorThreadLock = false;
/*  475:     */   }
/*  476:     */   
/*  477:     */   public void notifyOnOverrunError(boolean paramBoolean)
/*  478:     */   {
/*  479: 996 */     waitForTheNativeCodeSilly();
/*  480: 997 */     this.MonitorThreadLock = true;
/*  481: 998 */     nativeSetEventFlag(this.fd, 7, paramBoolean);
/*  482: 999 */     this.monThread.OE = paramBoolean;
/*  483:1000 */     this.MonitorThreadLock = false;
/*  484:     */   }
/*  485:     */   
/*  486:     */   public void notifyOnParityError(boolean paramBoolean)
/*  487:     */   {
/*  488:1010 */     waitForTheNativeCodeSilly();
/*  489:1011 */     this.MonitorThreadLock = true;
/*  490:1012 */     nativeSetEventFlag(this.fd, 8, paramBoolean);
/*  491:1013 */     this.monThread.PE = paramBoolean;
/*  492:1014 */     this.MonitorThreadLock = false;
/*  493:     */   }
/*  494:     */   
/*  495:     */   public void notifyOnFramingError(boolean paramBoolean)
/*  496:     */   {
/*  497:1024 */     waitForTheNativeCodeSilly();
/*  498:1025 */     this.MonitorThreadLock = true;
/*  499:1026 */     nativeSetEventFlag(this.fd, 9, paramBoolean);
/*  500:1027 */     this.monThread.FE = paramBoolean;
/*  501:1028 */     this.MonitorThreadLock = false;
/*  502:     */   }
/*  503:     */   
/*  504:     */   public void notifyOnBreakInterrupt(boolean paramBoolean)
/*  505:     */   {
/*  506:1038 */     waitForTheNativeCodeSilly();
/*  507:1039 */     this.MonitorThreadLock = true;
/*  508:1040 */     nativeSetEventFlag(this.fd, 10, paramBoolean);
/*  509:1041 */     this.monThread.BI = paramBoolean;
/*  510:1042 */     this.MonitorThreadLock = false;
/*  511:     */   }
/*  512:     */   
/*  513:1049 */   boolean closeLock = false;
/*  514:     */   
/*  515:     */   public void close()
/*  516:     */   {
/*  517:1052 */     synchronized (this)
/*  518:     */     {
/*  519:1056 */       while (this.IOLocked > 0) {
/*  520:     */         try
/*  521:     */         {
/*  522:1061 */           wait(500L);
/*  523:     */         }
/*  524:     */         catch (InterruptedException localInterruptedException)
/*  525:     */         {
/*  526:1065 */           Thread.currentThread().interrupt();
/*  527:1066 */           return;
/*  528:     */         }
/*  529:     */       }
/*  530:1072 */       if (this.closeLock) {
/*  531:1072 */         return;
/*  532:     */       }
/*  533:1073 */       this.closeLock = true;
/*  534:     */     }
/*  535:1076 */     if (this.fd <= 0)
/*  536:     */     {
/*  537:1078 */       z.reportln("RXTXPort:close detected bad File Descriptor");
/*  538:1079 */       return;
/*  539:     */     }
/*  540:1081 */     setDTR(false);
/*  541:1082 */     setDSR(false);
/*  542:1085 */     if (!this.monThreadisInterrupted) {
/*  543:1087 */       removeEventListener();
/*  544:     */     }
/*  545:1091 */     nativeClose(this.name);
/*  546:     */     
/*  547:     */ 
/*  548:1094 */     super.close();
/*  549:1095 */     this.fd = 0;
/*  550:1096 */     this.closeLock = false;
/*  551:     */   }
/*  552:     */   
/*  553:     */   protected void finalize()
/*  554:     */   {
/*  555:1107 */     if (this.fd > 0) {
/*  556:1111 */       close();
/*  557:     */     }
/*  558:1113 */     z.finalize();
/*  559:     */   }
/*  560:     */   
/*  561:     */   class SerialOutputStream
/*  562:     */     extends OutputStream
/*  563:     */   {
/*  564:     */     SerialOutputStream() {}
/*  565:     */     
/*  566:     */     public void write(int paramInt)
/*  567:     */       throws IOException
/*  568:     */     {
/*  569:1127 */       if (RXTXPort.this.speed == 0) {
/*  570:1127 */         return;
/*  571:     */       }
/*  572:1128 */       if (RXTXPort.this.monThreadisInterrupted == true) {
/*  573:1130 */         return;
/*  574:     */       }
/*  575:1132 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  576:     */       {
/*  577:1133 */         RXTXPort.this.IOLocked += 1;
/*  578:     */       }
/*  579:     */       try
/*  580:     */       {
/*  581:1136 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*  582:1137 */         if (RXTXPort.this.fd == 0) {
/*  583:1139 */           throw new IOException();
/*  584:     */         }
/*  585:1141 */         RXTXPort.this.writeByte(paramInt, RXTXPort.this.monThreadisInterrupted);
/*  586:     */       }
/*  587:     */       finally
/*  588:     */       {
/*  589:1145 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  590:     */         {
/*  591:1146 */           RXTXPort.this.IOLocked -= 1;
/*  592:     */         }
/*  593:     */       }
/*  594:     */     }
/*  595:     */     
/*  596:     */     public void write(byte[] paramArrayOfByte)
/*  597:     */       throws IOException
/*  598:     */     {
/*  599:1160 */       if (RXTXPort.this.speed == 0) {
/*  600:1160 */         return;
/*  601:     */       }
/*  602:1161 */       if (RXTXPort.this.monThreadisInterrupted == true) {
/*  603:1163 */         return;
/*  604:     */       }
/*  605:1165 */       if (RXTXPort.this.fd == 0) {
/*  606:1165 */         throw new IOException();
/*  607:     */       }
/*  608:1166 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  609:     */       {
/*  610:1167 */         RXTXPort.this.IOLocked += 1;
/*  611:     */       }
/*  612:     */       try
/*  613:     */       {
/*  614:1170 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*  615:1171 */         RXTXPort.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length, RXTXPort.this.monThreadisInterrupted);
/*  616:     */       }
/*  617:     */       finally
/*  618:     */       {
/*  619:1175 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  620:     */         {
/*  621:1176 */           RXTXPort.this.IOLocked -= 1;
/*  622:     */         }
/*  623:     */       }
/*  624:     */     }
/*  625:     */     
/*  626:     */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*  627:     */       throws IOException
/*  628:     */     {
/*  629:1190 */       if (RXTXPort.this.speed == 0) {
/*  630:1190 */         return;
/*  631:     */       }
/*  632:1191 */       if (paramInt1 + paramInt2 > paramArrayOfByte.length) {
/*  633:1193 */         throw new IndexOutOfBoundsException("Invalid offset/length passed to read");
/*  634:     */       }
/*  635:1198 */       byte[] arrayOfByte = new byte[paramInt2];
/*  636:1199 */       System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
/*  637:1204 */       if (RXTXPort.this.fd == 0) {
/*  638:1204 */         throw new IOException();
/*  639:     */       }
/*  640:1205 */       if (RXTXPort.this.monThreadisInterrupted == true) {
/*  641:1207 */         return;
/*  642:     */       }
/*  643:1209 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  644:     */       {
/*  645:1210 */         RXTXPort.this.IOLocked += 1;
/*  646:     */       }
/*  647:     */       try
/*  648:     */       {
/*  649:1214 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*  650:1215 */         RXTXPort.this.writeArray(arrayOfByte, 0, paramInt2, RXTXPort.this.monThreadisInterrupted);
/*  651:     */       }
/*  652:     */       finally
/*  653:     */       {
/*  654:1219 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  655:     */         {
/*  656:1220 */           RXTXPort.this.IOLocked -= 1;
/*  657:     */         }
/*  658:     */       }
/*  659:     */     }
/*  660:     */     
/*  661:     */     public void flush()
/*  662:     */       throws IOException
/*  663:     */     {
/*  664:1230 */       if (RXTXPort.this.speed == 0) {
/*  665:1230 */         return;
/*  666:     */       }
/*  667:1231 */       if (RXTXPort.this.fd == 0) {
/*  668:1231 */         throw new IOException();
/*  669:     */       }
/*  670:1232 */       if (RXTXPort.this.monThreadisInterrupted == true) {
/*  671:1236 */         return;
/*  672:     */       }
/*  673:1238 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  674:     */       {
/*  675:1239 */         RXTXPort.this.IOLocked += 1;
/*  676:     */       }
/*  677:     */       try
/*  678:     */       {
/*  679:1243 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*  680:1248 */         if (RXTXPort.this.nativeDrain(RXTXPort.this.monThreadisInterrupted)) {
/*  681:1249 */           RXTXPort.this.sendEvent(2, true);
/*  682:     */         }
/*  683:     */       }
/*  684:     */       finally
/*  685:     */       {
/*  686:1255 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  687:     */         {
/*  688:1256 */           RXTXPort.this.IOLocked -= 1;
/*  689:     */         }
/*  690:     */       }
/*  691:     */     }
/*  692:     */   }
/*  693:     */   
/*  694:     */   class SerialInputStream
/*  695:     */     extends InputStream
/*  696:     */   {
/*  697:     */     SerialInputStream() {}
/*  698:     */     
/*  699:     */     public synchronized int read()
/*  700:     */       throws IOException
/*  701:     */     {
/*  702:1284 */       if (RXTXPort.this.fd == 0) {
/*  703:1284 */         throw new IOException();
/*  704:     */       }
/*  705:1285 */       if (RXTXPort.this.monThreadisInterrupted) {
/*  706:1287 */         RXTXPort.z.reportln("+++++++++ read() monThreadisInterrupted");
/*  707:     */       }
/*  708:1289 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  709:     */       {
/*  710:1290 */         RXTXPort.this.IOLocked += 1;
/*  711:     */       }
/*  712:     */       try
/*  713:     */       {
/*  714:1295 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*  715:     */         
/*  716:     */ 
/*  717:1298 */         Object localObject1 = RXTXPort.this.readByte();
/*  718:     */         
/*  719:     */ 
/*  720:     */ 
/*  721:1302 */         return localObject1;
/*  722:     */       }
/*  723:     */       finally
/*  724:     */       {
/*  725:1306 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  726:     */         {
/*  727:1307 */           RXTXPort.this.IOLocked -= 1;
/*  728:     */         }
/*  729:     */       }
/*  730:     */     }
/*  731:     */     
/*  732:     */     public synchronized int read(byte[] paramArrayOfByte)
/*  733:     */       throws IOException
/*  734:     */     {
/*  735:1329 */       if (RXTXPort.this.monThreadisInterrupted == true) {
/*  736:1331 */         return 0;
/*  737:     */       }
/*  738:1333 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  739:     */       {
/*  740:1334 */         RXTXPort.this.IOLocked += 1;
/*  741:     */       }
/*  742:     */       try
/*  743:     */       {
/*  744:1338 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*  745:1339 */         Object localObject1 = read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*  746:     */         
/*  747:     */ 
/*  748:1342 */         return localObject1;
/*  749:     */       }
/*  750:     */       finally
/*  751:     */       {
/*  752:1346 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  753:     */         {
/*  754:1347 */           RXTXPort.this.IOLocked -= 1;
/*  755:     */         }
/*  756:     */       }
/*  757:     */     }
/*  758:     */     
/*  759:     */     public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*  760:     */       throws IOException
/*  761:     */     {
/*  762:1380 */       if (RXTXPort.this.fd == 0)
/*  763:     */       {
/*  764:1384 */         RXTXPort.z.reportln("+++++++ IOException()\n");
/*  765:1385 */         throw new IOException();
/*  766:     */       }
/*  767:1388 */       if (paramArrayOfByte == null)
/*  768:     */       {
/*  769:1390 */         RXTXPort.z.reportln("+++++++ NullPointerException()\n");
/*  770:     */         
/*  771:     */ 
/*  772:1393 */         throw new NullPointerException();
/*  773:     */       }
/*  774:1396 */       if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length))
/*  775:     */       {
/*  776:1398 */         RXTXPort.z.reportln("+++++++ IndexOutOfBoundsException()\n");
/*  777:     */         
/*  778:     */ 
/*  779:1401 */         throw new IndexOutOfBoundsException();
/*  780:     */       }
/*  781:1407 */       if (paramInt2 == 0) {
/*  782:1411 */         return 0;
/*  783:     */       }
/*  784:1416 */       int i = paramInt2;
/*  785:1418 */       if (RXTXPort.this.threshold == 0)
/*  786:     */       {
/*  787:1427 */         int j = RXTXPort.this.nativeavailable();
/*  788:1428 */         if (j == 0) {
/*  789:1429 */           i = 1;
/*  790:     */         } else {
/*  791:1431 */           i = Math.min(i, j);
/*  792:     */         }
/*  793:     */       }
/*  794:     */       else
/*  795:     */       {
/*  796:1440 */         i = Math.min(i, RXTXPort.this.threshold);
/*  797:     */       }
/*  798:1442 */       if (RXTXPort.this.monThreadisInterrupted == true) {
/*  799:1446 */         return 0;
/*  800:     */       }
/*  801:1448 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  802:     */       {
/*  803:1449 */         RXTXPort.this.IOLocked += 1;
/*  804:     */       }
/*  805:     */       try
/*  806:     */       {
/*  807:1453 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*  808:1454 */         Object localObject1 = RXTXPort.this.readArray(paramArrayOfByte, paramInt1, i);
/*  809:     */         
/*  810:     */ 
/*  811:1457 */         return localObject1;
/*  812:     */       }
/*  813:     */       finally
/*  814:     */       {
/*  815:1461 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  816:     */         {
/*  817:1462 */           RXTXPort.this.IOLocked -= 1;
/*  818:     */         }
/*  819:     */       }
/*  820:     */     }
/*  821:     */     
/*  822:     */     public synchronized int read(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
/*  823:     */       throws IOException
/*  824:     */     {
/*  825:1493 */       if (RXTXPort.this.fd == 0)
/*  826:     */       {
/*  827:1497 */         RXTXPort.z.reportln("+++++++ IOException()\n");
/*  828:1498 */         throw new IOException();
/*  829:     */       }
/*  830:1501 */       if (paramArrayOfByte1 == null)
/*  831:     */       {
/*  832:1503 */         RXTXPort.z.reportln("+++++++ NullPointerException()\n");
/*  833:     */         
/*  834:     */ 
/*  835:1506 */         throw new NullPointerException();
/*  836:     */       }
/*  837:1509 */       if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte1.length))
/*  838:     */       {
/*  839:1511 */         RXTXPort.z.reportln("+++++++ IndexOutOfBoundsException()\n");
/*  840:     */         
/*  841:     */ 
/*  842:1514 */         throw new IndexOutOfBoundsException();
/*  843:     */       }
/*  844:1520 */       if (paramInt2 == 0) {
/*  845:1524 */         return 0;
/*  846:     */       }
/*  847:1529 */       int i = paramInt2;
/*  848:1531 */       if (RXTXPort.this.threshold == 0)
/*  849:     */       {
/*  850:1540 */         int j = RXTXPort.this.nativeavailable();
/*  851:1541 */         if (j == 0) {
/*  852:1542 */           i = 1;
/*  853:     */         } else {
/*  854:1544 */           i = Math.min(i, j);
/*  855:     */         }
/*  856:     */       }
/*  857:     */       else
/*  858:     */       {
/*  859:1553 */         i = Math.min(i, RXTXPort.this.threshold);
/*  860:     */       }
/*  861:1555 */       if (RXTXPort.this.monThreadisInterrupted == true) {
/*  862:1559 */         return 0;
/*  863:     */       }
/*  864:1561 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  865:     */       {
/*  866:1562 */         RXTXPort.this.IOLocked += 1;
/*  867:     */       }
/*  868:     */       try
/*  869:     */       {
/*  870:1566 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*  871:1567 */         Object localObject1 = RXTXPort.this.readTerminatedArray(paramArrayOfByte1, paramInt1, i, paramArrayOfByte2);
/*  872:     */         
/*  873:     */ 
/*  874:1570 */         return localObject1;
/*  875:     */       }
/*  876:     */       finally
/*  877:     */       {
/*  878:1574 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  879:     */         {
/*  880:1575 */           RXTXPort.this.IOLocked -= 1;
/*  881:     */         }
/*  882:     */       }
/*  883:     */     }
/*  884:     */     
/*  885:     */     public synchronized int available()
/*  886:     */       throws IOException
/*  887:     */     {
/*  888:1585 */       if (RXTXPort.this.monThreadisInterrupted == true) {
/*  889:1587 */         return 0;
/*  890:     */       }
/*  891:1591 */       synchronized (RXTXPort.this.IOLockedMutex)
/*  892:     */       {
/*  893:1592 */         RXTXPort.this.IOLocked += 1;
/*  894:     */       }
/*  895:     */       try
/*  896:     */       {
/*  897:1596 */         Object localObject1 = RXTXPort.this.nativeavailable();
/*  898:     */         
/*  899:     */ 
/*  900:     */ 
/*  901:1600 */         return localObject1;
/*  902:     */       }
/*  903:     */       finally
/*  904:     */       {
/*  905:1604 */         synchronized (RXTXPort.this.IOLockedMutex)
/*  906:     */         {
/*  907:1605 */           RXTXPort.this.IOLocked -= 1;
/*  908:     */         }
/*  909:     */       }
/*  910:     */     }
/*  911:     */   }
/*  912:     */   
/*  913:     */   class MonitorThread
/*  914:     */     extends Thread
/*  915:     */   {
/*  916:1617 */     private volatile boolean CTS = false;
/*  917:1618 */     private volatile boolean DSR = false;
/*  918:1619 */     private volatile boolean RI = false;
/*  919:1620 */     private volatile boolean CD = false;
/*  920:1621 */     private volatile boolean OE = false;
/*  921:1622 */     private volatile boolean PE = false;
/*  922:1623 */     private volatile boolean FE = false;
/*  923:1624 */     private volatile boolean BI = false;
/*  924:1625 */     private volatile boolean Data = false;
/*  925:1626 */     private volatile boolean Output = false;
/*  926:     */     
/*  927:     */     MonitorThread() {}
/*  928:     */     
/*  929:     */     public void run()
/*  930:     */     {
/*  931:1640 */       RXTXPort.this.monThreadisInterrupted = false;
/*  932:1641 */       RXTXPort.this.eventLoop();
/*  933:     */     }
/*  934:     */     
/*  935:     */     protected void finalize()
/*  936:     */       throws Throwable
/*  937:     */     {}
/*  938:     */   }
/*  939:     */   
/*  940:     */   public static int staticGetBaudRate(String paramString)
/*  941:     */     throws UnsupportedCommOperationException
/*  942:     */   {
/*  943:1745 */     return nativeStaticGetBaudRate(paramString);
/*  944:     */   }
/*  945:     */   
/*  946:     */   public static int staticGetDataBits(String paramString)
/*  947:     */     throws UnsupportedCommOperationException
/*  948:     */   {
/*  949:1763 */     return nativeStaticGetDataBits(paramString);
/*  950:     */   }
/*  951:     */   
/*  952:     */   public static int staticGetParity(String paramString)
/*  953:     */     throws UnsupportedCommOperationException
/*  954:     */   {
/*  955:1782 */     return nativeStaticGetParity(paramString);
/*  956:     */   }
/*  957:     */   
/*  958:     */   public static int staticGetStopBits(String paramString)
/*  959:     */     throws UnsupportedCommOperationException
/*  960:     */   {
/*  961:1801 */     return nativeStaticGetStopBits(paramString);
/*  962:     */   }
/*  963:     */   
/*  964:     */   public static void staticSetSerialPortParams(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*  965:     */     throws UnsupportedCommOperationException
/*  966:     */   {
/*  967:1829 */     nativeStaticSetSerialPortParams(paramString, paramInt1, paramInt2, paramInt3, paramInt4);
/*  968:     */   }
/*  969:     */   
/*  970:     */   public static boolean staticSetDSR(String paramString, boolean paramBoolean)
/*  971:     */     throws UnsupportedCommOperationException
/*  972:     */   {
/*  973:1852 */     return nativeStaticSetDSR(paramString, paramBoolean);
/*  974:     */   }
/*  975:     */   
/*  976:     */   public static boolean staticSetDTR(String paramString, boolean paramBoolean)
/*  977:     */     throws UnsupportedCommOperationException
/*  978:     */   {
/*  979:1875 */     return nativeStaticSetDTR(paramString, paramBoolean);
/*  980:     */   }
/*  981:     */   
/*  982:     */   public static boolean staticSetRTS(String paramString, boolean paramBoolean)
/*  983:     */     throws UnsupportedCommOperationException
/*  984:     */   {
/*  985:1898 */     return nativeStaticSetRTS(paramString, paramBoolean);
/*  986:     */   }
/*  987:     */   
/*  988:     */   public static boolean staticIsRTS(String paramString)
/*  989:     */     throws UnsupportedCommOperationException
/*  990:     */   {
/*  991:1919 */     return nativeStaticIsRTS(paramString);
/*  992:     */   }
/*  993:     */   
/*  994:     */   public static boolean staticIsCD(String paramString)
/*  995:     */     throws UnsupportedCommOperationException
/*  996:     */   {
/*  997:1939 */     return nativeStaticIsCD(paramString);
/*  998:     */   }
/*  999:     */   
/* 1000:     */   public static boolean staticIsCTS(String paramString)
/* 1001:     */     throws UnsupportedCommOperationException
/* 1002:     */   {
/* 1003:1959 */     return nativeStaticIsCTS(paramString);
/* 1004:     */   }
/* 1005:     */   
/* 1006:     */   public static boolean staticIsDSR(String paramString)
/* 1007:     */     throws UnsupportedCommOperationException
/* 1008:     */   {
/* 1009:1979 */     return nativeStaticIsDSR(paramString);
/* 1010:     */   }
/* 1011:     */   
/* 1012:     */   public static boolean staticIsDTR(String paramString)
/* 1013:     */     throws UnsupportedCommOperationException
/* 1014:     */   {
/* 1015:1999 */     return nativeStaticIsDTR(paramString);
/* 1016:     */   }
/* 1017:     */   
/* 1018:     */   public static boolean staticIsRI(String paramString)
/* 1019:     */     throws UnsupportedCommOperationException
/* 1020:     */   {
/* 1021:2019 */     return nativeStaticIsRI(paramString);
/* 1022:     */   }
/* 1023:     */   
/* 1024:     */   public byte getParityErrorChar()
/* 1025:     */     throws UnsupportedCommOperationException
/* 1026:     */   {
/* 1027:2039 */     byte b = nativeGetParityErrorChar();
/* 1028:     */     
/* 1029:     */ 
/* 1030:     */ 
/* 1031:2043 */     return b;
/* 1032:     */   }
/* 1033:     */   
/* 1034:     */   public boolean setParityErrorChar(byte paramByte)
/* 1035:     */     throws UnsupportedCommOperationException
/* 1036:     */   {
/* 1037:2062 */     return nativeSetParityErrorChar(paramByte);
/* 1038:     */   }
/* 1039:     */   
/* 1040:     */   public byte getEndOfInputChar()
/* 1041:     */     throws UnsupportedCommOperationException
/* 1042:     */   {
/* 1043:2081 */     byte b = nativeGetEndOfInputChar();
/* 1044:     */     
/* 1045:     */ 
/* 1046:     */ 
/* 1047:2085 */     return b;
/* 1048:     */   }
/* 1049:     */   
/* 1050:     */   public boolean setEndOfInputChar(byte paramByte)
/* 1051:     */     throws UnsupportedCommOperationException
/* 1052:     */   {
/* 1053:2102 */     return nativeSetEndOfInputChar(paramByte);
/* 1054:     */   }
/* 1055:     */   
/* 1056:     */   public boolean setUARTType(String paramString, boolean paramBoolean)
/* 1057:     */     throws UnsupportedCommOperationException
/* 1058:     */   {
/* 1059:2121 */     return nativeSetUartType(paramString, paramBoolean);
/* 1060:     */   }
/* 1061:     */   
/* 1062:     */   public String getUARTType()
/* 1063:     */     throws UnsupportedCommOperationException
/* 1064:     */   {
/* 1065:2134 */     return nativeGetUartType();
/* 1066:     */   }
/* 1067:     */   
/* 1068:     */   public boolean setBaudBase(int paramInt)
/* 1069:     */     throws UnsupportedCommOperationException, IOException
/* 1070:     */   {
/* 1071:2152 */     return nativeSetBaudBase(paramInt);
/* 1072:     */   }
/* 1073:     */   
/* 1074:     */   public int getBaudBase()
/* 1075:     */     throws UnsupportedCommOperationException, IOException
/* 1076:     */   {
/* 1077:2166 */     return nativeGetBaudBase();
/* 1078:     */   }
/* 1079:     */   
/* 1080:     */   public boolean setDivisor(int paramInt)
/* 1081:     */     throws UnsupportedCommOperationException, IOException
/* 1082:     */   {
/* 1083:2181 */     return nativeSetDivisor(paramInt);
/* 1084:     */   }
/* 1085:     */   
/* 1086:     */   public int getDivisor()
/* 1087:     */     throws UnsupportedCommOperationException, IOException
/* 1088:     */   {
/* 1089:2195 */     return nativeGetDivisor();
/* 1090:     */   }
/* 1091:     */   
/* 1092:     */   public boolean setLowLatency()
/* 1093:     */     throws UnsupportedCommOperationException
/* 1094:     */   {
/* 1095:2208 */     return nativeSetLowLatency();
/* 1096:     */   }
/* 1097:     */   
/* 1098:     */   public boolean getLowLatency()
/* 1099:     */     throws UnsupportedCommOperationException
/* 1100:     */   {
/* 1101:2221 */     return nativeGetLowLatency();
/* 1102:     */   }
/* 1103:     */   
/* 1104:     */   public boolean setCallOutHangup(boolean paramBoolean)
/* 1105:     */     throws UnsupportedCommOperationException
/* 1106:     */   {
/* 1107:2235 */     return nativeSetCallOutHangup(paramBoolean);
/* 1108:     */   }
/* 1109:     */   
/* 1110:     */   public boolean getCallOutHangup()
/* 1111:     */     throws UnsupportedCommOperationException
/* 1112:     */   {
/* 1113:2249 */     return nativeGetCallOutHangup();
/* 1114:     */   }
/* 1115:     */   
/* 1116:     */   public boolean clearCommInput()
/* 1117:     */     throws UnsupportedCommOperationException
/* 1118:     */   {
/* 1119:2263 */     return nativeClearCommInput();
/* 1120:     */   }
/* 1121:     */   
/* 1122:     */   private static native void Initialize();
/* 1123:     */   
/* 1124:     */   private synchronized native int open(String paramString)
/* 1125:     */     throws PortInUseException;
/* 1126:     */   
/* 1127:     */   private native int nativeGetParity(int paramInt);
/* 1128:     */   
/* 1129:     */   private native int nativeGetFlowControlMode(int paramInt);
/* 1130:     */   
/* 1131:     */   private native boolean nativeSetSerialPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/* 1132:     */     throws UnsupportedCommOperationException;
/* 1133:     */   
/* 1134:     */   native void setflowcontrol(int paramInt)
/* 1135:     */     throws IOException;
/* 1136:     */   
/* 1137:     */   public void disableReceiveFraming() {}
/* 1138:     */   
/* 1139:     */   public native int NativegetReceiveTimeout();
/* 1140:     */   
/* 1141:     */   private native boolean NativeisReceiveTimeoutEnabled();
/* 1142:     */   
/* 1143:     */   private native void NativeEnableReceiveTimeoutThreshold(int paramInt1, int paramInt2, int paramInt3);
/* 1144:     */   
/* 1145:     */   public native boolean isDTR();
/* 1146:     */   
/* 1147:     */   public native void setDTR(boolean paramBoolean);
/* 1148:     */   
/* 1149:     */   public native void setRTS(boolean paramBoolean);
/* 1150:     */   
/* 1151:     */   private native void setDSR(boolean paramBoolean);
/* 1152:     */   
/* 1153:     */   public native boolean isCTS();
/* 1154:     */   
/* 1155:     */   public native boolean isDSR();
/* 1156:     */   
/* 1157:     */   public native boolean isCD();
/* 1158:     */   
/* 1159:     */   public native boolean isRI();
/* 1160:     */   
/* 1161:     */   public native boolean isRTS();
/* 1162:     */   
/* 1163:     */   public native void sendBreak(int paramInt);
/* 1164:     */   
/* 1165:     */   protected native void writeByte(int paramInt, boolean paramBoolean)
/* 1166:     */     throws IOException;
/* 1167:     */   
/* 1168:     */   protected native void writeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
/* 1169:     */     throws IOException;
/* 1170:     */   
/* 1171:     */   protected native boolean nativeDrain(boolean paramBoolean)
/* 1172:     */     throws IOException;
/* 1173:     */   
/* 1174:     */   protected native int nativeavailable()
/* 1175:     */     throws IOException;
/* 1176:     */   
/* 1177:     */   protected native int readByte()
/* 1178:     */     throws IOException;
/* 1179:     */   
/* 1180:     */   protected native int readArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 1181:     */     throws IOException;
/* 1182:     */   
/* 1183:     */   protected native int readTerminatedArray(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
/* 1184:     */     throws IOException;
/* 1185:     */   
/* 1186:     */   native void eventLoop();
/* 1187:     */   
/* 1188:     */   private native void interruptEventLoop();
/* 1189:     */   
/* 1190:     */   private native void nativeSetEventFlag(int paramInt1, int paramInt2, boolean paramBoolean);
/* 1191:     */   
/* 1192:     */   private native void nativeClose(String paramString);
/* 1193:     */   
/* 1194:     */   /**
/* 1195:     */    * @deprecated
/* 1196:     */    */
/* 1197:     */   public void setRcvFifoTrigger(int paramInt) {}
/* 1198:     */   
/* 1199:     */   private static native void nativeStaticSetSerialPortParams(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/* 1200:     */     throws UnsupportedCommOperationException;
/* 1201:     */   
/* 1202:     */   private static native boolean nativeStaticSetDSR(String paramString, boolean paramBoolean)
/* 1203:     */     throws UnsupportedCommOperationException;
/* 1204:     */   
/* 1205:     */   private static native boolean nativeStaticSetDTR(String paramString, boolean paramBoolean)
/* 1206:     */     throws UnsupportedCommOperationException;
/* 1207:     */   
/* 1208:     */   private static native boolean nativeStaticSetRTS(String paramString, boolean paramBoolean)
/* 1209:     */     throws UnsupportedCommOperationException;
/* 1210:     */   
/* 1211:     */   private static native boolean nativeStaticIsDSR(String paramString)
/* 1212:     */     throws UnsupportedCommOperationException;
/* 1213:     */   
/* 1214:     */   private static native boolean nativeStaticIsDTR(String paramString)
/* 1215:     */     throws UnsupportedCommOperationException;
/* 1216:     */   
/* 1217:     */   private static native boolean nativeStaticIsRTS(String paramString)
/* 1218:     */     throws UnsupportedCommOperationException;
/* 1219:     */   
/* 1220:     */   private static native boolean nativeStaticIsCTS(String paramString)
/* 1221:     */     throws UnsupportedCommOperationException;
/* 1222:     */   
/* 1223:     */   private static native boolean nativeStaticIsCD(String paramString)
/* 1224:     */     throws UnsupportedCommOperationException;
/* 1225:     */   
/* 1226:     */   private static native boolean nativeStaticIsRI(String paramString)
/* 1227:     */     throws UnsupportedCommOperationException;
/* 1228:     */   
/* 1229:     */   private static native int nativeStaticGetBaudRate(String paramString)
/* 1230:     */     throws UnsupportedCommOperationException;
/* 1231:     */   
/* 1232:     */   private static native int nativeStaticGetDataBits(String paramString)
/* 1233:     */     throws UnsupportedCommOperationException;
/* 1234:     */   
/* 1235:     */   private static native int nativeStaticGetParity(String paramString)
/* 1236:     */     throws UnsupportedCommOperationException;
/* 1237:     */   
/* 1238:     */   private static native int nativeStaticGetStopBits(String paramString)
/* 1239:     */     throws UnsupportedCommOperationException;
/* 1240:     */   
/* 1241:     */   private native byte nativeGetParityErrorChar()
/* 1242:     */     throws UnsupportedCommOperationException;
/* 1243:     */   
/* 1244:     */   private native boolean nativeSetParityErrorChar(byte paramByte)
/* 1245:     */     throws UnsupportedCommOperationException;
/* 1246:     */   
/* 1247:     */   private native byte nativeGetEndOfInputChar()
/* 1248:     */     throws UnsupportedCommOperationException;
/* 1249:     */   
/* 1250:     */   private native boolean nativeSetEndOfInputChar(byte paramByte)
/* 1251:     */     throws UnsupportedCommOperationException;
/* 1252:     */   
/* 1253:     */   private native boolean nativeSetUartType(String paramString, boolean paramBoolean)
/* 1254:     */     throws UnsupportedCommOperationException;
/* 1255:     */   
/* 1256:     */   native String nativeGetUartType()
/* 1257:     */     throws UnsupportedCommOperationException;
/* 1258:     */   
/* 1259:     */   private native boolean nativeSetBaudBase(int paramInt)
/* 1260:     */     throws UnsupportedCommOperationException;
/* 1261:     */   
/* 1262:     */   private native int nativeGetBaudBase()
/* 1263:     */     throws UnsupportedCommOperationException;
/* 1264:     */   
/* 1265:     */   private native boolean nativeSetDivisor(int paramInt)
/* 1266:     */     throws UnsupportedCommOperationException;
/* 1267:     */   
/* 1268:     */   private native int nativeGetDivisor()
/* 1269:     */     throws UnsupportedCommOperationException;
/* 1270:     */   
/* 1271:     */   private native boolean nativeSetLowLatency()
/* 1272:     */     throws UnsupportedCommOperationException;
/* 1273:     */   
/* 1274:     */   private native boolean nativeGetLowLatency()
/* 1275:     */     throws UnsupportedCommOperationException;
/* 1276:     */   
/* 1277:     */   private native boolean nativeSetCallOutHangup(boolean paramBoolean)
/* 1278:     */     throws UnsupportedCommOperationException;
/* 1279:     */   
/* 1280:     */   private native boolean nativeGetCallOutHangup()
/* 1281:     */     throws UnsupportedCommOperationException;
/* 1282:     */   
/* 1283:     */   private native boolean nativeClearCommInput()
/* 1284:     */     throws UnsupportedCommOperationException;
/* 1285:     */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.RXTXPort
 * JD-Core Version:    0.7.0.1
 */