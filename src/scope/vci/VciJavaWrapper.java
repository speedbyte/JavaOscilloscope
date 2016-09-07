/**
 * Main File
 */
package scope.vci;

import java.io.*;
import de.ixxat.vci3.*;
import de.ixxat.vci3.bal.*;
import de.ixxat.vci3.bal.can.*;
import de.ixxat.vci3.bal.lin.*;



/**
 * VCI V3 Java API Demo program
 * 
 * @author Michael Ummenhofer
 * 
 */
public class VciJavaWrapper
{

  /**
   * Main entry point. Takes command line arguments args
   */
  public void VciJavaWrapperInitialise()
  {
    // Create VCI Server Object
    VciServer         oVciServer      = null;
    IVciDeviceManager oDeviceManager  = null;
    IVciEnumDevice    oVciEnumDevice  = null;
    IVciDevice        oVciDevice      = null;
    IBalObject        oBalObject      = null;

    VciDeviceInfo     aoVciDeviceInfo[] = null;


    System.out.println("\nIXXAT VCI V3 - Java API - Demoprogram\n");
    
    // Initialize VCI Server Object
    try
    {
      // Create VCI Server Object
      oVciServer = new VciServer();

      // Print Version Number
      System.out.println(oVciServer.GetVersion());
    
      // Open VCI Device Manager
      oDeviceManager = oVciServer.GetDeviceManager();
      
      // Open VCI Device Enumerator
      oVciEnumDevice = oDeviceManager.EnumDevices();

      System.out.print("Wait for VCI Device Enum Event: ...");
      try
      {
        oVciEnumDevice.WaitFor(3000);
        System.out.println("... change detected!");
      }
      catch(Throwable oException)
      {
        System.out.println("... NO change detected!");
      }
      
      // Show Device list and count devices
      if(oVciEnumDevice != null)
      {
        boolean       fEndFlag          = false;
        boolean       fCounting         = true;
        int           dwVciDeviceCount  = 0;
        int           dwVciDeviceIndex  = 0;
        VciDeviceInfo oVciDeviceInfo    = null;
        
        do
        {
          try
          {
            // Try to get next device
            oVciDeviceInfo = oVciEnumDevice.Next();
          }
          catch(Throwable oException)
          {
            // Last device reached?
            oVciDeviceInfo = null;
          }

          // Device available
          if(oVciDeviceInfo != null)
          {
            // Do counting only?
            if(fCounting)
            {
              // Print Device Info
              dwVciDeviceCount++;
              System.out.println("\nVCI Device: " + dwVciDeviceCount + "\n" + oVciDeviceInfo);
            }
            else
            {
              if(dwVciDeviceIndex < aoVciDeviceInfo.length)
                aoVciDeviceInfo[dwVciDeviceIndex++] = oVciDeviceInfo;
              else
                throw new IndexOutOfBoundsException("VCI Device list has changed during scan -> ABORT");
            }
          }
          else
          {
            // Do counting only?
            if(fCounting)
            {
              //Switch of counting and build device list
              fCounting = false;
              
              // Reset Enum Device Index
              oVciEnumDevice.Reset();

              // Build device info list
              aoVciDeviceInfo = new VciDeviceInfo[dwVciDeviceCount];
            }
            else
            {
              fEndFlag = true;
              
              // Check if device list has changed
              if(dwVciDeviceIndex != aoVciDeviceInfo.length)
                throw new IndexOutOfBoundsException("VCI Device list has changed during scan -> ABORT");
            }
          }
        }while(!fEndFlag);
      }

      // If more than one device ask user
      long lVciId = 0;
      if(aoVciDeviceInfo.length != 1)
      {
        try
        {
          lVciId = oDeviceManager.SelectDeviceDialog();
        }
        catch( Throwable oException)
        {
          System.err.println("IVciDeviceManager.SelectDeviceDialog() failed with error: " + oException);
          System.out.println("Opening first found VCI 3 board instead\n");
          
          // In case of error try to open the first board
          lVciId = aoVciDeviceInfo[0].m_qwVciObjectId;
        }
      }
      else
        lVciId = aoVciDeviceInfo[0].m_qwVciObjectId;

      // Open VCI Device
      oVciDevice = oDeviceManager.OpenDevice(lVciId);

      // Get Device Info and Capabilities
      if(oVciDevice != null)
      {
        VciDeviceCapabilities oVciDeviceCaps = null;
        VciDeviceInfo         oVciDeviceInfo = null;
        
        oVciDeviceCaps = oVciDevice.GetDeviceCaps();
        System.out.println("VCI Device Capabilities: " + oVciDeviceCaps);
        
        oVciDeviceInfo = oVciDevice.GetDeviceInfo();
        System.out.println("VCI Device Info: " + oVciDeviceInfo);
      }
      
      // Open BAL Object
      oBalObject = oVciDevice.OpenBusAccessLayer();

      // Free VciEnumDevice, DeviceManager and VCI Server which are not longer needed
      oVciEnumDevice.Dispose();
      oVciEnumDevice = null;
      oDeviceManager.Dispose();
      oDeviceManager = null;
      oVciServer.Dispose();
      oVciServer     = null;
      
      // Perform Tests
      System.out.println("\n\n Now testing CAN Controller 1");
      TestCan(oBalObject, (short)0);
    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }
    
    // release all references
    System.out.println("Cleaning up Interface references to VCI Device");
    try
    {
      oBalObject.Dispose();
    } 
    catch (Throwable oException){} finally
    {
      oBalObject = null;
    }
    try
    {
      oVciDevice.Dispose();
    } 
    catch (Throwable oException){} 
    finally
    {
      oVciDevice = null;
    }

    System.out.println("Program finished!");
  }

