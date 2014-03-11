package com.smsc.smartmeter.ptcl;



public class PtclModule_dlt {
	
	static public class DataUnit
	{
		public short dataItemId;
		public byte databuf[] = new byte[1024];
		public int lenDataBuf;
	}
	
	
	public final static byte AFN_SECURITY_CERTIFICATION = 0x03; //安全认证	
	public final static byte AFN_SET_TIMING			=	0x08;	//广播对时
	public final static byte AFN_READ_DATA			=	0x11;	//读数据
	public final static byte AFN_READ_NEXT_DATA		=	0x12;	//读后续数据
	public final static byte AFN_READ_DEVICE_ADDR	=	0x13;	//读通信地址
	public final static byte AFN_WRITE_DATA			=	0x14;	//写数据
	public final static byte AFN_WRITE_DEVICE_ADDR	=	0x15;	//写通信地址
	public final static byte AFN_FREEZE				=	0x16;	//冻结命令
	public final static byte AFN_UPDATE_COM_BPS		=	0x17;	//更改通信速率
	public final static byte AFN_UPDATE_PSW			=	0x18;	//修改密码
	public final static byte AFN_MAX_DEMAND_ZERO	=	0x19;	//最大需量清零
	public final static byte AFN_METER_ZERO			=	0x1A;	//电表清零
	public final static byte AFN_ALARM_ZERO			=	0x1B;	//事件清零
	public final static byte AFN_REMOTE_CONTROL     =   0x1C;   //跳闸、合闸允许、报警、报警解除、保电和保电解除。
	
	protected static PtclModule_dlt instance = null;

	public PtclModule_dlt()
	{
		
	}
	
	public static PtclModule_dlt getInstance()
	{
		if (instance == null)
			instance = new PtclModule_dlt();
		return instance;
	}
	
	public void fromatPacketCommand(PtclPacket_dlt packet,byte[] psw,byte[] operatorCode,byte operatorType,String cmdEffectiveTime)
	{
		if (packet == null)
			return;

		//控制域
		packet.setCtrlCode(AFN_REMOTE_CONTROL);

		//数据域
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		int lenDataBuf = 0;
		
		//密码
		System.arraycopy(psw, 0, dataBuf, lenDataBuf, 4);
		lenDataBuf = lenDataBuf + 4;
		
		//操作者代码 
		System.arraycopy(operatorCode, 0, dataBuf, lenDataBuf, 4);
	    lenDataBuf = lenDataBuf + 4;
	    
	    //操作类型
	    dataBuf[lenDataBuf++] = operatorType;
	    
	    //保留
	    lenDataBuf++;

		//命令有效截止时间,z1表为六个字节00
	    if(cmdEffectiveTime.compareToIgnoreCase("000000000000") != 0)
	    	PtclModule.timeToBcd(cmdEffectiveTime, dataBuf, lenDataBuf, PtclModule.time_type_ssmmhhddmmyy);
	    
	    lenDataBuf = lenDataBuf + 6;
	    
	    packet.setBufDataArea(dataBuf, lenDataBuf);
	    
	    packet.formatPacket();
	}
	
	public void fromatPacketCommandEx(PtclPacket_dlt packet,byte[] psw,byte[] operatorCode,byte[]pswDataBuffer,int dataLen)
	{
		if (packet == null)
			return;

		//控制域
		packet.setCtrlCode(AFN_REMOTE_CONTROL);

		//数据域
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		int lenDataBuf = 0;
		
		//密码
		System.arraycopy(psw, 0, dataBuf, lenDataBuf, 4);
		lenDataBuf = lenDataBuf + 4;
		
		//操作者代码 
		System.arraycopy(operatorCode, 0, dataBuf, lenDataBuf, 4);
	    lenDataBuf = lenDataBuf + 4;
	    
	    //加密的数据内容
	    System.arraycopy(pswDataBuffer, 0, dataBuf, lenDataBuf,dataLen);
	    lenDataBuf = lenDataBuf + dataLen;
	    
	    packet.setBufDataArea(dataBuf, lenDataBuf);
	    
	    packet.formatPacket();
	}
	
	public void fromatPacketSecurityCertification(PtclPacket_dlt packet,byte[] dataItemId,byte[]dataBuffer,int dataLen,byte[] operatorCode)
	{
		if (packet == null)
			return;

		//控制域
		packet.setCtrlCode(AFN_SECURITY_CERTIFICATION);

		//数据域
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		int lenDataBuf = 0;
		
		//数据标识
		System.arraycopy(dataItemId, 0, dataBuf, lenDataBuf, 4);
		lenDataBuf = lenDataBuf + 4;
		
		//操作者代码 
		System.arraycopy(operatorCode, 0, dataBuf, lenDataBuf, 4);
	    lenDataBuf = lenDataBuf + 4;
	    
	    //数据内容
	    System.arraycopy(dataBuffer, 0, dataBuf, lenDataBuf,dataLen);
	    lenDataBuf = lenDataBuf + dataLen;
	    
	    packet.setBufDataArea(dataBuf, lenDataBuf);
	    
	    packet.formatPacket();
	}
	
	public void fromatPacketSetTime(PtclPacket_dlt packet,String time)
	{
		if (packet == null)
			return;

		//控制域
		packet.setCtrlCode(AFN_SET_TIMING);

		//数据域
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		int lenDataBuf = 0;
		
		PtclModule.timeToBcd(time, dataBuf, lenDataBuf, PtclModule.time_type_ssmmhhddmmyy);
		lenDataBuf = lenDataBuf + 6;
		
	    packet.setBufDataArea(dataBuf, lenDataBuf);
	    
	    packet.formatPacket();
	}
	


	public void formatPacketReadDataRealTime(PtclPacket_dlt packet, int dataItemId)
	{
		if (packet == null)
			return;
		
		//控制欲
		packet.setCtrlCode(AFN_READ_DATA);
		
		//数据域
		int lenDataBuf = 0;
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		
		//System.arraycopy(dataItemId, 0, dataBuf, lenDataBuf, 4);
		PtclModule.intToBytes(dataItemId, dataBuf, lenDataBuf);
		lenDataBuf = lenDataBuf + 4;
		
		packet.setBufDataArea(dataBuf, lenDataBuf);
		
		packet.formatPacket();
	}
	
	
}
