package com.smsc.smartmeter.ptcl;

public class PtclPacket
{
	public final static int MAX_PACKET_LENGTH = 1536;
	protected byte[] bufPacket = new byte[MAX_PACKET_LENGTH];
	protected int lenPacket;
	protected int lenDataArea;
	protected int lenHeader;
	
	public PtclPacket()
	{
		reset();
	}
	
	public void reset()
	{
		lenPacket = 0;
		lenDataArea = 0;
		lenHeader = 0;
	}
	
	public void formatPacket()
	{
		
	}
	
	//解析报文 必须先setBufPacket
	//返回�?>0:解析报文成功 0:报文长度不够 <0:报文格式不对
	public int parsePacket()
	{
		return  0;
	}
	
	//解析报文 必须先setBufPacket
	//返回�?>0:解析报文成功 0:报文长度不够 <0:报文格式不对
	public int parseHeader()
	{
		return  0;
	}
	
	//返回�?>0:解析报文成功 0:报文长度不够 <0:报文格式不对
	public int parseHeader(byte[] buffer,int length)
	{
		return  0;
	}

	//返回终端逻辑地址 按照报文顺序
	public int getDeviceAddr()
	{
		return 0;
	}

	//返回报文序列�?
	public int getFrameSeq()
	{
		return 0;
	}

	//返回主站ID
	public byte getHostID()
	{
		return 0;
	}
	
	//得到报文buffer
	public byte[] getBufPacket()
	{
		return bufPacket;
	}
	
	//得到报文长度
	public int getLenPacket()
	{
		return lenPacket;
	}

	//设置报文buffer
	public void setBufPacket(byte[] buffer,int length)
	{
		if (length < 0 || length > MAX_PACKET_LENGTH)
			return ;
		
		System.arraycopy(buffer,0,bufPacket,0,length);
		lenPacket = length;
	}
	
	public void setBufPacket(byte[] buffer,int offset,int length)
	{
		if (length < 0 || length > MAX_PACKET_LENGTH)
			return ;
		
		System.arraycopy(buffer,offset,bufPacket,0,length);
		lenPacket = length;
	}


	//得到用户域数据长�?
	public int getLenDataArea()
	{
		return lenDataArea;
	}
	
	//得到报文buffer
	public byte[] getBufDataArea()
	{
		byte[] buffer = new byte[MAX_PACKET_LENGTH];
		System.arraycopy(bufPacket,lenHeader,buffer,0,lenDataArea);
		return buffer;
	}
	
	//设置用户域数据buffer
	public void setBufDataArea(byte[] buffer,int length)
	{
		if (length < 0 || length > (MAX_PACKET_LENGTH-lenHeader))
			return ;
		
		System.arraycopy(buffer,0,bufPacket,lenHeader,length);
		lenDataArea = length;
	}
	
	//设置用户域数据buffer
	public void setBufDataArea(byte[] buffer,int offset,int length)
	{
		if (length < 0 || length > (MAX_PACKET_LENGTH-lenHeader))
			return ;
		
		System.arraycopy(buffer,offset,bufPacket,lenHeader,length);
		lenDataArea = length;
	}

	//返回报文头长�?
	public int getLenHeader()
	{
		return lenHeader;
	}
	
	protected byte getCheckSum(byte[] buffer,int offset,int length)
	{
		int sum = 0;
		for(int n=0;n<length;n++)
			sum = sum + buffer[offset+n];

		return (byte)sum;
	}
	
	@Override
	public String toString()
	{
		String textPacket = new String();
		for (int n=0;n<lenPacket;n++)
		{
			textPacket = textPacket + String.format("%1$02x", bufPacket[n]).toUpperCase() + " ";
		}
		
		return textPacket;
	}
}
