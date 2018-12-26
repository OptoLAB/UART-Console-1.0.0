/*
 * Author: J. Bajic, 2018.
 */
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class SerialPortModel {

	
/*---------------------------------------------
  	variables
---------------------------------------------*/		
	
	
	private ArrayList<Integer> bauds=new ArrayList<Integer>(Arrays.asList(110,300,600,1200,2400,4800,9600,14400,19200,28800,38400,56000,57600,115200,128000,256000));
	private int baud=bauds.get(6);
	private ArrayList<String> parities=new ArrayList<String>(Arrays.asList("NONE", "ODD", "EVEN", "MARK", "SPACE"));
	private String parity=parities.get(0);
	
	private ArrayList<Integer> dataBitss=new ArrayList<Integer>(Arrays.asList(8, 7, 6, 5));
	private int dataBits=dataBitss.get(0);
	
	private ArrayList<String> stopBitss=new ArrayList<String>(Arrays.asList("1","2","1.5"));
	private String stopBits=stopBitss.get(0);
	
	private ArrayList<String> flowControls=new ArrayList<String>(Arrays.asList("NONE", "RTS/CTS", "XON/XOFF", "RTS/CTS+XON/XOFF", "RTS on TX"));
	private String flowControl=flowControls.get(0);
	
	private boolean connected=false;
	private SerialPort serialPort;
	private ArrayList<String> serialPorts=new ArrayList<String>();
	private int bytesRead=0;
	private int portsNum=0;
	private String message="Scan for ports!";
	

/*---------------------------------------------
  	scan for serial ports
---------------------------------------------*/	
	public boolean scanForPorts() 
	{

		Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier commId; 
		boolean portsFound=false;
		portsNum=0;
		serialPorts.clear();
		while (ports.hasMoreElements()) 
		{
		      commId = (CommPortIdentifier) ports.nextElement();
		      
		      if (commId.getPortType() == CommPortIdentifier.PORT_SERIAL)
		      {
		    	  serialPorts.add(commId.getName());
		    	  portsFound=true;
		    	  portsNum++;
		      } 
		      
		}
		return portsFound;

	}
	
/*---------------------------------------------
  	connect & disconnect functions
---------------------------------------------*/
	
	public String connect(String name)
	{
		CommPortIdentifier port;
		CommPort commPort;
		
		try
		{
			CommPortIdentifier.getPortIdentifiers();
			port = CommPortIdentifier.getPortIdentifier(name);
			commPort = (CommPort)port.open(this.getClass().getName(),2000);
			this.serialPort=(SerialPort)commPort;
			this.serialPort.setSerialPortParams(this.getBaudRate(),this.getDataBits(),this.getStopBits(),this.getParity());
			this.serialPort.setFlowControlMode(this.getFlowControl());
	        connected=true;	
	        message= "Port open";
		}
		catch(NoSuchPortException e)
		{
			message= "Error: No such port";
		}
		catch(PortInUseException e)
		{
			
			message= "Error: Port in use";
		} 
		catch (UnsupportedCommOperationException e) 
		{	
			message= "Unsupported comm. operation";
		}

		catch (Exception e) 
		{
			this.disconnect();
			message= "An error occured";
			
		}
		return message;
	

	}
	
	public String disconnect()
	{
		serialPort.close();
		connected=false;
		message= "Port closed";
		return message;
	}

/*---------------------------------------------
  	read & write functions
---------------------------------------------*/
	
	public void write(int b)
	{
		try(OutputStream outStream=serialPort.getOutputStream())
		{
			outStream.write(b);
		}
		catch(IOException e)
		{
			message="Write error";
		}
		catch(Exception e)
		{
			message="error";
		}
	}
	
	public void write(byte[] data)
	{
		try(OutputStream outStream=serialPort.getOutputStream())
		{
			outStream.write(data);
			outStream.flush();
			
		}
		catch(IOException e)
		{
			message="Write error";
		}
		catch(Exception e)
		{
			message="error";
		}
	
	}
	
	
	public void write(String s)
	{
		try(BufferedWriter outLine=new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream(), StandardCharsets.ISO_8859_1))) 
		{
			outLine.write(s);
			outLine.flush();
		}
		catch(IOException e)
		{
			message="Write error";
		}
		catch(Exception e)
		{
			message="error";
		}
	
	}
	

	public byte[] read(int n)
	{
		byte[] data=new byte[n];
		int b;
		try(InputStream inStream=serialPort.getInputStream())
		{		
            int i = 0;
            while ( ( b = inStream.read()) > -1 )
            {
    
            	if(i<n)
                {
            		data[i++] = (byte) b;
  
                }
            }
            bytesRead=i;
		} 
		catch(IOException e)
		{
			message="Read error";
		}
		catch(Exception e)
		{
			message="error";
		}
		return data;	
	}
	
	public String read()
	{
		StringBuilder readed=new StringBuilder("");
		int n;
		byte[] b=new byte[1]; 
		try(InputStream inStream=serialPort.getInputStream())
		{		
            int i = 0;
           
            while ( ( n = inStream.read()) > -1 )
            {
            	b[0]=(byte) n;
            	readed.append(new String(b, StandardCharsets.ISO_8859_1));
                i++;
            }
            bytesRead=i;
		} 
		catch(IOException e)
		{
			message="Read error";
		}
		catch(Exception e)
		{
			message="error";
		}
		return readed.toString();	
	}
	
	
	public String readSingle()
	{
		String readed="";
		int n;
		byte[] b=new byte[1]; 
		try(InputStream inStream=serialPort.getInputStream())
		{		
        
            if ( ( n = inStream.read()) > -1 )
            {
            	b[0]=(byte) n;
            	readed=new String(b, StandardCharsets.ISO_8859_1);

            }
            bytesRead=1;
		} 
		catch(IOException e)
		{
			message="Read error";
		}
		catch(Exception e)
		{
			message="error";
		}
		return readed;	
	}
	
	public String readLine()
	{
		String s="";
		
		try(BufferedReader inLine=new BufferedReader(new InputStreamReader(serialPort.getInputStream(), StandardCharsets.ISO_8859_1))) 
		{
			if(inLine.ready())
			{
				s=inLine.readLine();
							
			}
		} 
		catch(IOException e)
		{
			message="Read line error";
		}
		catch(Exception e)
		{
			message="error";
		}
		return s;
	}
	