  /**
   * Test CAN Controller
   * 
   * @param oBalObject
   * @param wSocketNumber
   */
  public static void TestCan(IBalObject oBalObject, short wSocketNumber)
  {
    ICanControl       oCanControl     = null;
    ICanSocket        oCanSocket      = null;
    ICanScheduler     oCanScheduler   = null;
    ICanChannel       oCanChannel     = null;
    ICanMessageReader oCanMsgReader   = null;
    ICanMessageWriter oCanMsgWriter   = null;
    
    // Open CAN Control and CAN Channel
    try
    {
      IBalResource  oBalResource  = null;
      BalFeatures   oBalFeatures  = null;
      
      System.out.println("Using Socket Number: " + wSocketNumber);
      
      oBalFeatures = oBalObject.GetFeatures();
      System.out.println("BAL Features: " + oBalFeatures);
      
      // Socket available?
      if(oBalFeatures.m_wBusSocketCount > wSocketNumber)
      {
        // Ensure CAN Controller Type
        if(oBalFeatures.m_awBusType[wSocketNumber] == VciDeviceCapabilities.VCI_BUS_CAN)
        {
          // Get CAN Control
          try
          {
            oBalResource  = oBalObject.OpenSocket(wSocketNumber, IBalResource.IID_ICanControl);
            oCanControl   = (ICanControl)oBalResource;
            oBalResource  = null;
          }
          catch(Throwable oException)
          {
            if (oException instanceof VciException)
            {
              VciException oVciException = (VciException) oException;
              System.err.println("Open Socket(IID_ICanControl), VciException: " + oVciException + " => " + oVciException.VciFormatError());
            } 
            else
              System.err.println("Open Socket(IID_ICanControl), exception: " + oException);
          }

          // Get CAN Socket
          try
          {
            oBalResource  = oBalObject.OpenSocket(wSocketNumber, IBalResource.IID_ICanSocket);
            oCanSocket    = (ICanSocket)oBalResource;
            oBalResource  = null;
          }
          catch(Throwable oException)
          {
            if (oException instanceof VciException)
            {
              VciException oVciException = (VciException) oException;
              System.err.println("Open Socket(IID_ICanSocket), VciException: " + oVciException + " => " + oVciException.VciFormatError());
            } 
            else
              System.err.println("Open Socket(IID_ICanSockets), exception: " + oException);
          }

          // Get CAN Scheduler
          try
          {
            oBalResource  = oBalObject.OpenSocket(wSocketNumber, IBalResource.IID_ICanScheduler);
            oCanScheduler = (ICanScheduler)oBalResource;
            oBalResource  = null;
          }
          catch(Throwable oException)
          {
            if (oException instanceof VciException)
            {
              VciException oVciException = (VciException) oException;
              System.err.println("Open Socket(IID_ICanScheduler), VciException: " + oVciException + " => " + oVciException.VciFormatError());
            } 
            else
              System.err.println("Open Socket(IID_ICanScheduler), exception: " + oException);
          }
        }
        else
          System.err.println("Socket No. " + wSocketNumber + " is not a \"VCI_BUS_CAN\"");
      }
      else
        System.err.println("Socket No. " + wSocketNumber + " is not a available!");
    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }

    // Test CAN
    try
    {
      // Create CAN Channel
      if(oCanSocket != null)
      {
        BalSocketInfo   oBalSocketInfo    = null;
        CanCapabilities oCanCapabilities  = null;
        CanLineStatus   oCanLineStatus    = null;
  
        oBalSocketInfo = oCanSocket.GetSocketInfo();
        System.out.println("BAL Socket Info: " + oBalSocketInfo);
  
        oCanCapabilities = oCanSocket.GetCapabilities();
        System.out.println("CAN Capabilities: " + oCanCapabilities);
  
        oCanLineStatus = oCanSocket.GetLineStatus();
        System.out.println("CAN Line Status: " + oCanLineStatus);
        
        System.out.println("Creating CAN Channel");
        oCanChannel = oCanSocket.CreateChannel(false);
      }
  
      // Configure, start Channel and Query Reader and Writer
      if(oCanChannel != null)
      {
        System.out.println("Initializing CAN Message Channel");
        oCanChannel.Initialize(Short.MAX_VALUE, Short.MAX_VALUE);
        oCanChannel.Activate();
        
        System.out.println("Query Message Reader");
        oCanMsgReader = oCanChannel.GetMessageReader();
        
        System.out.println("Query Message Writer");
        oCanMsgWriter = oCanChannel.GetMessageWriter();
      }
  
      // Configure and start CAN Controller
      if(oCanControl != null)
      {
        CanBitrate        oCanBitrate       = new CanBitrate(CanBitrate.Cia500KBit);
        CanChannelStatus  oChanStatus       = null;
        CanLineStatus     oLineStatus       = null;
        
        // Get Line Status
        oLineStatus = oCanControl.GetLineStatus();
        System.out.println("CAN Line Status: " + oLineStatus);
        
        // Try to detect BaudRate
        try
        {
          CanBitrate[] oaCanBitrateList  = null;
          CanBitrate   oBitrateDetected  = null;
          oaCanBitrateList  = new CanBitrate[]{ new CanBitrate(CanBitrate.Cia10KBit),
              new CanBitrate(CanBitrate.Cia20KBit),
              new CanBitrate(CanBitrate.Cia50KBit),
              new CanBitrate(CanBitrate.Cia125KBit),
              new CanBitrate(CanBitrate.Cia250KBit),
              new CanBitrate(CanBitrate.Cia500KBit),
              new CanBitrate(CanBitrate.Cia800KBit),
              new CanBitrate(CanBitrate.Cia1000KBit)};
  
          // Detect BaudRate, wait 100ms between two messages
          oBitrateDetected = oCanControl.DetectBaud(100, oaCanBitrateList);
          System.out.println("Detected Baudrate: " + oBitrateDetected);
          
        }
        catch(Throwable oException)
        {
          if (oException instanceof VciException)
          {
            VciException oVciException = (VciException) oException;
            System.err.println("DetectBaudrate failed with VciException: " + oVciException + " => " + oVciException.VciFormatError());
          } 
          else
            System.err.println("DetectBaudrate failed with Exception: " + oException);
        }
        
        System.out.println("Starting CAN Controller with 500 kBAUD");
        oCanControl.InitLine(ICanControl.CAN_OPMODE_STANDARD |
                             ICanControl.CAN_OPMODE_EXTENDED, 
                             oCanBitrate);
        
        // Filter closed completely
        oCanControl.SetAccFilter(ICanControl.CAN_FILTER_STD, 0xFFFF, 0xFFFF);
        // Filter opened completely
        oCanControl.SetAccFilter(ICanControl.CAN_FILTER_STD, 0, 0);
  
        // Add ID 1
        oCanControl.AddFilterIds(ICanControl.CAN_FILTER_STD, 1, 0xFFFF);
        // Remove ID 1
        oCanControl.RemFilterIds(ICanControl.CAN_FILTER_STD, 1, 0xFFFF);
        
        // Start
        oCanControl.StartLine();
  
        // Wait for controller
        Thread.sleep(250);
        
        // Get CAN Channel Status
        oChanStatus = oCanChannel.GetStatus();
        System.out.println("CAN Channel Status: " + oChanStatus);
        System.out.println("CAN Line Status: " + oChanStatus.m_oCanLineStatus);
        System.out.println("");
      }
      
      // CAN Scheduler available
      if(oCanScheduler != null)
      {
        TestCanScheduler(oCanScheduler);
      }
  
      // Read CAN Messages
      if(oCanMsgReader != null)
      {
        TestCanMessageReader(oCanMsgReader);
      }
  
      // Write CAN Messages
      if(oCanMsgWriter != null)
      {
        TestCanMessageWriter(oCanMsgWriter, oCanMsgReader);

        // Release CAN Message Writer
        oCanMsgWriter.Dispose();
        oCanMsgWriter = null;
      }
      
      // Dispose CAN Message Reader
      if(oCanMsgReader != null)
      {
        // Release CAN Message Reader
        oCanMsgReader.Dispose();
        oCanMsgReader = null;
      }
    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }
    
    if(oCanControl != null)
    {
      try
      { 
        // Get Line Status
        CanLineStatus oLineStatus = null;
        oLineStatus = oCanControl.GetLineStatus();
        System.out.println("CAN Line Status: " + oLineStatus);
      }
      catch(Throwable oException)
      {
        if (oException instanceof VciException)
        {
          VciException oVciException = (VciException) oException;
          System.err.println("Get CAN Line Status, VciException: " + oVciException + " => " + oVciException.VciFormatError());
        } 
        else
          System.err.println("Get CAN Line Status, Exception: " + oException);
      }
    }

    // Wait for user
    try
    { 
      BufferedReader  oBufferReader   = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Press <Enter> to exit");
      oBufferReader.readLine(); 
    } 
    catch(IOException oIOException) 
    {
      System.err.println("IOException: " + oIOException);
    }
  
    // Stop CAN Controller
    if(oCanControl != null)
    {
      try
      { 
        System.out.println("Stopping CAN Controller");
        oCanControl.StopLine();
        oCanControl.ResetLine();
      }
      catch(Throwable oException)
      {
        if (oException instanceof VciException)
        {
          VciException oVciException = (VciException) oException;
          System.err.println("Reset CAN Controller, VciException: " + oVciException + " => " + oVciException.VciFormatError());
        } 
        else
          System.err.println("Reset CAN Controller, Exception: " + oException);
      }
    }
    
    // release all references
    System.out.println("Cleaning up CAN Interface references");
    try
    {
      oCanControl.Dispose();
    } 
    catch (Throwable oException){} 
    finally
    {
      oCanControl = null;
    }
    try
    {
      oCanSocket.Dispose();
    } 
    catch (Throwable oException){} 
    finally
    {
      oCanSocket = null;
    }
    try
    {
      oCanScheduler.Dispose();
    } 
    catch (Throwable oException){} 
    finally
    {
      oCanScheduler = null;
    }
    try
    {
      oCanChannel.Dispose();
    } 
    catch (Throwable oException){}
    finally
    {
      oCanChannel = null;
    }
  }
  
  
  /**
   * Sub test {@link ICanScheduler}
   * 
   * @param oCanScheduler
   */
  public static void TestCanScheduler(ICanScheduler oCanScheduler)
  {
    try
    {
      CanSchedulerStatus oCanSchedulerStatus  = null;
      CanCyclicTxMessage oCanCyclicTxMessage  = new CanCyclicTxMessage();
      oCanCyclicTxMessage.m_wCycleTime        = 100;
      oCanCyclicTxMessage.m_bIncrMode         = CanCyclicTxMessage.CAN_CTXMSG_INC_16;
      oCanCyclicTxMessage.m_bByteIndex        = 0;
      oCanCyclicTxMessage.m_dwIdentifier      = 1;
      oCanCyclicTxMessage.m_bDataLength       = 8;
      oCanCyclicTxMessage.m_fSelfReception    = true;
      System.out.println("Cyclic CAN TX Message: " + oCanCyclicTxMessage);
      
      System.out.println("New created Scheduler...");
      oCanSchedulerStatus = oCanScheduler.GetStatus();
      System.out.println("CAN Scheduler Status: " + oCanSchedulerStatus);
  
      System.out.println("Started cyclic Message...");
      oCanScheduler.Resume();
      oCanScheduler.AddMessage(oCanCyclicTxMessage, 0);
      oCanScheduler.StartMessage(0, 1000);
      Thread.sleep(1000);
      oCanSchedulerStatus = oCanScheduler.GetStatus();
      System.out.println("CAN Scheduler Status: " + oCanSchedulerStatus);
  
      System.out.println("stopped cyclic Message...");
      oCanScheduler.StopMessage(0);
      oCanScheduler.RemMessage(0);
      oCanScheduler.Suspend();
      Thread.sleep(200);
      oCanSchedulerStatus = oCanScheduler.GetStatus();
      System.out.println("CAN Scheduler Status: " + oCanSchedulerStatus);
      
      System.out.println("Reseted Scheduler...");
      oCanScheduler.Reset();
      Thread.sleep(200);
      oCanSchedulerStatus = oCanScheduler.GetStatus();
      System.out.println("CAN Scheduler Status: " + oCanSchedulerStatus);
    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }
  }

  
  /**
   * Sub test {@link ICanMessageReader}
   * 
   * @param oCanMsgReader
   */
  public static void TestCanMessageReader(ICanMessageReader oCanMsgReader)
  {
    try
    {
      CanMessage  oCanMsg   = new CanMessage();
      long        qwMsgNo   = 0;
      boolean     fEnd      = false;
      boolean     fTimedOut = false;

      System.out.println("\nPress <Enter> to exit reception mode\n");
      do
      {
        try
        {
          // Read CAN Message
          // If ReadMessage fails, it throws an exception
          oCanMsg = oCanMsgReader.ReadMessage(oCanMsg);

          qwMsgNo++;
          if(fTimedOut)
          {
            System.out.print("\n");
            fTimedOut = false;
          }
          //System.out.println("No: " + qwMsgNo + " " + oCanMsg); // Scroll mode
          System.out.print("\rNo: " + qwMsgNo + " " + oCanMsg + "  "); // Overwrite mode
        }
        catch(Throwable oException)
        {
          //System.out.print("\r" + oException);

          //Wait for a new message in the queue
          try 
          {
            oCanMsgReader.WaitFor(500);
          }
          catch(Throwable oThrowable)
          {
            if(!fTimedOut)
            {
              System.out.print("\n");
              fTimedOut = true;
            }
            System.out.print(".");
          }
        }
        try
        {
          // User abort?
          if(System.in.available() > 0)
          {
            while(System.in.available() > 0)
              System.in.read();
            fEnd = true;
          }
        }
        catch(IOException ioErr)
        {
          System.err.println("An IO Error occured: " + ioErr);
          fEnd = true;
        }
      
      }while(!fEnd);

    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }
  }

  
  /**
   * Sub test {@link ICanMessageWriter}
   * 
   * @param oCanMsgWriter
   * @param oCanMsgReader 
   */
  public static void TestCanMessageWriter(ICanMessageWriter oCanMsgWriter,
                                          ICanMessageReader oCanMsgReader)
  {
    try
    {
      CanMessage  oTxCanMsg = new CanMessage();
      CanMessage  oRxCanMsg = new CanMessage();
      long        qwMsgNo   = 0;
      boolean     fEnd      = false;
      boolean     fTimedOut = false;

      // PreSet CAN Message
      oTxCanMsg.m_abData        = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte)0x88};
      oTxCanMsg.m_bDataLength   = 8;
      oTxCanMsg.m_dwIdentifier  = 0;
      oTxCanMsg.m_dwTimestamp   = 0; // No Delay
      oTxCanMsg.m_fExtendedFrameFormat        = false;
      oTxCanMsg.m_fRemoteTransmissionRequest  = false;
      oTxCanMsg.m_fSelfReception              = true;
      
      
      System.out.println("\nPress <Enter> to exit transmission mode\n");
      do
      {
        try
        {
          // Write CAN Message
          // If WriteMessage fails, it throws an exception
          oCanMsgWriter.WriteMessage(oTxCanMsg);

          qwMsgNo++;
          if(fTimedOut)
          {
            System.out.print("\n");
            fTimedOut = false;
          }
          //System.out.println("No: " + qwMsgNo + " " + oCanMsg); //Scroll Mode
          System.out.print("\rNo: " + qwMsgNo + " " + oTxCanMsg + "  "); //Overwrite Mode

          // Prepare Message ID for next Message
          oTxCanMsg.m_dwIdentifier++;
          if(oTxCanMsg.m_dwIdentifier > 0x7ff)
            oTxCanMsg.m_dwIdentifier = 0;
        }
        catch(Throwable oException)
        {
          //System.out.print("\r" + oException);
          //Wait for empty space in the FIFO
          try 
          {
            oCanMsgWriter.WaitFor(500);
          }
          catch(Throwable oThrowable)
          {
            if(!fTimedOut)
            {
              System.out.print("\n");
              fTimedOut = true;
            }
            System.out.print(".");
          }
        }
        
        // Read out all CAN Messages
        try
        {
          while(oCanMsgReader != null)
          {
            // Read CAN Message
            // If ReadMessage fails, it throws an exception
            oCanMsgReader.ReadMessage(oRxCanMsg);
          };
        }
        catch(Throwable oException)
        {
        }
        
        try
        {
          // User abort?
          if(System.in.available() > 0)
          {
            while(System.in.available() > 0)
              System.in.read();
            fEnd = true;
          }
        }
        catch(IOException ioErr)
        {
          
          System.err.println("An IO Error occured: " + ioErr);
          fEnd = true;
        }
      
      }while(!fEnd);
    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }
  }
}
