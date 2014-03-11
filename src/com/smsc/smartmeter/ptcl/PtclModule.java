package com.smsc.smartmeter.ptcl;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


public class PtclModule
{
	static public class DataUnit
	{
		public short dataItemId;
		public byte databuf[] = new byte[1024];
		public int lenDataBuf;
	}
	

	// 反序 [0]秒 [1]分 [2]时 [3]日 [4]月 [5]年
	public final static int time_type_ssmmhhddmmyy      = 1;
	public final static int time_type_mmhhddmmyy        = 2;
	public final static int time_type_ddmmyy		    = 3;
	public final static int time_type_mmhhddmm          = 4;
	public final static int time_type_hhddmmyy          = 5;
	public final static int time_type_mmyy              = 6;
	public final static int time_type_hhddmm		    = 7;
	public final static int time_type_ssmmhhddwwmmyy = 8;
	public final static int time_type_mmhhdd            = 9; 
	public final static int time_type_wwddmmyy          = 10;
	public final static int time_type_ssmmhh            = 11;
	public final static int time_type_mmhh              = 12;
	public final static int time_type_ddmm              = 13;

	
		
	// 正序 [0]年 [1]月 [2]日 [3]时 [4]分 [5]秒
	public final static int time_type_yymmddhhmmss	= 21;
	public final static int time_type_yymmddhhmm	= 22;
	public final static int time_type_yymmdd		= 23;
	public final static int time_type_mmddhhmm		= 24;
	public final static int time_type_mmddhhmmss    = 25;
	public final static int time_type_ddhh          = 26;
	public final static int time_type_yymmddww		= 27;
	public final static int time_type_hhmmss  		= 28;
	
	protected byte hostID;
	protected ConcurrentHashMap<Integer, Byte> mapSeq = new ConcurrentHashMap<Integer, Byte>();
	
	public PtclModule()
	{
		hostID = 0;
	}

	public byte getHostID()
	{
		return hostID;
	}

	public void setHostID(byte id)
	{
		hostID = id;
	}
	
	public byte getFrameSeq(int addr)
	{
		if (addr == 0)
			return 0;


		if (!mapSeq.containsKey(addr))
		{
			mapSeq.put(addr, (byte)0);
			return 0;
		}
		else
		{
			byte seq = mapSeq.get(addr);
			if (seq == 127)
				seq = 0;
			else
				seq++;
			mapSeq.put(addr, seq);
			
			return seq;
		}
	}
	
	public void resetFrameSeq(int addr)
	{
		if (addr == 0)
			return ;
		
		mapSeq.put(addr,(byte)0);
	}
	
	public static void longToBytes(long src,byte []buffer,int posStart)
	{
		buffer[posStart+0] = (byte) (src & 0xFF);
		buffer[posStart+1] = (byte) ((src >>> 8) & 0xFF);
		buffer[posStart+2] = (byte) ((src >>> 16) & 0xFF);
		buffer[posStart+3] = (byte) ((src >>> 24) & 0xFF);
		buffer[posStart+4] = (byte) ((src >>> 32) & 0xFF);
		buffer[posStart+5] = (byte) ((src >>> 40) & 0xFF);
	}
	
	public static void intToBytes(int src,byte []buffer,int posStart)
	{
		buffer[posStart+0] = (byte) (src & 0xFF);
		buffer[posStart+1] = (byte) ((src >>> 8) & 0xFF);
		buffer[posStart+2] = (byte) ((src >>> 16) & 0xFF);
		buffer[posStart+3] = (byte) ((src >>> 24) & 0xFF);
	}
	
	public static void shortToBytes(short src,byte []buffer,int posStart)
	{
		buffer[posStart+0] = (byte) (src & 0xFF);
		buffer[posStart+1] = (byte) ((src >>> 8) & 0xFF);
	}
	
	public static long bytesToLong(byte []buffer,int posStart)
	{
		long dest = buffer[posStart+0] & 0xFF;
		dest = dest | ((long) (buffer[posStart+1] & 0xFF) << 8);
		dest = dest | ((long) (buffer[posStart+2] & 0xFF) << 16);
		dest = dest | ((long) (buffer[posStart+3] & 0xFF) << 24);
		dest = dest | ((long) (buffer[posStart+4] & 0xFF) << 32);
		dest = dest | ((long) (buffer[posStart+5] & 0xFF) << 40);
		
		return dest;
	}
	
	public static int bytesToInt(byte []buffer,int posStart)
	{
		int dest = buffer[posStart+0] & 0xFF;
		dest = dest | (buffer[posStart+1] & 0xFF) << 8;
		dest = dest | (buffer[posStart+2] & 0xFF) << 16;
		dest = dest | (buffer[posStart+3] & 0xFF) << 24;
		return dest;
	}
	
	public static short bytesToShort(byte []buffer,int posStart)
	{
		short dest = (short) (buffer[posStart+0] & 0xFF);
		dest = (short) (dest | (buffer[posStart+1] & 0xFF) << 8);
		
		return dest;
	}
	
	public static int htoni(int src)
	{
        int dest = (src & 0xFF) << 24;     
        dest = dest | (src & 0xFF00) << 8;   
        dest = dest | (src & 0xFF0000) >>> 8;   
        dest = dest | (src & 0xFF000000) >>> 24;  
        return dest; 
		
	}
	
	public static short htons(short src)
	{
		short dest = (short) ((src & 0xFF) << 8);     
        dest = (short) (dest | ((src & 0xFF00) >>> 8));   
        return dest;
	}
	
	
	//十六进制
	//private final static byte[] hex = "0123456789ABCDEF".getBytes();
	
	//字符转十进制
	private static int asciiToBin(char c) 
	{
		if (c >= 'a')
		    return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
		    return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	} 	
	