/*---------------------------------------------
  	getters & setters
---------------------------------------------*/
	
	public int getNumOfPortFound()
	{
		return portsNum;
	}
	
	public SerialPort getSerialPort()
	{
		return this.serialPort;
	}
	
	public ArrayList<String> getSerialPorts()
	{
		return this.serialPorts;
	}
	
	public void setBaudRate(int bd)
	{
		this.baud=bd;
	
	}
	
	public int getBaudRate()
	{
		return this.baud;
	}
	
	public int getBytesRead()
	{
		return bytesRead;
	}
	
	public int getStopBits()
	{
		int sb=1;
		switch(this.stopBits)
		{
			case "1":
				sb=1;
				break;
			case "2":
				sb=2;
				break;
			case "1.5":
				sb=3;
				break;
		}
		return sb;
	}
	
	public void setStopBits(String s)
	{
		this.stopBits=s;
	}
	
	public int getDataBits()
	{
		return this.dataBits;
	}
	
	public void setDataBits(int i)
	{
		this.dataBits=i;
	}
	
	public int getParity()
	{
		int p=0;
		switch(this.parity)
		{
			case "NONE":
				p=0;
				break;
			case "ODD":
				p=1;
				break;
			case "EVEN":
				p=2;
				break;
			case "MARK":
				p=3;
				break;
			case "SPACE":
				p=4;
				break;
		}
		return p;
		
	}
	
	public void setParity(String s)
	{
		this.parity=s;
	}
	
	public int getFlowControl()
	{
		int p=0;
		switch(this.flowControl)
		{
			case "NONE":
				p=0;
				break;
			case "RTS/CTS":
				p=3;
				break;
			case "XON/XOFF":
				p=12;
				break;
			case "RTS/CTS+XON/XOFF":
				p=15;
				break;
			case "RTS on TX":
				p=2;
				break;
		}
		return p;
	}
	
	public void setFlowControl(String s)
	{
		this.flowControl=s;
	}
	
	public ArrayList<Integer> getBaudRates()
	{
		return this.bauds;
	}
	
	public ArrayList<Integer> getDataBitss()
	{
		return this.dataBitss;
	}
	public ArrayList<String> getStopBitss()
	{
		return this.stopBitss;
	}
	public ArrayList<String> getParities()
	{
		return this.parities;
	}
	
	public ArrayList<String> getFlowControls()
	{
		return this.flowControls;
	}

	
	public String getMessage()
	{
		return message;
	}
/*---------------------------------------------
  	flags
---------------------------------------------*/	
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public void setConnectedStatus(boolean c)
	{
		connected=c;
	}
	
	public boolean getChecked(String name)
	{
		CommPortIdentifier.getPortIdentifiers();
		try 
		{
			CommPortIdentifier.getPortIdentifier(name);
			return true;
		} 
		catch (NoSuchPortException e) 
		{
			return false;
		}
	}
	
}


