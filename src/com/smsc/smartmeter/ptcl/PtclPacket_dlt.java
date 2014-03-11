package com.smsc.smartmeter.ptcl;


public class PtclPacket_dlt extends PtclPacket
{
//    public final static int HEADER_LENGTH = 14;
//
//    protected final static int posFrameHead = 0;
//	protected final static int posFrameStart = 4;
//	protected final static int posDeviceAddr = 5;
//	protected final static int posDataAreaStart = 11;
//	protected final static int posCtrlCodeArea = 12;	
//	protected final static int posLenArea = 13;
	
	
	//无fe fe fe fe
	public final static int HEADER_LENGTH = 10;
    protected final static int posFrameStart = 0;
	protected final static int posDeviceAddr = 1;
	protected final static int posDataAreaStart = 7;
	protected final static int posCtrlCodeArea = 8;	
	protected final static int posLenArea = 9;
	
	public PtclPacket_dlt()
	{
		reset();
	}
	
	@Override
	public void reset()
	{
		lenPacket = 0;
		lenDataArea = 0;
		lenHeader = HEADER_LENGTH;
	}
	
	@Override
	public void formatPacket()
	{
		lenPacket = 0;
		
//		for(int i=0;i<4;i++)
//		{
//			bufPacket[posFrameHead+i] = (byte)0xFE;			
//		}
		
		bufPacket[posFrameStart] = 0x68;
		bufPacket[posDataAreaStart] = 0x68;
		bufPacket[posLenArea]  = (byte)lenDataArea;		
		lenPacket = lenHeader + lenDataArea;
		
		for(int i=0;i<lenDataArea;i++)
		{
			bufPacket[lenHeader+i] += 0x33;
		}
		
		
		//bufPacket[lenPacket++] = getCheckSum(bufPacket,4,lenHeader + lenDataArea-4);
		bufPacket[lenPacket++] = getCheckSum(bufPacket,0,lenHeader + lenDataArea);
		bufPacket[lenPacket++] = 0x16;	
	}
	
	//解析报文 必须先setBufPacket
	//返回�?>0:解析报文成功 0:报文长度不够 <0:报文格式不对
	@Override
	public int parsePacket()
	{
		int result = parseHeader();
		if (result <= 0)
			return result;
		
		byte crc = getCheckSum(bufPacket,0,lenHeader + lenDataArea);
		if (crc != bufPacket[lenHeader + lenDataArea])
			return -1;
		
		for(int i=0;i<lenDataArea;i++)
		{
			bufPacket[lenHeader+i] -= 0x33;
		}

		if (bufPacket[lenPacket-1] != 0x16)
			return -1;
		
		return lenPacket;
	}
	
	@Override
	public int parseHeader()
	{
		if (bufPacket[posFrameStart] != 0x68 || bufPacket[posDataAreaStart] != 0x68)
			return -1;
		
		lenDataArea = bufPacket[posLenArea];

		if (lenDataArea < 0 || lenDataArea > (MAX_PACKET_LENGTH - lenHeader - 2))
			return -1;
		
		lenPacket = lenHeader + lenDataArea + 2;

		return lenHeader;
	}
	
	//返回�?>0:解析报文成功 0:报文长度不够 <0:报文格式不对
	@Override
	public int parseHeader(byte[] buffer,int length)
	{
		if (length < lenHeader)
			return 0;

		System.arraycopy(bufPacket,0,bufPacket,0,length);
		return parseHeader();
	}
	
	public void getMeterAddr(byte[] meterAddr)
	{
		System.arraycopy(bufPacket, posDeviceAddr, meterAddr, 0, 6);	
	}
	
	
	public void setMeterAddr(byte[] meterAddr)
	{
		System.arraycopy(meterAddr,0 , bufPacket, posDeviceAddr, 6);	
		
	}
	
	public byte getCtrlCode()
	{
		return bufPacket[posCtrlCodeArea];
	}
	
	public void setCtrlCode(byte afn)
	{
		bufPacket[posCtrlCodeArea] = afn;
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
