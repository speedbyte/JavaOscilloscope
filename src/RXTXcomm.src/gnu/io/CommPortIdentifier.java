/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.io.FileDescriptor;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.HashMap;
/*   7:    */ import java.util.Vector;
/*   8:    */ 
/*   9:    */ public class CommPortIdentifier
/*  10:    */ {
/*  11:    */   public static final int PORT_SERIAL = 1;
/*  12:    */   public static final int PORT_PARALLEL = 2;
/*  13:    */   public static final int PORT_I2C = 3;
/*  14:    */   public static final int PORT_RS485 = 4;
/*  15:    */   public static final int PORT_RAW = 5;
/*  16:    */   private String PortName;
/*  17: 79 */   private boolean Available = true;
/*  18:    */   private String Owner;
/*  19:    */   private CommPort commport;
/*  20:    */   private CommDriver RXTXDriver;
/*  21:    */   static CommPortIdentifier CommPortIndex;
/*  22:    */   CommPortIdentifier next;
/*  23:    */   private int PortType;
/*  24:    */   private static final boolean debug = false;
/*  25:104 */   static Object Sync = new Object();
/*  26:    */   Vector ownershipListener;
/*  27:    */   private boolean HideOwnerEvents;
/*  28:    */   
/*  29:    */   static
/*  30:    */   {
/*  31:    */     try
/*  32:    */     {
/*  33:107 */       CommDriver localCommDriver = (CommDriver)Class.forName("gnu.io.RXTXCommDriver").newInstance();
/*  34:108 */       localCommDriver.initialize();
/*  35:    */     }
/*  36:    */     catch (Throwable localThrowable)
/*  37:    */     {
/*  38:112 */       System.err.println(localThrowable + " thrown while loading " + "gnu.io.RXTXCommDriver");
/*  39:    */     }
/*  40:117 */     String str = System.getProperty("os.name");
/*  41:118 */     if (str.toLowerCase().indexOf("linux") == -1) {}
/*  42:123 */     System.loadLibrary("rxtxSerial");
/*  43:    */   }
/*  44:    */   
/*  45:    */   CommPortIdentifier(String paramString, CommPort paramCommPort, int paramInt, CommDriver paramCommDriver)
/*  46:    */   {
/*  47:127 */     this.PortName = paramString;
/*  48:128 */     this.commport = paramCommPort;
/*  49:129 */     this.PortType = paramInt;
/*  50:130 */     this.next = null;
/*  51:131 */     this.RXTXDriver = paramCommDriver;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static void addPortName(String paramString, int paramInt, CommDriver paramCommDriver)
/*  55:    */   {
/*  56:148 */     AddIdentifierToList(new CommPortIdentifier(paramString, null, paramInt, paramCommDriver));
/*  57:    */   }
/*  58:    */   
/*  59:    */   private static void AddIdentifierToList(CommPortIdentifier paramCommPortIdentifier)
/*  60:    */   {
/*  61:161 */     synchronized (Sync)
/*  62:    */     {
/*  63:163 */       if (CommPortIndex == null)
/*  64:    */       {
/*  65:165 */         CommPortIndex = paramCommPortIdentifier;
/*  66:    */       }
/*  67:    */       else
/*  68:    */       {
/*  69:170 */         CommPortIdentifier localCommPortIdentifier = CommPortIndex;
/*  70:171 */         while (localCommPortIdentifier.next != null) {
/*  71:173 */           localCommPortIdentifier = localCommPortIdentifier.next;
/*  72:    */         }
/*  73:176 */         localCommPortIdentifier.next = paramCommPortIdentifier;
/*  74:    */       }
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void addPortOwnershipListener(CommPortOwnershipListener paramCommPortOwnershipListener)
/*  79:    */   {
/*  80:194 */     if (this.ownershipListener == null) {
/*  81:196 */       this.ownershipListener = new Vector();
/*  82:    */     }
/*  83:201 */     if (!this.ownershipListener.contains(paramCommPortOwnershipListener)) {
/*  84:203 */       this.ownershipListener.addElement(paramCommPortOwnershipListener);
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public String getCurrentOwner()
/*  89:    */   {
/*  90:217 */     return this.Owner;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String getName()
/*  94:    */   {
/*  95:230 */     return this.PortName;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static CommPortIdentifier getPortIdentifier(String paramString)
/*  99:    */     throws NoSuchPortException
/* 100:    */   {
/* 101:    */     CommPortIdentifier localCommPortIdentifier;
/* 102:245 */     synchronized (Sync)
/* 103:    */     {
/* 104:247 */       localCommPortIdentifier = CommPortIndex;
/* 105:248 */       while ((localCommPortIdentifier != null) && (!localCommPortIdentifier.PortName.equals(paramString))) {
/* 106:249 */         localCommPortIdentifier = localCommPortIdentifier.next;
/* 107:    */       }
/* 108:251 */       if (localCommPortIdentifier == null)
/* 109:    */       {
/* 110:257 */         getPortIdentifiers();
/* 111:258 */         localCommPortIdentifier = CommPortIndex;
/* 112:259 */         while ((localCommPortIdentifier != null) && (!localCommPortIdentifier.PortName.equals(paramString))) {
/* 113:260 */           localCommPortIdentifier = localCommPortIdentifier.next;
/* 114:    */         }
/* 115:    */       }
/* 116:    */     }
/* 117:264 */     if (localCommPortIdentifier != null) {
/* 118:264 */       return localCommPortIdentifier;
/* 119:    */     }
/* 120:269 */     throw new NoSuchPortException();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static CommPortIdentifier getPortIdentifier(CommPort paramCommPort)
/* 124:    */     throws NoSuchPortException
/* 125:    */   {
/* 126:    */     CommPortIdentifier localCommPortIdentifier;
/* 127:285 */     synchronized (Sync)
/* 128:    */     {
/* 129:287 */       localCommPortIdentifier = CommPortIndex;
/* 130:288 */       while ((localCommPortIdentifier != null) && (localCommPortIdentifier.commport != paramCommPort)) {
/* 131:289 */         localCommPortIdentifier = localCommPortIdentifier.next;
/* 132:    */       }
/* 133:    */     }
/* 134:291 */     if (localCommPortIdentifier != null) {
/* 135:292 */       return localCommPortIdentifier;
/* 136:    */     }
/* 137:296 */     throw new NoSuchPortException();
/* 138:    */   }
/* 139:    */   
/* 140:    */   public static Enumeration getPortIdentifiers()
/* 141:    */   {
/* 142:311 */     synchronized (Sync)
/* 143:    */     {
/* 144:313 */       HashMap localHashMap = new HashMap();
/* 145:314 */       CommPortIdentifier localCommPortIdentifier1 = CommPortIndex;
/* 146:315 */       while (localCommPortIdentifier1 != null)
/* 147:    */       {
/* 148:316 */         localHashMap.put(localCommPortIdentifier1.PortName, localCommPortIdentifier1);
/* 149:317 */         localCommPortIdentifier1 = localCommPortIdentifier1.next;
/* 150:    */       }
/* 151:319 */       CommPortIndex = null;
/* 152:    */       try
/* 153:    */       {
/* 154:326 */         CommDriver localCommDriver = (CommDriver)Class.forName("gnu.io.RXTXCommDriver").newInstance();
/* 155:327 */         localCommDriver.initialize();
/* 156:    */         
/* 157:    */ 
/* 158:    */ 
/* 159:331 */         CommPortIdentifier localCommPortIdentifier2 = CommPortIndex;
/* 160:332 */         Object localObject1 = null;
/* 161:333 */         while (localCommPortIdentifier2 != null)
/* 162:    */         {
/* 163:334 */           CommPortIdentifier localCommPortIdentifier3 = (CommPortIdentifier)localHashMap.get(localCommPortIdentifier2.PortName);
/* 164:335 */           if ((localCommPortIdentifier3 != null) && (localCommPortIdentifier3.PortType == localCommPortIdentifier2.PortType))
/* 165:    */           {
/* 166:337 */             localCommPortIdentifier3.RXTXDriver = localCommPortIdentifier2.RXTXDriver;
/* 167:338 */             localCommPortIdentifier3.next = localCommPortIdentifier2.next;
/* 168:339 */             if (localObject1 == null) {
/* 169:340 */               CommPortIndex = localCommPortIdentifier3;
/* 170:    */             } else {
/* 171:342 */               ((CommPortIdentifier)localObject1).next = localCommPortIdentifier3;
/* 172:    */             }
/* 173:344 */             localObject1 = localCommPortIdentifier3;
/* 174:    */           }
/* 175:    */           else
/* 176:    */           {
/* 177:346 */             localObject1 = localCommPortIdentifier2;
/* 178:    */           }
/* 179:348 */           localCommPortIdentifier2 = localCommPortIdentifier2.next;
/* 180:    */         }
/* 181:    */       }
/* 182:    */       catch (Throwable localThrowable)
/* 183:    */       {
/* 184:353 */         System.err.println(localThrowable + " thrown while loading " + "gnu.io.RXTXCommDriver");
/* 185:354 */         System.err.flush();
/* 186:    */       }
/* 187:    */     }
/* 188:357 */     return new CommPortEnumerator();
/* 189:    */   }
/* 190:    */   
/* 191:    */   public int getPortType()
/* 192:    */   {
/* 193:370 */     return this.PortType;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public synchronized boolean isCurrentlyOwned()
/* 197:    */   {
/* 198:383 */     return !this.Available;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public synchronized CommPort open(FileDescriptor paramFileDescriptor)
/* 202:    */     throws UnsupportedCommOperationException
/* 203:    */   {
/* 204:396 */     throw new UnsupportedCommOperationException();
/* 205:    */   }
/* 206:    */   
/* 207:    */   public CommPort open(String paramString, int paramInt)
/* 208:    */     throws PortInUseException
/* 209:    */   {
/* 210:    */     boolean bool;
/* 211:416 */     synchronized (this)
/* 212:    */     {
/* 213:417 */       bool = this.Available;
/* 214:418 */       if (bool)
/* 215:    */       {
/* 216:420 */         this.Available = false;
/* 217:421 */         this.Owner = paramString;
/* 218:    */       }
/* 219:    */     }
/* 220:424 */     if (!bool)
/* 221:    */     {
/* 222:426 */       long l1 = System.currentTimeMillis() + paramInt;
/* 223:    */       
/* 224:428 */       fireOwnershipEvent(3);
/* 225:430 */       synchronized (this)
/* 226:    */       {
/* 227:    */         for (;;)
/* 228:    */         {
/* 229:    */           long l2;
/* 230:431 */           if ((!this.Available) && ((l2 = System.currentTimeMillis()) < l1)) {
/* 231:    */             try
/* 232:    */             {
/* 233:434 */               wait(l1 - l2);
/* 234:    */             }
/* 235:    */             catch (InterruptedException localInterruptedException)
/* 236:    */             {
/* 237:438 */               Thread.currentThread().interrupt();
/* 238:    */             }
/* 239:    */           }
/* 240:    */         }
/* 241:442 */         bool = this.Available;
/* 242:443 */         if (bool)
/* 243:    */         {
/* 244:445 */           this.Available = false;
/* 245:446 */           this.Owner = paramString;
/* 246:    */         }
/* 247:    */       }
/* 248:    */     }
/* 249:450 */     if (!bool) {
/* 250:452 */       throw new PortInUseException(getCurrentOwner());
/* 251:    */     }
/* 252:    */     try
/* 253:    */     {
/* 254:456 */       if (this.commport == null) {
/* 255:458 */         this.commport = this.RXTXDriver.getCommPort(this.PortName, this.PortType);
/* 256:    */       }
/* 257:460 */       if (this.commport != null)
/* 258:    */       {
/* 259:462 */         fireOwnershipEvent(1);
/* 260:463 */         return this.commport;
/* 261:    */       }
/* 262:467 */       throw new PortInUseException(native_psmisc_report_owner(this.PortName));
/* 263:    */     }
/* 264:    */     finally
/* 265:    */     {
/* 266:471 */       if (this.commport == null) {
/* 267:473 */         synchronized (this)
/* 268:    */         {
/* 269:474 */           this.Available = true;
/* 270:475 */           this.Owner = null;
/* 271:    */         }
/* 272:    */       }
/* 273:    */     }
/* 274:    */   }
/* 275:    */   
/* 276:    */   public void removePortOwnershipListener(CommPortOwnershipListener paramCommPortOwnershipListener)
/* 277:    */   {
/* 278:492 */     if (this.ownershipListener != null) {
/* 279:493 */       this.ownershipListener.removeElement(paramCommPortOwnershipListener);
/* 280:    */     }
/* 281:    */   }
/* 282:    */   
/* 283:    */   void internalClosePort()
/* 284:    */   {
/* 285:506 */     synchronized (this)
/* 286:    */     {
/* 287:508 */       this.Owner = null;
/* 288:509 */       this.Available = true;
/* 289:510 */       this.commport = null;
/* 290:    */       
/* 291:512 */       notifyAll();
/* 292:    */     }
/* 293:514 */     fireOwnershipEvent(2);
/* 294:    */   }
/* 295:    */   
/* 296:    */   void fireOwnershipEvent(int paramInt)
/* 297:    */   {
/* 298:527 */     if (this.ownershipListener != null)
/* 299:    */     {
/* 300:530 */       Enumeration localEnumeration = this.ownershipListener.elements();
/* 301:    */       CommPortOwnershipListener localCommPortOwnershipListener;
/* 302:531 */       for (; localEnumeration.hasMoreElements(); localCommPortOwnershipListener.ownershipChange(paramInt)) {
/* 303:533 */         localCommPortOwnershipListener = (CommPortOwnershipListener)localEnumeration.nextElement();
/* 304:    */       }
/* 305:    */     }
/* 306:    */   }
/* 307:    */   
/* 308:    */   private native String native_psmisc_report_owner(String paramString);
/* 309:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommPortIdentifier
 * JD-Core Version:    0.7.0.1
 */