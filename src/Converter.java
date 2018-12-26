/*
 * Author: J. Bajic, 2018.
 */
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Converter {
	
	
	public static final int HEX=16;
    public static final int DEC=10;
    public static final int BIN=2;
    public static final int ASCII=0;
    public static final char SEPARATOR='-';
    public static final boolean FINAL=false;
    public static final boolean CONTINIUE=true;
    
    public static final ArrayList<Integer> SPECIAL_CHARS=new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,28,30,31));
    public static final ArrayList<String> SPECIAL_CHARS_NAMES=new ArrayList<String>(Arrays.asList("<NULL>","<SOH>","<STX>","<ETX>", "<EOT>","<ENQ>", "<ACK>","<BEL>","<BS>","<HT>", "<LF>","<VT>", "<FF>","<CR>","<CO>","<SO>","<SI>","<DLE>","<DC1>", "<DC2>","<DC3>","<DC4>","<NAK>", "<SYN>", "<ETB>","<CAN>","<EM>","<SUB>","<ESC>","<FS>","<GS>","<RS>", "<US>"));
    		
    
    public static int length=0;
    
    public static String getSpecialChar(int value)
    {
    	return SPECIAL_CHARS_NAMES.get(value);
    }

    public static String generateASCIITable()
    {
    	String ASCIItable="";
    	String dec,hex,ascii;
    	byte[] b=new byte[1];

    	ASCIItable="DEC" + '\t'+'\t' + "HEX" + '\t'+'\t' + "ASCII" + '\t'+'\t' +"DEC" + '\t'+'\t' + "HEX" + '\t'+'\t' + "ASCII"+'\t'+'\t' +"DEC" + '\t'+'\t' + "HEX" + '\t'+'\t' + "ASCII"+'\n';
    	ASCIItable+="----------------------------------------------------------------------------------------------------------------------------------"+'\n';
    	
    	for(int i=0; i<=85;i++)
    	{
    		if(i<10)dec="00"+Integer.toString(i);
    		else dec="0"+Integer.toString(i);
    		
    		if(i<16)hex="0"+Integer.toHexString(i).toUpperCase();
    		else hex=Integer.toHexString(i).toUpperCase();
    		
    		if(i<32) ascii=SPECIAL_CHARS_NAMES.get(i);
    		

    		else
    		{
    			b[0]=(byte)i;
    			if(i==32) ascii="space";
    			else ascii=new String(b,StandardCharsets.ISO_8859_1)+'\t';
    		}
    		ASCIItable+=dec + '\t' + '\t'+ hex + '\t'+ '\t' + ascii + '\t'+ '\t';
    		if(i==16) ASCIItable+='\t';
    		
    		int j=i+85;
    		
    		if(j<100) dec="0"+Integer.toString(j);
    		else dec=Integer.toString(j);
    		hex=Integer.toHexString(j).toUpperCase();

    		b[0]=(byte)j;
    		if(j==127) ascii="<DEL>"+'\t';
    		else if (j==160) ascii="hard space";
    		else ascii=new String(b,StandardCharsets.ISO_8859_1)+'\t'+'\t';
    
    		
    		ASCIItable+=dec + '\t'+ '\t' + hex + '\t'+ '\t' + ascii +  '\t';
    		
    		j+=85;
    		
    		dec=Integer.toString(j);
    		hex=Integer.toHexString(j).toUpperCase();

    		b[0]=(byte)j;
    		ascii=new String(b,StandardCharsets.ISO_8859_1)+'\t';
    
    		
    		ASCIItable+=dec + '\t'+ '\t' + hex + '\t'+ '\t' + ascii + '\n';
    		
    		
    		
    	}
    	
    	ASCIItable+="----------------------------------------------------------------------------------------------------------------------------------"+'\n';
    	
    	return ASCIItable;
    }
    
    public static void setLength(int l)
    {
    	length=l;
    }
    
    public static int getLength()
    {
    	return length;
    }
    
    public static int getNumOfDigits(int f)
    {
    	int b=0;
    	switch(f)
    	{
    		case DEC:
    			b=3;
    			break;
    		case HEX:
    			b=2;
    			break;
    		case BIN:
    			b=8;
    			break;	
    	}	
    	return b;
    }
    
	public  String ByteArrayToStringASCII(byte[] b)
	{
		String s=new String(b,StandardCharsets.ISO_8859_1);
		return s;
	}
	
	public  String ByteArrayToStringASCII(byte[] b, int n)
	{
		String s=new String(b,0,n,StandardCharsets.ISO_8859_1);
		return s;
	}
	
	
	public  byte[] StringToByteArrayASCII(String s)
	{
			byte[] b;
			b = s.getBytes(StandardCharsets.ISO_8859_1);
			
			return b;
	}
	
	
	public int int16 (byte[] b, int s)
	{
		return ((int)(256*b[s]+b[s+1]));
	}
	
	public String ByteArrayToStringHEX(byte[] b)
	{
		String s="";
		for(byte i:b)
		{
			String s1=Integer.toHexString(i).toUpperCase();
			{
				while(s1.length()<2)
				{
					s1="0"+s1;
				}
			}
			if(s.isEmpty())s=s1;
			else s+=SEPARATOR+s1;
		}
		
		return s;
	}
	
	public String ByteArrayToStringHEX(byte[] b, int length)
	{
		String s="";
		for(int i=0; i<length; i++)
		{
			String s1=Integer.toHexString(unsignedByte(b[i])).toUpperCase();
			{
				while(s1.length()<2)
				{
					s1="0"+s1;
				}
			}
			if(s.isEmpty())s=s1;
			else s+=SEPARATOR+s1;
		}
		
		return s;
	}
	
	public String ByteArrayToStringBIN(byte[] b)
	{
		String s="";
		for(byte i:b)
		{
			String s1=Integer.toBinaryString(i);
			{
				while(s1.length()<8)
				{
					s1="0"+s1;
				}
			}
			if(s.isEmpty())s=s1;
			else s+=SEPARATOR+s1;
		}
	
		return s;
	}
	
	public String ByteArrayToStringDEC(byte[] b)
	{
		String s="";
		for(byte i:b)
		{
			String s1=Integer.toString(i);
			{
				while(s1.length()<3)
				{
					s1="0"+s1;
				}
			}
			if(s.isEmpty())s=s1;
			else s+=SEPARATOR+s1;
		}
		
		return s;
	}
	
	public String ByteArrayToStringDEC(byte[] b, int length)
	{
		String s="";
		//short uByte;
		for(int i=0;i<length; i++)
		{
			//uByte=unsignedByte(b[i]);
			String s1=Integer.toString(unsignedByte(b[i]));
			{
				while(s1.length()<3)
				{
					s1="0"+s1;
				}
			}
			if(s.isEmpty())s=s1;
			else s+=SEPARATOR+s1;
		}
		
		return s;
	}
	
	public String ByteArrayToString(byte[] b, int length, int f)
	{
		String s="";
	//	StringBuilder str=new StringBuilder("");

		if(f==ASCII)
		{
			s=new String(b,0,length,StandardCharsets.ISO_8859_1);
		//	str.append(new String(b,0,length,StandardCharsets.ISO_8859_1));
		}
		else
		{
			for(int i=0;i<length; i++)
			{
				String s1="";
			//	StringBuilder str1=new StringBuilder("");
				switch(f)
				{
					case DEC:
						s1=Integer.toString(unsignedByte(b[i]));
					//	str1.append(Integer.toString(unsignedByte(b[i])));
						break;
					case HEX:
						s1=Integer.toHexString(unsignedByte(b[i])).toUpperCase();
					//	str1.append(Integer.toHexString(unsignedByte(b[i])).toUpperCase());
						break;
					case BIN:
						s1=Integer.toBinaryString(unsignedByte(b[i]));
					//	str1.append(Integer.toBinaryString(unsignedByte(b[i])));
						break;
				}
							
				while(s1.length()<getNumOfDigits(f))
				{
						s1="0"+s1;
				}
			/*	while(str1.length()<getNumOfDigits(f))
				{
						str1.insert(0,'0');
				}*/
			
				if(s.isEmpty())s=s1;
				else s+=SEPARATOR+s1;
				
			//	if(str.equals(""))str=str1;
			//	else str.append(SEPARATOR).append(str1);
			}
		}
	return s;
		
	//	return str.toString();
	}
	
    public byte[] StringToByteArray(String string, int format)
    {
    
    	if(format==ASCII)
    	{
    		byte[] b = string.getBytes(StandardCharsets.ISO_8859_1);
    		setLength(string.length());
    		return b;
    		
    	}
    	else
    	{
	    	String s="0";
	    	byte[] b=new byte[1024];
	    	int bN=0;
	    	
	    	if(!string.isEmpty())
	    	{
	    		if(string.charAt(string.length()-1)==SEPARATOR)
		    	{
		    		string=string.substring(0, string.length()-1);
		    	}
	    	}
	    	int d=string.length();
	    	if(d>1024)d=1024;
	    	

	    	for(int i=0;i<d;i++)
	    	{
	    		if(string.charAt(i)==SEPARATOR)
	    		{
	    				b[bN]=getByte(s,format);
	    				bN++;
	    				s="0";
	    		}
	    		else
	    		{
	    	
	    			if(s.length()<(getNumOfDigits(format)+1)) s+=string.charAt(i);
	    		}
	    	}
	    	if(s=="0")	
	    	{
	    		setLength(bN);
	    		return b;
	    	}
	    	else
	    	{
				b[bN]=getByte(s,format);
				bN++;
				setLength(bN);
				return b;
	    	}
    	}
    	
    	
    }
    
    public byte getByte(String string, int format)
    {
		int j=Integer.parseInt(string,format);
		if(j>255)j=255;
		return (byte) j;
    }

	
	//--------------------------------------------------------
		public  short[] unsignedByteArray(byte[] sByte, int length)
		{
			short[] uByte=new short[length];
			for(int j=0;j<length;j++)
			{
				uByte[j]=(short)(sByte[j]&0x00FF);
			}
			
			return uByte;
		}
		
		public  byte[] signedByteArray(short[] uByte, int length)
		{
			byte[] sByte=new byte[length];
			for(int j=0;j<length;j++)
			{
				sByte[j]=(byte)(uByte[j]);
			}
			
			return sByte;
		}
		
		public  byte signedByte(short uByte)
		{
			return ((byte)uByte);
		}
		
		public  short unsignedByte(byte sByte)
		{
			return (short) (sByte & 0xFF);
		}
		
	//---------------------------------------------------
}