	// 从字节数组到radix进制字符串转换 
	//十六进制：0x07 0x31 0x00 0x0A-->"0731000A" 
	//十进制：0x0A -->"10"
	//二进制：0X0A -->"00001010"
	public static String bytesToNumberString(byte[] buffer,int offset,int len,int radix) 
	{
		if (radix == 16)
		{
			StringBuilder stringBuilder = new StringBuilder("");  
			if (buffer == null || buffer.length <= 0) 
			{  
			  return null;  
			}  
			for (int i = 0; i < len; i++) 
			{  
		        int v = buffer[offset + i] & 0xFF;  
			    String hv = Integer.toHexString(v);  
			    if (hv.length() < 2) 
			    {  
			        stringBuilder.append(0);  
			    }  
			    stringBuilder.append(hv);  
			} 
			
			return stringBuilder.toString();  

			
			
//			byte[] buff = new byte[2 * len];
//			for (int i = 0; i < len; i++) 
//			{
//				buff[2 * i] = hex[(byte)((buffer[offset+i] >> 4) & 0x0f)];
//				buff[2 * i + 1] = hex[(byte)(buffer[offset+i] & 0x0f)];
//			}
//			return new String(buff);
		}
		else if (radix == 10)
		{
			if(len == 1)
			{
				return String.valueOf(buffer[offset] & 0xff);
			}
			if (len == 2)
			{
				return String.valueOf(bytesToShort(buffer,offset));
			}
			else if (len == 4)
			{
				return String.valueOf(bytesToInt(buffer,offset));
			}
		}
		else if (radix == 2)
		{
			String str = new String();
			for (int i = 0; i < len; i++)
			{
				for (int n=0;n<8;n++)
				{
					int b = (buffer[offset+i] >>> n) & 0x01;
					str = String.valueOf(b) + str;
				}
			}
			return str;
		
			
		}
		
		return null;
		
	} 	
	
	// 从十六进制、十进制、二进制字符串到字节数组转换 "07310001" --> 0x07 0x31 0x00 0x01
	public static void numberStringToBytes(String str,int radix,byte[] buffer,int offset) 
	{
		if(str == null)
			return;
		
		if (radix == 16)
		{
			int j = 0;
			for (int i = 0; i < str.length()/2; i++) 
			{
				char c0 = str.charAt(j++);
				char c1 = str.charAt(j++);
				buffer[offset+i] = (byte) ((asciiToBin(c0) << 4) | asciiToBin(c1));
			} 
		
		}
		else if (radix == 10)
		{
			intToBytes(Integer.valueOf(str,10),buffer,offset);
		}
		else if (radix == 2)
		{
			intToBytes(Integer.valueOf(str,2),buffer,offset);
		}
	}
	
	//将ascii字节数组码转换为字符串  反序
	public static String asciiByteToString(byte[] buffer,int offset,int len)
	{
		if(buffer.length - offset < len)
			return "";
		
		String strValue = "";
		char cTemp = 0;
		for(int i=0;i<len;i++)
		{
			cTemp = (char)buffer[offset+i];
			
			strValue = String.valueOf(cTemp) + strValue;
		}
		
		return strValue.trim();
		
	}
	
	//将ascii字节数组码转换为字符串  正序
	public static String asciiByteToStringOrder(byte[] buffer,int offset,int len)
	{
		if(buffer.length - offset < len)
			return "";
		
		String strValue = "";
		char cTemp = 0;
		for(int i=0;i<len;i++)
		{
			cTemp = (char)buffer[offset+i];
			
			strValue =  strValue + String.valueOf(cTemp);
		}
		
		return strValue.trim();
		
	}
	
	//将字符串转换为ascii字节数组 反序
	public static void stringToAsciiByte(String str,byte[] buffer,int offset,int len)
	{
		if(str == null)
			return;
		
		char[]chars=str.toCharArray(); 
		int strLen = str.length();
		for(int i=0;i<strLen;i++)
		{
			buffer[offset+len-i-1] = (byte)chars[i];
		}	
	}
	
	//将字符串转换为ascii字节数组 正序
	public static int stringToAsciiByteOrder(String str,byte[] buffer,int offset)
	{
		int len = 0;
		if(str == null)
			return 0;
		
		char[]chars=str.toCharArray(); 
		int strLen = str.length();
		for(int i=0;i<strLen;i++)
		{
			buffer[offset+i] = (byte)chars[i];
			len ++;
		}
		
		return len;
	}
	
	
	
	
	public static boolean isInvalidData(byte[] buffer,int offset,int len,byte invalid)
	{
		for (int n=0;n<len;n++)
		{
			if (buffer[offset+n] != invalid)
				return false;
		}
		return true;
	}
	
	
	//99 -> 0x99
	public static byte binToBcd(byte bin)
	{
		if (bin > 99)
			return 0;

		return (byte)((bin/10)*16 + bin%10);
	}
	
	//0x99 -> 99
	public static byte bcdToBin(byte bcd)
	{
		byte b1 = (byte)((bcd & 0xff)/16);
		if (b1 > 9)
			return 0;

		byte b0 = (byte)((bcd & 0xff)%16);
		if (b0 > 9)
			return 0;

		return (byte)(b1*10 + b0);
	}
	
	//0x12 0x34 0x56 0x78 90 12 -> 129078563412
	public static long bcdToBin(byte[] buffer,int offset,int len)
	{

		long value = 0;
		for (int n=len-1;n>=0;n--)
		{
			long pow = 1;
			for (int m=0;m<n;m++)
				pow = pow * 100;

			value = value + (bcdToBin(buffer[offset+n]) * pow);
			
		}

		return value;
	}
	
	
	//0x12 0x34 0x56 0x78 -> 78563412
	public static int bcdToBin(byte[] buffer,int offset,int len,byte invalid)
	{
		if (len > 4)
			return 0;

		if (isInvalidData(buffer,offset,len,invalid) == true)
			return -10;


		int value = 0;
		for (int n=len-1;n>=0;n--)
		{
			int pow = 1;
			for (int m=0;m<n;m++)
				pow = pow * 100;

			value = value + (bcdToBin(buffer[offset+n]) * pow);
			
		}

		return value;
	}
	
	//0x12 0x34 0x56 0x78 -> 785634.12
	public static double bcdToBin(byte[] buffer,int offset,int len,int countRadixPoint,byte invalid)
	{
		if (len > 5 || countRadixPoint > 10)
			return 0;

		if (isInvalidData(buffer,offset,len,invalid) == true)
			return -10;

		int value = 0;
		for (int n=len-1;n>=0;n--)
		{
			int pow = 1;
			for (int m=0;m<n;m++)
				pow = pow * 100;

			value = value + (bcdToBin(buffer[offset+n]) * pow);
		}

		double pow = 1;
		if (countRadixPoint >= 0)
		{
			for (int m=0;m<countRadixPoint;m++)
				pow = pow * 10;

			return value / pow;
		}
		else
		{
			for (int m=0;m<(-1*countRadixPoint);m++)
				pow = pow * 10;
			return value * pow;
		}
	}
	
	//12345678 -> 0x78 0x56 0x34 0x12
	public static void binToBcd(int value,byte[] buffer,int offset)
	{
		int n = 0;
		while (value > 0)
		{
			buffer[offset+n] = binToBcd((byte)(value % 100));
			
			value = value / 100;
			n++;
		}
	}
	
