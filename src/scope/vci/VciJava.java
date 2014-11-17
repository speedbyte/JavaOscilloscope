/**
 * Main File
 */
package scope.vci;
import java.io.*;

import de.ixxat.vci3.*;
import de.ixxat.vci3.bal.*;
import de.ixxat.vci3.bal.can.*;


public class VciJava
{
	static long 			messageNo 			= 0;
	static CanMessage 		oCanMsg   			= new CanMessage();
	static VciDeviceInfo[] 	aoVciDeviceInfo 	= null;
    static int 				dwVciDeviceIndex  	= 0;
    static boolean 			fTimedOut 			= false;
    public int 		errCnt 				= 0;
	ICanControl       oCanControl     = null;
    ICanSocket        oCanSocket      = null;
    ICanChannel       oCanChannel     = null;
	
	
    public VciJava()
    {
        System.out.println("\nCAN Blue II Constructur\n");
    }
  
    public IBalObject InitCanBlue()
    {
    // Create VCI Server Object
	VciServer		  oVciServer      = null;
    IVciDeviceManager oDeviceManager  = null;
    IVciEnumDevice    oVciEnumDevice  = null;
    IVciDevice        oVciDevice      = null;
    IBalObject        oBalObject      = null;
    
    aoVciDeviceInfo = new VciDeviceInfo[1];
    //new Thread(new CanReadThread()).start();

    System.out.println("\nCAN Blue II Initialise\n");
    
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
      System.out.printf("Wait for VCI Device Enum Event: ...%s", oVciEnumDevice);
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
        VciDeviceInfo oVciDeviceInfo    = null;
        oVciDeviceInfo = oVciEnumDevice.Next();
        // Device available
		if(oVciDeviceInfo != null)
		{
			if(dwVciDeviceIndex < aoVciDeviceInfo.length)
			{
				aoVciDeviceInfo[dwVciDeviceIndex++] = oVciDeviceInfo;
			}
		}
	  }
      // If more than one device ask user
      long lVciId = 0;
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
      System.out.println("\nStarting CAN Interface");
      //Can(oBalObject, (short)0);
    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("1VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
      {
    	  System.err.println("Exception: " + oException);
      }
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
    return oBalObject;

    }

  /**
   * Test CAN Controller
   * 
   * @param oBalObject
   * @param wSocketNumber
   */
  public ICanMessageReader StartCan(IBalObject oBalObject, short wSocketNumber)
  {
    
    ICanMessageReader oCanMsgReader   = null;
    
    // Open CAN Control and CAN Channel
    try
    {
      IBalResource  oBalResource  = null; // temporary variable 
      BalFeatures   oBalFeatures  = null; // temporary variable
      
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
        System.err.println("2VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }
    
    // Now we have CanSocket, CanScheduler and CanControl
    // Start CAN
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
          CanBitrate   oBitrateDetected  = new CanBitrate(CanBitrate.Cia500KBit);
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
    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("3VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }
    return oCanMsgReader;
  }
  
  
  public void StopCan(IBalObject oBalObject, ICanMessageReader oCanMsgReader)
  {
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
	    System.out.println("Program finished!");
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
	      oCanChannel.Dispose();
	    } 
	    catch (Throwable oException){}
	    finally
	    {
	      oCanChannel = null;
	    }
	    try
	    {
	      oCanMsgReader.Dispose();
	    } 
	    catch (Throwable oException){}
	    finally
	    {
	      oCanMsgReader = null;
	    }

  
  }

  
  /**
   * Sub test {@link ICanMessageReader}
   * 
   * @param oCanMsgReader
   */
  public  CanMessage CanMessageReader(ICanMessageReader oCanMsgReader)
  {
    try
    {
        try
        {
          // Read CAN Message
          // If ReadMessage fails, it throws an exception
			oCanMsg = oCanMsgReader.ReadMessage(oCanMsg);
			errCnt = 0;
			if(fTimedOut)
			{
			  fTimedOut = false;
			}
        }
        catch(Throwable oException)
        {
          //Wait for a new message in the queue
          oCanMsg = null;
          try 
          {
            oCanMsgReader.WaitFor(1);
          }
          catch(Throwable oThrowable)
          {
            if(!fTimedOut)
            {
              fTimedOut = true;
            }
			errCnt++;
          }
        }
        try
        {
          // User abort?
          if(System.in.available() > 0)
          {
            while(System.in.available() > 0)
              System.in.read();
          }
        }
        catch(IOException ioErr)
        {
          System.err.println("An IO Error occured: " + ioErr);
        }
    }
    catch(Throwable oException)
    {
      if (oException instanceof VciException)
      {
        VciException oVciException = (VciException) oException;
        System.err.println("5VciException: " + oVciException + " => " + oVciException.VciFormatError());
      } 
      else
        System.err.println("Exception: " + oException);
    }
    return oCanMsg;
  }
  
  public void ResetDeviceIndex()
  {
	  dwVciDeviceIndex  = 0;
  }
}
