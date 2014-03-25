/*   1:    */ package gnu.io;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Button;
/*   5:    */ import java.awt.Checkbox;
/*   6:    */ import java.awt.Frame;
/*   7:    */ import java.awt.GridLayout;
/*   8:    */ import java.awt.Label;
/*   9:    */ import java.awt.Panel;
/*  10:    */ import java.awt.TextArea;
/*  11:    */ import java.awt.TextField;
/*  12:    */ import java.awt.event.ActionEvent;
/*  13:    */ import java.awt.event.ActionListener;
/*  14:    */ import java.awt.event.WindowAdapter;
/*  15:    */ import java.awt.event.WindowEvent;
/*  16:    */ import java.io.FileOutputStream;
/*  17:    */ import java.io.IOException;
/*  18:    */ import java.io.PrintStream;
/*  19:    */ 
/*  20:    */ class Configure
/*  21:    */   extends Frame
/*  22:    */ {
/*  23:    */   Checkbox[] cb;
/*  24:    */   Panel p1;
/*  25:    */   static final int PORT_SERIAL = 1;
/*  26:    */   static final int PORT_PARALLEL = 2;
/*  27: 70 */   int PortType = 1;
/*  28:    */   
/*  29:    */   private void saveSpecifiedPorts()
/*  30:    */   {
/*  31: 75 */     String str2 = System.getProperty("java.home");
/*  32: 76 */     String str3 = System.getProperty("path.separator", ":");
/*  33: 77 */     String str4 = System.getProperty("file.separator", "/");
/*  34: 78 */     String str5 = System.getProperty("line.separator");
/*  35:    */     String str1;
/*  36: 81 */     if (this.PortType == 1)
/*  37:    */     {
/*  38: 82 */       str1 = str2 + str4 + "lib" + str4 + "gnu.io.rxtx.SerialPorts";
/*  39:    */     }
/*  40: 85 */     else if (this.PortType == 2)
/*  41:    */     {
/*  42: 86 */       str1 = str2 + "gnu.io.rxtx.ParallelPorts";
/*  43:    */     }
/*  44:    */     else
/*  45:    */     {
/*  46: 90 */       System.out.println("Bad Port Type!");
/*  47: 91 */       return;
/*  48:    */     }
/*  49: 93 */     System.out.println(str1);
/*  50:    */     try
/*  51:    */     {
/*  52: 96 */       FileOutputStream localFileOutputStream = new FileOutputStream(str1);
/*  53: 98 */       for (int i = 0; i < 128; i++) {
/*  54:100 */         if (this.cb[i].getState())
/*  55:    */         {
/*  56:102 */           String str6 = this.cb[i].getLabel() + str3;
/*  57:    */           
/*  58:104 */           localFileOutputStream.write(str6.getBytes());
/*  59:    */         }
/*  60:    */       }
/*  61:107 */       localFileOutputStream.write(str5.getBytes());
/*  62:108 */       localFileOutputStream.close();
/*  63:    */     }
/*  64:    */     catch (IOException localIOException)
/*  65:    */     {
/*  66:112 */       System.out.println("IOException!");
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   void addCheckBoxes(String paramString)
/*  71:    */   {
/*  72:118 */     for (int i = 0; i < 128; i++) {
/*  73:119 */       if (this.cb[i] != null) {
/*  74:120 */         this.p1.remove(this.cb[i]);
/*  75:    */       }
/*  76:    */     }
/*  77:121 */     for (i = 1; i < 129; i++)
/*  78:    */     {
/*  79:123 */       this.cb[(i - 1)] = new Checkbox(paramString + i);
/*  80:124 */       this.p1.add("NORTH", this.cb[(i - 1)]);
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public Configure()
/*  85:    */   {
/*  86:130 */     int i = 640;
/*  87:131 */     int j = 480;
/*  88:132 */     this.cb = new Checkbox[''];
/*  89:133 */     Frame localFrame = new Frame("Configure gnu.io.rxtx.properties");
/*  90:    */     
/*  91:135 */     String str1 = System.getProperty("file.separator", "/");
/*  92:    */     String str2;
/*  93:137 */     if (str1.compareTo("/") != 0) {
/*  94:138 */       str2 = "COM";
/*  95:    */     } else {
/*  96:140 */       str2 = "/dev/";
/*  97:    */     }
/*  98:142 */     localFrame.setBounds(100, 50, i, j);
/*  99:143 */     localFrame.setLayout(new BorderLayout());
/* 100:144 */     this.p1 = new Panel();
/* 101:145 */     this.p1.setLayout(new GridLayout(16, 4));
/* 102:146 */     ActionListener local1 = new ActionListener()
/* 103:    */     {
/* 104:    */       public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/* 105:    */       {
/* 106:149 */         String str = paramAnonymousActionEvent.getActionCommand();
/* 107:150 */         if (str.equals("Save")) {
/* 108:151 */           Configure.this.saveSpecifiedPorts();
/* 109:    */         }
/* 110:    */       }
/* 111:155 */     };
/* 112:156 */     addCheckBoxes(str2);
/* 113:157 */     TextArea localTextArea = new TextArea(this.EnumMessage, 5, 50, 3);
/* 114:    */     
/* 115:159 */     localTextArea.setSize(50, i);
/* 116:160 */     localTextArea.setEditable(false);
/* 117:    */     
/* 118:162 */     Panel localPanel = new Panel();
/* 119:163 */     localPanel.add(new Label("Port Name:"));
/* 120:164 */     TextField localTextField = new TextField(str2, 8);
/* 121:165 */     localTextField.addActionListener(new ActionListener()
/* 122:    */     {
/* 123:    */       private final Frame val$f;
/* 124:    */       
/* 125:    */       public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/* 126:    */       {
/* 127:168 */         Configure.this.addCheckBoxes(paramAnonymousActionEvent.getActionCommand());
/* 128:169 */         this.val$f.setVisible(true);
/* 129:    */       }
/* 130:171 */     });
/* 131:172 */     localPanel.add(localTextField);
/* 132:173 */     Checkbox localCheckbox = new Checkbox("Keep Ports");
/* 133:174 */     localPanel.add(localCheckbox);
/* 134:175 */     Button[] arrayOfButton = new Button[6];
/* 135:176 */     int k = 0;
/* 136:176 */     for (int m = 4; m < 129; k++)
/* 137:    */     {
/* 138:178 */       arrayOfButton[k] = new Button("1-" + m);
/* 139:179 */       arrayOfButton[k].addActionListener(new ActionListener()
/* 140:    */       {
/* 141:    */         private final Frame val$f;
/* 142:    */         
/* 143:    */         public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/* 144:    */         {
/* 145:182 */           int i = Integer.parseInt(paramAnonymousActionEvent.getActionCommand().substring(2));
/* 146:184 */           for (int j = 0; j < i; j++)
/* 147:    */           {
/* 148:186 */             Configure.this.cb[j].setState(!Configure.this.cb[j].getState());
/* 149:    */             
/* 150:188 */             this.val$f.setVisible(true);
/* 151:    */           }
/* 152:    */         }
/* 153:191 */       });
/* 154:192 */       localPanel.add(arrayOfButton[k]);m *= 2;
/* 155:    */     }
/* 156:194 */     Button localButton1 = new Button("More");
/* 157:195 */     Button localButton2 = new Button("Save");
/* 158:196 */     localButton1.addActionListener(local1);
/* 159:197 */     localButton2.addActionListener(local1);
/* 160:198 */     localPanel.add(localButton1);
/* 161:199 */     localPanel.add(localButton2);
/* 162:200 */     localFrame.add("South", localPanel);
/* 163:201 */     localFrame.add("Center", this.p1);
/* 164:202 */     localFrame.add("North", localTextArea);
/* 165:203 */     localFrame.addWindowListener(new WindowAdapter()
/* 166:    */     {
/* 167:    */       public void windowClosing(WindowEvent paramAnonymousWindowEvent)
/* 168:    */       {
/* 169:207 */         System.exit(0);
/* 170:    */       }
/* 171:210 */     });
/* 172:211 */     localFrame.setVisible(true);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public static void main(String[] paramArrayOfString)
/* 176:    */   {
/* 177:215 */     new Configure();
/* 178:    */   }
/* 179:    */   
/* 180:217 */   String EnumMessage = "gnu.io.rxtx.properties has not been detected.\n\nThere is no consistant means of detecting ports on this operating System.  It is necessary to indicate which ports are valid on this system before proper port enumeration can happen.  Please check the ports that are valid on this system and select Save";
/* 181:    */ }


/* Location:           F:\workspace_java\comemso_reichweite_analyse\impl\Application\CAN-Oscilloscope\CAN-Oscilloscope 2.0\lib\RXTXcomm.jar
 * Qualified Name:     gnu.io.Configure
 * JD-Core Version:    0.7.0.1
 */