	//123456789012 -> 0x12 0x90 0x78 0x56 0x34 0x12
	public static void binToBcd(long value,byte[] buffer,int offset)
	{
		int n = 0;
		while (value > 0)
		{
			buffer[offset+n] = binToBcd((byte)(value % 100));
			
			value = value / 100;
			n++;
		}
	}
	
	public static void binToBcd(double value,int len,int countRadixPoint,byte[] buffer,int offset)
	{
		int pow = 1;
		for(int i= 0;i<countRadixPoint;i++)
			pow = pow * 10;
			
			
		if(len <= 4)
		{
			int iValue = (int)(value * pow);		
			binToBcd(iValue,buffer,offset);
		}
		else if(len > 4 && len <=6)
		{
			long iValue = (long)(value * pow);		
			binToBcd(iValue,buffer,offset);						
		}
	}
	/*
	 * 字符串数据转换为字节数组："1234.56"->0x56 0x34 0x12
	 * intCount:整数位数
	 * pointCount:小数位数
	 */
	public static void BinToBcd(String value,int intCount,int PointCount,byte[] buffer,int offset)
	{
		int point = value.indexOf(".");
        String intData = "";
        String pointData = "";
		if(point > 0)
		{
			intData = value.substring(0, point);
			pointData = value.substring(point+1,value.length());
		}
		else
		{
			intData = value;	
		}
		
		for(int i=intData.length();i<intCount;i++)
		{
			intData = "0" + intData;		
		}
		for(int i=pointData.length();i<PointCount;i++)
		{
			pointData = "0" + pointData;		
		}
		
		String valueData = intData + pointData;
		
		int n=0;
		for(int i=valueData.length();i>=2;i=i-2)
		{
			byte bValue = Byte.valueOf(valueData.substring(i-2, i));
			buffer[offset+n] = binToBcd(bValue);
			n++;
		}

	}
	
	/*
	 * 带符号位的字符串数据转换为字节数组："-234.56"->0x56 0x34 0x12
	 * intCount:整数位数
	 * pointCount:小数位数
	 */
	public static void BinToBcdWithS(String value,int intCount,int PointCount,byte[] buffer,int offset)
	{
		double dValue = Double.valueOf(value);
		int s = 0;
		if(dValue < 0)
		{
			dValue = 0-dValue;
			s = 1;
		}
		
		int point = value.indexOf(".");
        String intData = "";
        String pointData = "";
		if(point > 0)
		{
			if(s==1)
				intData = value.substring(1, point);
			else
				intData = value.substring(0, point);
			
			pointData = value.substring(point+1,value.length());
		}
		else
		{
			if(s==1)
				intData = value.substring(1, value.length());
			else
				intData = value;	

		}
		
		for(int i=intData.length();i<intCount;i++)
		{
			intData = "0" + intData;		
		}
		for(int i=pointData.length();i<PointCount;i++)
		{
			pointData = "0" + pointData;		
		}
		
		String valueData = intData + pointData;
		
		int n=0;
		for(int i=valueData.length();i>=2;i=i-2)
		{
			byte bValue = Byte.valueOf(valueData.substring(i-2, i));
			buffer[offset+n] = binToBcd(bValue);
			n++;
		}
		
		int sOffset = offset+(intCount+PointCount)/2-1;
		if(s == 1)
			buffer[sOffset] = (byte)(buffer[sOffset] | 0x10);
		else
			buffer[sOffset] = (byte)(buffer[sOffset] & 0x0f);
	}
	
	/*
	 * 字节数组转换为带符号位的数据
	 */
	//0x12 0x34 0x56 0x17 -> -75634.12
	public static double bcdToBinWithS(byte[] buffer,int offset,int len,int countRadixPoint,byte invalid)
	{
		if (isInvalidData(buffer,offset,len,(byte)0xee) == true)
		{
			return -10;
		}
		
		byte s = (byte)((buffer[offset+len-1] & 0xf0)>>>4);
		
		byte []temp = new byte[len];
		System.arraycopy(buffer,offset,temp,0,len);
		temp[len-1] = (byte)(temp[len-1] & 0x0f);
		
		double d = PtclModule.bcdToBin(temp, 0, len, countRadixPoint, (byte)0xff);

		if (s == 1)
			d = 0 - d;

		return d;
	}
	
	@SuppressWarnings("static-access")
	public static String bcdToTime(byte[] buffer,int offset,int len,int type,byte invalid)
	{
		SimpleDateFormat dateformat= null;
		int len_parse = 0;
		String timeValue = null;
		
		if (isInvalidData(buffer,offset,len,invalid) == true)
		{
			return String.valueOf(-10);
		}		

		if (type == time_type_mmhhddmmyy)
		{
			len_parse = 5;
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.MINUTE,bcdToBin(buffer[offset++]));
			calendar.set(calendar.HOUR_OF_DAY,bcdToBin(buffer[offset++]));
			calendar.set(calendar.DAY_OF_MONTH,bcdToBin(buffer[offset++]));
			calendar.set(calendar.MONTH,bcdToBin(buffer[offset++])-1);
			calendar.set(calendar.YEAR,bcdToBin(buffer[offset++])+2000);
			
			dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");	
						
			timeValue = dateformat.format(calendar.getTime());
		}
		
		//秒分时日星期月年中，星期和月共用一个字节，其余的是单独的一个字节
		else if (type == time_type_ssmmhhddwwmmyy )
		{
			len_parse = 6;
			if (len < len_parse)
				return null;
			Calendar calendar = Calendar.getInstance();
			
			calendar.set(calendar.SECOND, bcdToBin(buffer[offset++]));
			calendar.set(calendar.MINUTE, bcdToBin(buffer[offset++]));
			calendar.set(calendar.HOUR_OF_DAY,bcdToBin(buffer[offset++]));
			calendar.set(calendar.DAY_OF_MONTH,bcdToBin(buffer[offset++]));
			//calendar.set(calendar.DAY_OF_WEEK, bcdToBin((byte)((buffer[offset]& 0xe0)>>>5))+1);
			calendar.set(calendar.MONTH,bcdToBin((byte)(buffer[offset++]& 0x1f))-1);			
			calendar.set(calendar.YEAR,bcdToBin(buffer[offset++])+2000);
			
			dateformat = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss");	
			
			timeValue = dateformat.format(calendar.getTime());
		}
		else if (type == time_type_mmhhddmm )
		{
			len_parse = 4;
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.MINUTE, bcdToBin(buffer[offset++]));
			calendar.set(calendar.HOUR_OF_DAY,bcdToBin(buffer[offset++]));
			calendar.set(calendar.DAY_OF_MONTH,bcdToBin(buffer[offset++]));
			calendar.set(calendar.MONTH,bcdToBin(buffer[offset++]) -1);
			
