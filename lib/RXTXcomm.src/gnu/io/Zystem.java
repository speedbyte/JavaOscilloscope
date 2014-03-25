/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.RandomAccessFile;
/*   5:    */ import java.util.logging.Logger;
/*   6:    */ 
/*   7:    */ public class Zystem
/*   8:    */ {
/*   9:    */   public static final int SILENT_MODE = 0;
/*  10:    */   public static final int FILE_MODE = 1;
/*  11:    */   public static final int NET_MODE = 2;
/*  12:    */   public static final int MEX_MODE = 3;
/*  13:    */   public static final int PRINT_MODE = 4;
/*  14:    */   public static final int J2EE_MSG_MODE = 5;
/*  15:    */   public static final int J2SE_LOG_MODE = 6;
/*  16: 85 */   static int mode = 0;
/*  17:    */   private static String target;
/*  18:    */   
/*  19:    */   public Zystem(int paramInt)
/*  20:    */     throws UnSupportedLoggerException
/*  21:    */   {
/*  22: 92 */     mode = paramInt;
/*  23: 93 */     startLogger("asdf");
/*  24:    */   }
/*  25:    */   
/*  26:    */   public Zystem()
/*  27:    */     throws UnSupportedLoggerException
/*  28:    */   {
/*  29:109 */     String str = System.getProperty("gnu.io.log.mode");
/*  30:110 */     if (str != null)
/*  31:    */     {
/*  32:112 */       if ("SILENT_MODE".equals(str)) {
/*  33:114 */         mode = 0;
/*  34:116 */       } else if ("FILE_MODE".equals(str)) {
/*  35:118 */         mode = 1;
/*  36:120 */       } else if ("NET_MODE".equals(str)) {
/*  37:122 */         mode = 2;
/*  38:124 */       } else if ("MEX_MODE".equals(str)) {
/*  39:126 */         mode = 3;
/*  40:128 */       } else if ("PRINT_MODE".equals(str)) {
/*  41:130 */         mode = 4;
/*  42:132 */       } else if ("J2EE_MSG_MODE".equals(str)) {
/*  43:134 */         mode = 5;
/*  44:136 */       } else if ("J2SE_LOG_MODE".equals(str)) {
/*  45:138 */         mode = 6;
/*  46:    */       } else {
/*  47:    */         try
/*  48:    */         {
/*  49:144 */           mode = Integer.parseInt(str);
/*  50:    */         }
/*  51:    */         catch (NumberFormatException localNumberFormatException)
/*  52:    */         {
/*  53:148 */           mode = 0;
/*  54:    */         }
/*  55:    */       }
/*  56:    */     }
/*  57:    */     else {
/*  58:154 */       mode = 0;
/*  59:    */     }
/*  60:156 */     startLogger("asdf");
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void startLogger()
/*  64:    */     throws UnSupportedLoggerException
/*  65:    */   {
/*  66:162 */     if ((mode == 0) || (mode == 4)) {
/*  67:165 */       return;
/*  68:    */     }
/*  69:167 */     throw new UnSupportedLoggerException("Target Not Allowed");
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void startLogger(String paramString)
/*  73:    */     throws UnSupportedLoggerException
/*  74:    */   {
/*  75:174 */     target = paramString;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void finalize()
/*  79:    */   {
/*  80:198 */     mode = 0;
/*  81:199 */     target = null;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void filewrite(String paramString)
/*  85:    */   {
/*  86:    */     try
/*  87:    */     {
/*  88:205 */       RandomAccessFile localRandomAccessFile = new RandomAccessFile(target, "rw");
/*  89:    */       
/*  90:207 */       localRandomAccessFile.seek(localRandomAccessFile.length());
/*  91:208 */       localRandomAccessFile.writeBytes(paramString);
/*  92:209 */       localRandomAccessFile.close();
/*  93:    */     }
/*  94:    */     catch (Exception localException)
/*  95:    */     {
/*  96:211 */       System.out.println("Debug output file write failed");
/*  97:    */     }
/*  98:    */   }
/*  99:    */   
/* 100:    */   public boolean report(String paramString)
/* 101:    */   {
/* 102:217 */     if (mode != 2)
/* 103:    */     {
/* 104:221 */       if (mode == 4)
/* 105:    */       {
/* 106:223 */         System.out.println(paramString);
/* 107:224 */         return true;
/* 108:    */       }
/* 109:226 */       if (mode != 3)
/* 110:    */       {
/* 111:230 */         if (mode == 0) {
/* 112:232 */           return true;
/* 113:    */         }
/* 114:234 */         if (mode == 1)
/* 115:    */         {
/* 116:236 */           filewrite(paramString);
/* 117:    */         }
/* 118:    */         else
/* 119:    */         {
/* 120:238 */           if (mode == 5) {
/* 121:240 */             return false;
/* 122:    */           }
/* 123:242 */           if (mode == 6)
/* 124:    */           {
/* 125:244 */             Logger.getLogger("gnu.io").fine(paramString);
/* 126:245 */             return true;
/* 127:    */           }
/* 128:    */         }
/* 129:    */       }
/* 130:    */     }
/* 131:247 */     return false;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public boolean reportln()
/* 135:    */   {
/* 136:253 */     if (mode != 2)
/* 137:    */     {
/* 138:258 */       if (mode == 4)
/* 139:    */       {
/* 140:260 */         System.out.println();
/* 141:261 */         return true;
/* 142:    */       }
/* 143:263 */       if (mode != 3)
/* 144:    */       {
/* 145:268 */         if (mode == 0) {
/* 146:270 */           return true;
/* 147:    */         }
/* 148:272 */         if (mode == 1) {
/* 149:274 */           filewrite("\n");
/* 150:276 */         } else if (mode == 5) {
/* 151:278 */           return false;
/* 152:    */         }
/* 153:    */       }
/* 154:    */     }
/* 155:280 */     return false;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public boolean reportln(String paramString)
/* 159:    */   {
/* 160:286 */     if (mode != 2)
/* 161:    */     {
/* 162:291 */       if (mode == 4)
/* 163:    */       {
/* 164:293 */         System.out.println(paramString);
/* 165:294 */         return true;
/* 166:    */       }
/* 167:296 */       if (mode != 3)
/* 168:    */       {
/* 169:301 */         if (mode == 0) {
/* 170:303 */           return true;
/* 171:    */         }
/* 172:305 */         if (mode == 1)
/* 173:    */         {
/* 174:307 */           filewrite(paramString + "\n");
/* 175:    */         }
/* 176:    */         else
/* 177:    */         {
/* 178:309 */           if (mode == 5) {
/* 179:311 */             return false;
/* 180:    */           }
/* 181:313 */           if (mode == 6) {
/* 182:315 */             return true;
/* 183:    */           }
/* 184:    */         }
/* 185:    */       }
/* 186:    */     }
/* 187:317 */     return false;
/* 188:    */   }
/* 189:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.Zystem
 * JD-Core Version:    0.7.0.1
 */