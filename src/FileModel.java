/*
 * Author: J. Bajic, 2018.
 */
public class FileModel {

	
	public static String generateCODE(int n)
	{
		
		int c=0;
		int b=n;
		int i=b%10;
		String code="";
		while(b>0)
		{
			b=b/10;
			i+=b%10;
			c++;
			
		}
		
		if(c<10)code+="0"+Integer.toString(c);
		else code+=Integer.toString(c);
		if(i<10)code+="0"+Integer.toString(i);
		else code+=Integer.toString(i);
		code+=Integer.toHexString(n).toUpperCase();
		code="CODE: "+code;
		return code;	
		
	}
	
	public static boolean checkCODE(String code)
	{
		code=code.replace(" ", "");
		int c=0;
		int i=0;
		int b=0;
		int sum=0;
		if(code.contains("CODE:"))
		{
			//System.out.println("CODE OK "+code);
			code=code.substring(5,code.length());
			//System.out.println(code);
			if(code.substring(0,2).matches("^[0-9]*"))
			{
				
				c=Integer.parseInt(code.substring(0,2));
				//System.out.println("c OK "+ Integer.toString(c));
				if(code.substring(2,4).matches("^[0-9]*"))
				{
					i=Integer.parseInt(code.substring(2,4));
					//System.out.println("i OK "+ Integer.toString(i));
					if(code.substring(4,code.length()).matches("^[0-9A-F]*"))
					{
						b=Integer.parseInt(code.substring(4,code.length()),16);
						//System.out.println("b OK "+ Integer.toString(b));
						for(int j=0;j<c;j++)
						{
							sum+=b%10;
							b=b/10;
						}
						if(sum==i) return true;
						else return false;
						
					}
					else return false;
					
				}
				else return false;
				
			}	
			else return false;
		}
		else return false;


			
				
	}
}