			dateformat = new SimpleDateFormat("MM-dd HH:mm");	
			
			timeValue = dateformat.format(calendar.getTime());
		}
		else if (type == time_type_ssmmhhddmmyy )
		{
			len_parse = 6;
			
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();			
			calendar.set(calendar.SECOND, bcdToBin(buffer[offset++]));
			calendar.set(calendar.MINUTE, bcdToBin(buffer[offset++]));
			calendar.set(calendar.HOUR_OF_DAY,bcdToBin(buffer[offset++]));
			calendar.set(calendar.DAY_OF_MONTH,bcdToBin(buffer[offset++]));
			calendar.set(calendar.MONTH,bcdToBin(buffer[offset++])-1);			
			calendar.set(calendar.YEAR,bcdToBin(buffer[offset++])+2000);
			
			dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
			
			timeValue = dateformat.format(calendar.getTime());
		}
		else if (type == time_type_wwddmmyy )
		{
			len_parse = 4;
			if (len < len_parse)
				return null;
			Calendar calendar = Calendar.getInstance();
			
			//calendar.set(calendar.DAY_OF_WEEK, bcdToBin(buffer[offset++])+2);	
			offset++;//周
			calendar.set(calendar.DAY_OF_MONTH,bcdToBin(buffer[offset++]));
			calendar.set(calendar.MONTH,bcdToBin(buffer[offset++])-1);	
			calendar.set(calendar.YEAR,bcdToBin(buffer[offset++])+2000);
			
			dateformat = new SimpleDateFormat("yyyy-MM-dd EEE");	
			
			timeValue = dateformat.format(calendar.getTime());
		
		}
		else if (type == time_type_ssmmhh)
		{
            len_parse = 3;
			
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();			
			calendar.set(calendar.SECOND, bcdToBin(buffer[offset++]));
			calendar.set(calendar.MINUTE, bcdToBin(buffer[offset++]));
			calendar.set(calendar.HOUR_OF_DAY,bcdToBin(buffer[offset++]));
			
            dateformat = new SimpleDateFormat("HH:mm:ss");	
			
			timeValue = dateformat.format(calendar.getTime());
		}
		else if (type == time_type_mmhh)
		{
            len_parse = 2;
			
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();					
			calendar.set(calendar.MINUTE, bcdToBin(buffer[offset++]));
			calendar.set(calendar.HOUR_OF_DAY,bcdToBin(buffer[offset++]));
			
            dateformat = new SimpleDateFormat("HH:mm");	
			
			timeValue = dateformat.format(calendar.getTime());
			
		}
		else if (type == time_type_ddhh)
		{
            len_parse = 2;
			
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();	
			calendar.set(calendar.DAY_OF_MONTH,bcdToBin(buffer[offset++]));
			calendar.set(calendar.HOUR_OF_DAY,bcdToBin(buffer[offset++]));
			
            dateformat = new SimpleDateFormat("dd HH");	
			
			timeValue = dateformat.format(calendar.getTime());
			
		}
		else if (type == time_type_yymmddww)
		{
			len_parse = 4;
			
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.YEAR, bcdToBin(buffer[offset++]));
			calendar.set(calendar.MONTH, bcdToBin(buffer[offset++]));
			calendar.set(calendar.DAY_OF_MONTH, bcdToBin(buffer[offset++]));
			calendar.set(calendar.WEEK_OF_MONTH, bcdToBin(buffer[offset++]));
			
			dateformat = new SimpleDateFormat("yy-MM-dd WW");
			timeValue = dateformat.format(calendar.getTime());
		}
		else if (type == time_type_hhmmss)
		{
			len_parse = 3;
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.HOUR_OF_DAY, bcdToBin(buffer[offset++]));
			calendar.set(calendar.MINUTE, bcdToBin(buffer[offset++]));
			calendar.set(calendar.SECOND, bcdToBin(buffer[offset++]));
			
			dateformat = new SimpleDateFormat("HH:mm:ss");
			timeValue = dateformat.format(calendar.getTime());
		}
		else if (type == time_type_mmhhdd)
		{
			len_parse = 3;
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.MINUTE, bcdToBin(buffer[offset++]));
			calendar.set(calendar.HOUR_OF_DAY, bcdToBin(buffer[offset++]));
			calendar.set(calendar.DAY_OF_MONTH, bcdToBin(buffer[offset++]));
			
			dateformat = new SimpleDateFormat("dd HH:mm");
			timeValue = dateformat.format(calendar.getTime());
		}
		else if (type == time_type_ddmm)
		{
			len_parse = 2;
			if (len < len_parse)
				return null;
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.DAY_OF_MONTH,bcdToBin(buffer[offset++]));
			calendar.set(calendar.MONTH,bcdToBin(buffer[offset++])-1);
			
			dateformat = new SimpleDateFormat("MM-dd");
			timeValue = dateformat.format(calendar.getTime());
		}
		else if (type == time_type_ddmmyy )
		{
			len_parse = 3;
			if (len < len_parse)
				return null;
			Calendar calendar = Calendar.getInstance();
						
			calendar.set(calendar.DAY_OF_MONTH,bcdToBin(buffer[offset++]));
			calendar.set(calendar.MONTH,bcdToBin(buffer[offset++])-1);	
			calendar.set(calendar.YEAR,bcdToBin(buffer[offset++])+2000);
			
			dateformat = new SimpleDateFormat("yyyy-MM-dd");	
			
			timeValue = dateformat.format(calendar.getTime());
		
		}
			
		return timeValue;
	}
	
	public static void timeToBcd(String tm,byte[] buffer,int offset,int type) 
	{
		if (type == time_type_ssmmhhddmmyy)
		{			
			DateFormat fmt = new SimpleDateFormat("yy-MM-dd HH:mm:ss"); 

			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			
			int year = cal.get(Calendar.YEAR); 
			String sYear = Integer.toString(year);
			String subYear = sYear.substring(2, 4);
			year = Integer.valueOf(subYear);
			
			int month = cal.get(Calendar.MONTH)+1; 
			int day = cal.get(Calendar.DAY_OF_MONTH); 
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);
			
			buffer[offset++] = PtclModule.binToBcd((byte)second);
			buffer[offset++] = PtclModule.binToBcd((byte)minute);
			buffer[offset++] = PtclModule.binToBcd((byte)hour);
			buffer[offset++] = PtclModule.binToBcd((byte)day);
			buffer[offset++] = PtclModule.binToBcd((byte)month);
			buffer[offset++] = PtclModule.binToBcd((byte)year);
			
			return;
		}
		if (type == time_type_ssmmhh)
		{			
			DateFormat fmt = new SimpleDateFormat("HH:mm:ss"); 

			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);
			
			buffer[offset++] = PtclModule.binToBcd((byte)second);
			buffer[offset++] = PtclModule.binToBcd((byte)minute);
			buffer[offset++] = PtclModule.binToBcd((byte)hour);
			
			return;
		}
		if (type == time_type_mmhh)
		{
			DateFormat fmt = new SimpleDateFormat("HH:mm"); 
			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			buffer[offset++] = PtclModule.binToBcd((byte)minute);
			buffer[offset++] = PtclModule.binToBcd((byte)hour);		
		}
		if (type == time_type_ddhh)
		{
			DateFormat fmt = new SimpleDateFormat("dd HH"); 
			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			buffer[offset++] = PtclModule.binToBcd((byte)day);
			buffer[offset++] = PtclModule.binToBcd((byte)hour);		
		}
		
		if(type == time_type_yymmddhhmm)
		{
			DateFormat fmt = new SimpleDateFormat("yy-MM-dd HH:mm"); 

			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			
			int year = cal.get(Calendar.YEAR); 
			String sYear = Integer.toString(year);
			String subYear = sYear.substring(2, 4);
			year = Integer.valueOf(subYear);
			
			int month = cal.get(Calendar.MONTH)+1; 
			int day = cal.get(Calendar.DAY_OF_MONTH); 
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			//int second = cal.get(Calendar.SECOND);
			
			buffer[offset++] = PtclModule.binToBcd((byte)year);
			buffer[offset++] = PtclModule.binToBcd((byte)month);
			buffer[offset++] = PtclModule.binToBcd((byte)day);
			buffer[offset++] = PtclModule.binToBcd((byte)hour);
			buffer[offset++] = PtclModule.binToBcd((byte)minute);
			
			return;		
		}
		if(type == time_type_wwddmmyy)
		{
			DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd"); 

			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			
			int year = cal.get(Calendar.YEAR); 
			String sYear = Integer.toString(year);
			String subYear = sYear.substring(2, 4);
			year = Integer.valueOf(subYear);
			
			int month = cal.get(Calendar.MONTH)+1; 
			int day = cal.get(Calendar.DAY_OF_MONTH); 
			int week = cal.get(Calendar.DAY_OF_WEEK);  
	
			buffer[offset++] = PtclModule.binToBcd((byte)week);
			buffer[offset++] = PtclModule.binToBcd((byte)day);
			buffer[offset++] = PtclModule.binToBcd((byte)month);
			buffer[offset++] = PtclModule.binToBcd((byte)year);
			
			return;		
		}
		if (type == time_type_mmhhdd)
		{
			DateFormat fmt = new SimpleDateFormat("dd HH:mm"); 
			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			buffer[offset++] = PtclModule.binToBcd((byte)min);	
			buffer[offset++] = PtclModule.binToBcd((byte)hour);	
			buffer[offset++] = PtclModule.binToBcd((byte)day);	
		}
		if (type == time_type_ddmm)
		{
			DateFormat fmt = new SimpleDateFormat("MM-dd"); 
			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int mon = cal.get(Calendar.MONTH) + 1;

			buffer[offset++] = PtclModule.binToBcd((byte)day);	
			buffer[offset++] = PtclModule.binToBcd((byte)mon);	
		}
		if(type == time_type_ddmmyy)
		{
			DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd"); 

			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			
			int year = cal.get(Calendar.YEAR); 
			String sYear = Integer.toString(year);
			String subYear = sYear.substring(2, 4);
			year = Integer.valueOf(subYear);
			
			int month = cal.get(Calendar.MONTH)+1; 
			int day = cal.get(Calendar.DAY_OF_MONTH);			
				
			buffer[offset++] = PtclModule.binToBcd((byte)day);
			buffer[offset++] = PtclModule.binToBcd((byte)month);
			buffer[offset++] = PtclModule.binToBcd((byte)year);
			
			return;		
		}
		if (type == time_type_ssmmhhddwwmmyy)
		{			
			DateFormat fmt = new SimpleDateFormat("yy-MM-dd HH:mm:ss"); 

			Date d = null;
			try {
				d = fmt.parse(tm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			
			int year = cal.get(Calendar.YEAR); 
			String sYear = Integer.toString(year);
			String subYear = sYear.substring(2, 4);
			year = Integer.valueOf(subYear);
			
			int month = cal.get(Calendar.MONTH)+1; 
			int day = cal.get(Calendar.DAY_OF_MONTH); 
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);
			int week  = cal.get(Calendar.DAY_OF_WEEK);
			
			buffer[offset++] = PtclModule.binToBcd((byte)second);
			buffer[offset++] = PtclModule.binToBcd((byte)minute);
			buffer[offset++] = PtclModule.binToBcd((byte)hour);
			buffer[offset++] = PtclModule.binToBcd((byte)day);
			buffer[offset++] = (byte)((PtclModule.binToBcd((byte)week)) << 5 | (PtclModule.binToBcd((byte)month) & 0x1F));
			buffer[offset++] = PtclModule.binToBcd((byte)year);
			
			return;
		}
		
	}
	
	public static short convertDataItemIdStringToShort(String dataItemId)
	{
		if(dataItemId.length() != 4)
			return 0;
		byte bFn[] = new byte[2]; 
    	PtclModule.numberStringToBytes(dataItemId, 16, bFn, 0);
    	return PtclModule.htons(PtclModule.bytesToShort(bFn, 0));
	}
	
	
	public static String convertDataItemIdShortToString(short dataItemId)
	{
		byte bFn[] = new byte[2];
		PtclModule.shortToBytes(PtclModule.htons(dataItemId), bFn, 0);	
		String sDataItemId = PtclModule.bytesToNumberString(bFn, 0, 2, 16);
		return sDataItemId;
	}
	
	public static int convertDataItemIdStringToInt(String dataItemId)
	{
		if(dataItemId.length() != 8)
			return 0;
		byte bFn[] = new byte[4]; 
    	PtclModule.numberStringToBytes(dataItemId, 16, bFn, 0);
    	return PtclModule.htoni(PtclModule.bytesToInt(bFn, 0));
	}
	
	public static String convertDataItemIdIntToString(int dataItemId)
	{
		byte bFn[] = new byte[4];
		PtclModule.intToBytes(PtclModule.htoni(dataItemId), bFn, 0);
		String sDataItemId = PtclModule.bytesToNumberString(bFn, 0, 4, 16);
		return sDataItemId;
	}
	
	//type 0:顺序  1：反序
	public static void formatIpAddress(String ipAddress,byte[] buffer,int offset,byte type)
	{	
		byte ip[] = new byte[4];
		int position1 = ipAddress.indexOf(".");
		int position2 = ipAddress.indexOf(".", position1+1);
		int position3 = ipAddress.indexOf(".", position2+1);
		
		
		ip[0] = (byte)(Integer.valueOf(ipAddress.substring(0, position1)) & 0xFF);
		ip[1] = (byte)(Integer.valueOf(ipAddress.substring(position1+1, position2)) & 0xFF);
		ip[2] = (byte)(Integer.valueOf(ipAddress.substring(position2+1, position3)) & 0xFF);
		ip[3] = (byte)(Integer.valueOf(ipAddress.substring(position3+1, ipAddress.length())) & 0xFF);
		
		if(type == 0)
		{
			for(int i=0;i<4;i++)
			{
				buffer[offset++] = ip[i];
			}
			
		}
		else
		{
			for(int i=0;i<4;i++)
			{
				buffer[offset++] = ip[3-i];
			}
		}
	}
	
	//type 0:顺序  1：反序
	public static String parseIpAddress(byte[] buffer,int offset,byte type)
	{
		if((buffer.length - offset)<4)
			return "";
		
		String ip = null;
		
		if(type == 0)
		{
			String ip1 =  PtclModule.bytesToNumberString(buffer, offset++, 1, 10);
			String ip2 =  PtclModule.bytesToNumberString(buffer, offset++, 1, 10);
			String ip3 =  PtclModule.bytesToNumberString(buffer, offset++, 1, 10);
			String ip4 =  PtclModule.bytesToNumberString(buffer, offset++, 1, 10);
			ip = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
		}
		else
		{
		
			String ip4 =  PtclModule.bytesToNumberString(buffer, offset++, 1, 10);
			String ip3 =  PtclModule.bytesToNumberString(buffer, offset++, 1, 10);
			String ip2 =  PtclModule.bytesToNumberString(buffer, offset++, 1, 10);
			String ip1 =  PtclModule.bytesToNumberString(buffer, offset++, 1, 10);
			ip = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
		}
		
		return ip;		
	}
	
	public static String parseSG1(byte[] buffer,int offset,int len,int type)
	{
		return PtclModule.bcdToTime(buffer, offset, len, type, (byte)0xee);		
	}
	
	public static double parseSG2(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<2)
			return 0;
		
		if (isInvalidData(buffer,offset,2,(byte)0xee) == true)
		{
			return -10;
		}
		
		int s = (buffer[offset + 1] & 0x10)>>4;
		int g = (buffer[offset + 1] & 0xe0)>>5;
		
		int countRadixPoint = -4 + g;
		
		byte []temp = new byte[2];
		temp[0] = buffer[offset];
		temp[1] = (byte)(buffer[offset + 1] & 0x0F);
		
		double value = bcdToBin(temp,0,2,countRadixPoint,(byte)0xee);
		if(s == 1)
			value = 0 - value;
		return value;
	}
	
	public static long parseSG3(byte[] buffer,int offset)
	{
		long d = 0;
		if (isInvalidData(buffer,offset,4,(byte)0xee) == true)
		{
			return -10;
		}
		
		byte s = (byte)((buffer[offset+3] & 0x10) >>> 4);
		byte g = (byte)((buffer[offset+3] & 0x40) >>> 6);
		
		byte []temp = new byte[4];
		System.arraycopy(buffer,offset,temp,0,4);
		temp[3] = (byte)(temp[3] & 0x0f);
		
		d = bcdToBin(temp,0,4,(byte)0xee);
		if (g == 1)
			d = d * 1000;
		if (s == 1)
			d = 0 - d;

		return d;
	}
	
	public static void formatSG3(long value,byte[] buffer,int offset)
	{
		byte s = 0;
		byte g = 0;
		
		if (value < 0)
		{
			s = 1;
			value = 0 - value;
		}
		
		if (value % 1000 == 0 && value != 0)
		{
			g = 1;
			value = value / 1000;
		}
		
		binToBcd(value,buffer,offset);
		buffer[offset+3] = (byte)(buffer[offset+3] | (s<<4));
		buffer[offset+3] = (byte)(buffer[offset+3] | (g<<6));
		
	}
	
	public static void formatSG4(int value,byte[] buffer,int offset)
	{
		int iValue = (value);
		byte s = 0;
		
		if (value < 0)
		{
			s = 1;
			iValue = 0 - iValue;
			iValue = (((iValue/10)<<4) & 0x70)|((iValue%10) & 0x0F)| ((s & 0xff)<<7 );
			buffer[offset] =(byte) (iValue & 0xFF);
		}
		else
		{
		    binToBcd(iValue,buffer,offset);
		}
		
	}
	
	public static int parseSG4(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<1)
			return 0;
		
		if (isInvalidData(buffer,offset,2,(byte)0xee) == true)
		{
			return -10;
		}
		
		int s = (buffer[offset] & 0xff)>>7;
		
		byte bValue = (byte)(buffer[offset] & 0x7f);
		
		int value = bcdToBin(bValue);
		
		if(s == 1)
			value = 0 - value;
		return value;
	}

	public static double parseSG5(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<2)
			return 0;
		
		if (isInvalidData(buffer,offset,2,(byte)0xee) == true)
		{
			return -10;
		}
		
		byte temp[] = new byte[2];
		System.arraycopy(buffer, offset, temp, 0, 2);
		byte s = (byte)((temp[1] & 0x80)>>7);
		temp[1] = (byte)(temp[1] & 0x7F);
		double value = PtclModule.bcdToBin(temp, 0, 2, 1, (byte)0xee);
		
		if(s == 1)
			value = 0 - value;
			
		return value;
	}
	
	public static void formatSG5(Double value,byte[] buffer,int offset)
	{
		int iValue = (int)(value * 10);
		byte s = 0;
		
		if (value < 0)
		{
			s = 1;
			iValue = 0 - iValue;
			int iValue1;
			iValue1 = (((iValue/1000)<<4) & 0x70)|(((iValue%1000)/100)&0x0F)|((s & 0xff)<<7 );
			binToBcd((iValue%100),buffer,offset);
			buffer[offset+1] =(byte) (iValue1 & 0xFF);			
		}
		else
		{
			binToBcd(iValue,buffer,offset);
		}		
	}	
	
	public static double parseSG7(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<2)
			return 0;
		
		double value = PtclModule.bcdToBin(buffer, offset, 2, 1, (byte)0xee);
		
		return value;
	}
	
	public static void formatSG7(Double value,byte[] buffer,int offset)
	{	
		int iValue = (int)(value * 10);
		binToBcd(iValue,buffer,offset);	
		return;
	}
	
	
	public static double parseSG9(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<3)
			return 0;
		
		if (isInvalidData(buffer,offset,3,(byte)0xee) == true)
		{
			return -10;
		}
		
		byte temp[] = new byte[3];
		System.arraycopy(buffer, offset, temp, 0, 3);
		byte s = (byte)(temp[2]>>7);
		temp[2] = (byte)(temp[2] & 0x7F);
		double value = PtclModule.bcdToBin(temp, 0, 3, 4, (byte)0xee);
		
		if(s == 1)
			value = 0 - value;
			
		return value;
		
	}
	
	public static double parseSG11(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<4)
			return 0;
		
		if (isInvalidData(buffer,offset,4,(byte)0xee) == true)
		{
			return -10;
		}
		
		double value = 0;
		for (int n=3;n>=0;n--)
		{
			long pow = 1;
			for (int m=0;m<n;m++)
				pow = pow * 100;

			value = value + (bcdToBin(buffer[offset+n]) * pow);
		}
		
		//小数点
		value = value/100;
						
		return value;
	}
	
	public static long parseSG12(byte[] buffer,int offset)
	{
		if (isInvalidData(buffer,offset,6,(byte)0xee) == true)
		{
			return -10;
		}
		
		long value = 0;
		for (int n=5;n>=0;n--)
		{
			long pow = 1;
			for (int m=0;m<n;m++)
				pow = pow * 100;

			value = value + (bcdToBin(buffer[offset+n]) * pow);
		}
		
		return value;
	}
	
	
	public static void formatSG12(long value,byte[] buffer,int offset)
	{
		binToBcd(value,buffer,offset);
	}
	
	public static double parseSG14(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<5)
			return 0;
		
		if (isInvalidData(buffer,offset,5,(byte)0xee) == true)
		{
			return -10;
		}
		
		double value = 0;
		for (int n=4;n>=0;n--)
		{
			long pow = 1;
			for (int m=0;m<n;m++)
				pow = pow * 100;

			value = value + (bcdToBin(buffer[offset+n]) * pow);
		}
		
		//小数点
		value = value/10000;
						
		return value;
	}
	
	public static String parseSG15(byte[] buffer,int offset,int len,int type)
	{
		return PtclModule.bcdToTime(buffer, offset, len, type,(byte)0xee);		
	}
	
	public static String parseSG17(byte[] buffer,int offset,int len,int type)
	{
		return PtclModule.bcdToTime(buffer, offset, len, type,(byte)0xee);		
	}
	
	public static String parseSG18(byte[] buffer,int offset,int len,int type)
	{
		return PtclModule.bcdToTime(buffer, offset, len, type,(byte)0xee);		
	}
	
	public static void formatSG18(String value,byte[] buffer,int offset)
	{
	    PtclModule.timeToBcd(value, buffer, offset,PtclModule.time_type_mmhhdd);	
	}	
	
	public static String parseSG19(byte[] buffer,int offset,int len,int type)
	{
		return PtclModule.bcdToTime(buffer, offset, len, type,(byte)0xee);		
	}
	
	public static void formatSG19(String value,byte[] buffer,int offset)
	{
	    PtclModule.timeToBcd(value, buffer, offset,PtclModule.time_type_mmhh);	
	}	
	
	public static double parseSG22(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<1)
			return 0;
		
		if (isInvalidData(buffer,offset,1,(byte)0xee) == true)
		{
			return -10;
		}
		
		double value = ((buffer[offset] & 0xF0) >> 4) + (buffer[offset] & 0x0F)*0.1;
		
 		return value;				
	}
	
	public static void formatSG22(Double value,byte[] buffer,int offset)
	{	
		byte iValue = (byte)(value * 10);
		binToBcd(iValue,buffer,offset);	
		return;
	}
	
	public static double parseSG23(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<3)
			return 0;
		
		if (isInvalidData(buffer,offset,3,(byte)0xee) == true)
		{
			return -10;
		}
		
		double value = 0;
		for (int n=2;n>=0;n--)
		{
			long pow = 1;
			for (int m=0;m<n;m++)
				pow = pow * 100;

			value = value + (bcdToBin(buffer[offset+n]) * pow);
		}
		
		//小数点
		value = value/10000;		
			
		return value;
				
	}
	
	public static void formatSG23(Double value,byte[] buffer,int offset)
	{	
		int iValue = (int)(value * 10000);
		binToBcd(iValue,buffer,offset);	
		return;
	}

	public static double parseSG25(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<3)
			return 0;
		
		if (isInvalidData(buffer,offset,3,(byte)0xee) == true)
		{
			return -10;
		}
		
		byte temp[] = new byte[3];
		System.arraycopy(buffer, offset, temp, 0, 3);
		byte s = (byte)(temp[2]>>7);
		temp[2] = (byte)(temp[2] & 0x7F);
		double value = PtclModule.bcdToBin(temp, 0, 3, 3, (byte)0xee);
		
		if(s == 1)
			value = 0 - value;
			
		return value;
		
		
	}
	
	public static void formatSG25(Double value,byte[] buffer,int offset)
	{
		int iValue = (int)(value * 1000);
		byte s = 0;
		
		if (value < 0)
		{
			s = 1;
			iValue = 0 - iValue;
			iValue = (iValue & 0x7fffff) | (s<<23);
		}
				
		binToBcd(iValue,buffer,offset);
	}
		

//将十进制字符串转换为二进制字符串，并取其中的某些位
	public static String intStringToSubBinString(String s,int beginIndex,int endIndex)
	{
		 BigInteger bigInt = new BigInteger(s);
		 String binString = bigInt.toString(2);
		 int len = binString.length();
		 for (int n=0;n<8-len;n++)
		 {
			 binString = "0" + binString;
		 }
		 return  binString.substring(beginIndex, endIndex);	
	}
	//取二进制字符串的某些位，转换为十进制数
	public static String SubBinStringToIntString(String binString,int beginIndex,int endIndex)
	{
		int len = binString.length();
		for (int n=0;n<8-len;n++)
		{
			binString = "0" + binString;
		}
		
		 String subS = binString.substring(beginIndex, endIndex);
		 BigInteger bigInt = new BigInteger(subS,2);
		 String intS = bigInt.toString();
		 return  intS;	
	}
	
	
	
	//解析首位是S符号位的数据
	public static double parseSData(byte[] buffer,int offset,int len,int countRadixPoint,byte invalid)
	{
		if (len > 5 || countRadixPoint > 10)
			return 0;

		if (isInvalidData(buffer,offset,len,invalid) == true)
			return -10;
        
		int s = (buffer[offset+len-1] & 0xf0)>>4;	
			
		double value = 0;
		value = bcdToBin(buffer,offset,len,invalid);
		value = value % (Math.pow(10,(len*2-1)));
		value = value / (Math.pow(10,countRadixPoint));
		
		if (s==1)
			value = 0 - value;			
		
		return value;
		
	}
	
	
	public static String getBit(byte[] buffer, int offset, int pos)
	{
		int index = (pos - 1) / 8;
		int bit = (pos - 1) % 8;
		int val = (buffer[offset + index] >>> bit) & 0x1;
		return "" + val;
	}
	
	public static int parseSG8(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<2)
			return 0;
		
		int value = PtclModule.bcdToBin(buffer, offset, 2, (byte)0xee);
		
		return value;
	}
	
	public static double parseSG10(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<3)
			return 0;
		
		double value = PtclModule.bcdToBin(buffer, offset, 3, 0, (byte)0xee);
		
		return value;
	}
	
	public static double parseSG27(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<4)
			return 0;
		
		double value = PtclModule.bcdToBin(buffer, offset, 4, 0, (byte)0xee);
		
		return value;
	}

	public static double parseSG13(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<4)
			return 0;
		
		double value = PtclModule.bcdToBin(buffer, offset, 4, 4, (byte)0xee);
		
		return value;
	}
	
	public static double parseSG6(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<2)
			return 0;
		
		if (isInvalidData(buffer,offset,2,(byte)0xee) == true)
		{
			return -10;
		}
		
		byte temp[] = new byte[2];
		System.arraycopy(buffer, offset, temp, 0, 2);
		byte s = (byte)((temp[1] & 0x80)>>7);
		temp[1] = (byte)(temp[1] & 0x7F);
		double value = PtclModule.bcdToBin(temp, 0, 2, 2, (byte)0xee);
		
		if(s == 1)
			value = 0 - value;
			
		return value;
	}
	
	public static void parseSGTdh(byte[] buffer, int offset, int[]values)
	{		
		if((buffer.length - offset)<2)
			return;
		
		if (isInvalidData(buffer,offset,2,(byte)0xee) == true)
		{
			return;
		}
		
		values[0] = (buffer[offset] & 0x0F) +  ((buffer[offset] & 0x30) >>> 4) * 10;
		
		ConcurrentHashMap<Integer, Integer> mapDensity = new ConcurrentHashMap<Integer, Integer>();
		mapDensity.put(1, 15);
		mapDensity.put(2, 30);
		mapDensity.put(3,  60);
		mapDensity.put(254, 5);
		mapDensity.put(255, 1);
		
		if (mapDensity.contains(Integer.parseInt(buffer[offset + 1] + "")))		
			values[1] = mapDensity.get(Integer.parseInt(buffer[offset + 1] + ""));
		else		
			values[1] = 0;		
	}
	
	public static void parseSG04Tdh(byte[] buffer, int offset, String[]values)
	{		
		if((buffer.length - offset)<1)
			return;
		
		if (isInvalidData(buffer,offset,1,(byte)0xee) == true)
		{
			return;
		}

		values[0] = String.valueOf((buffer[offset] & 0x0F) +  ((buffer[offset] & 0x30) >>> 4) * 10);
		
		ConcurrentHashMap<Integer, String> mapDensity = new ConcurrentHashMap<Integer, String>();
		mapDensity.put(0, "不冻结");
		mapDensity.put(1, "15");
		mapDensity.put(2, "30");
		mapDensity.put(3,  "60");

		if (mapDensity.get((buffer[offset] & 0xc0) >>> 6) != null)		
			values[1] = mapDensity.get((buffer[offset] & 0xc0) >>> 6);
		else		
			values[1] = "冻结 密度值不存在";		
	}
	
	public static void formatSG2(double value, byte[] buffer, int offset)
	{
		int s = 0;
		if (value < 0)
		{
			s = 1;
			value = 0 - value;
		}

		String sTemp = String.format("%1$.2e", value);
		String sPart1 = sTemp.substring(0, sTemp.indexOf("e"));
		String sSign = sTemp.substring(sTemp.indexOf("e") + 1, sTemp.indexOf("e") + 2);
		String sPart2 = sTemp.substring(sTemp.indexOf("e") + 2);

		Float fVal = Float.valueOf(sPart1) * 100;
		int nVal1 = fVal.intValue();

		int g = 0;
		int nVal2 = Integer.valueOf(sPart2);
		if (sSign.equalsIgnoreCase("-"))
			nVal2 = -1 * nVal2;
		nVal2 = nVal2 - 2;
		switch (nVal2)
		{
		case 4:
			g = 0;
			break;
		case 3:
			g = 1;
			break;
		case 2:
			g = 2;
			break;
		case 1:
			g = 3;
			break;
		case 0:
			g = 4;
			break;
		case -1:
			g = 5;
			break;
		case -2:
			g = 6;
			break;
		case -3:
			g = 7;
			break;			
		}
		
		int a = nVal1 / 100;
		nVal1 = nVal1 % 100;
		int b = nVal1 / 10;
		int c = nVal1 % 10;
		
		buffer[offset] = (byte)((b << 4) | c);
		buffer[offset + 1] = (byte)((g << 5) | (s << 4) | a);
	}
	
	public static void formatSG6(double value,byte[] buffer,int offset)
	{
		int iValue = (int)(value * 100);
		byte s = 0;
		
		if (value < 0)
		{
			s = 1;
			iValue = 0 - iValue;
			iValue = (iValue & 0x7fff) | (s<<15);
		}
				
		binToBcd(iValue,buffer,offset);
	}
	
	public static double parseSG26(byte[] buffer,int offset)
	{
		if((buffer.length - offset)<2)
			return 0;
		
		double value = PtclModule.bcdToBin(buffer, offset, 2, 3, (byte)0xee);

		return value;
	}
	
	public static void formatSG26(double value,byte[] buffer,int offset)
	{
		int iValue = (int)(value * 1000);
		
		binToBcd(iValue,buffer,offset);
	}
	
	/**  
	 * 字节转换为浮点  
	 *   
	 * @param b 字节（至少4个字节）  
	 * @param index 开始位置  
	 * @return  
	 */  
	public static float byteToFloat(byte[] b, int index) {     
	    int l;                                              
	    l = b[index + 0];                                   
	    l &= 0xff;                                          
	    l |= ((long) b[index + 1] << 8);                    
	    l &= 0xffff;                                        
	    l |= ((long) b[index + 2] << 16);                   
	    l &= 0xffffff;                                      
	    l |= ((long) b[index + 3] << 24);                   
	    return Float.intBitsToFloat(l);                     
	} 
}
