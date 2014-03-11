package com.smsc.smartmeter.ptcl;



public class PtclModule_dlt {
	
	static public class DataUnit
	{
		public short dataItemId;
		public byte databuf[] = new byte[1024];
		public int lenDataBuf;
	}
	
	
	public final static byte AFN_SECURITY_CERTIFICATION = 0x03; //��ȫ��֤	
	public final static byte AFN_SET_TIMING			=	0x08;	//�㲥��ʱ
	public final static byte AFN_READ_DATA			=	0x11;	//������
	public final static byte AFN_READ_NEXT_DATA		=	0x12;	//����������
	public final static byte AFN_READ_DEVICE_ADDR	=	0x13;	//��ͨ�ŵ�ַ
	public final static byte AFN_WRITE_DATA			=	0x14;	//д����
	public final static byte AFN_WRITE_DEVICE_ADDR	=	0x15;	//дͨ�ŵ�ַ
	public final static byte AFN_FREEZE				=	0x16;	//��������
	public final static byte AFN_UPDATE_COM_BPS		=	0x17;	//����ͨ������
	public final static byte AFN_UPDATE_PSW			=	0x18;	//�޸�����
	public final static byte AFN_MAX_DEMAND_ZERO	=	0x19;	//�����������
	public final static byte AFN_METER_ZERO			=	0x1A;	//�������
	public final static byte AFN_ALARM_ZERO			=	0x1B;	//�¼�����
	public final static byte AFN_REMOTE_CONTROL     =   0x1C;   //��բ����բ�����������������������ͱ�������
	
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

		//������
		packet.setCtrlCode(AFN_REMOTE_CONTROL);

		//������
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		int lenDataBuf = 0;
		
		//����
		System.arraycopy(psw, 0, dataBuf, lenDataBuf, 4);
		lenDataBuf = lenDataBuf + 4;
		
		//�����ߴ��� 
		System.arraycopy(operatorCode, 0, dataBuf, lenDataBuf, 4);
	    lenDataBuf = lenDataBuf + 4;
	    
	    //��������
	    dataBuf[lenDataBuf++] = operatorType;
	    
	    //����
	    lenDataBuf++;

		//������Ч��ֹʱ��,z1��Ϊ�����ֽ�00
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

		//������
		packet.setCtrlCode(AFN_REMOTE_CONTROL);

		//������
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		int lenDataBuf = 0;
		
		//����
		System.arraycopy(psw, 0, dataBuf, lenDataBuf, 4);
		lenDataBuf = lenDataBuf + 4;
		
		//�����ߴ��� 
		System.arraycopy(operatorCode, 0, dataBuf, lenDataBuf, 4);
	    lenDataBuf = lenDataBuf + 4;
	    
	    //���ܵ���������
	    System.arraycopy(pswDataBuffer, 0, dataBuf, lenDataBuf,dataLen);
	    lenDataBuf = lenDataBuf + dataLen;
	    
	    packet.setBufDataArea(dataBuf, lenDataBuf);
	    
	    packet.formatPacket();
	}
	
	public void fromatPacketSecurityCertification(PtclPacket_dlt packet,byte[] dataItemId,byte[]dataBuffer,int dataLen,byte[] operatorCode)
	{
		if (packet == null)
			return;

		//������
		packet.setCtrlCode(AFN_SECURITY_CERTIFICATION);

		//������
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		int lenDataBuf = 0;
		
		//���ݱ�ʶ
		System.arraycopy(dataItemId, 0, dataBuf, lenDataBuf, 4);
		lenDataBuf = lenDataBuf + 4;
		
		//�����ߴ��� 
		System.arraycopy(operatorCode, 0, dataBuf, lenDataBuf, 4);
	    lenDataBuf = lenDataBuf + 4;
	    
	    //��������
	    System.arraycopy(dataBuffer, 0, dataBuf, lenDataBuf,dataLen);
	    lenDataBuf = lenDataBuf + dataLen;
	    
	    packet.setBufDataArea(dataBuf, lenDataBuf);
	    
	    packet.formatPacket();
	}
	
	public void fromatPacketSetTime(PtclPacket_dlt packet,String time)
	{
		if (packet == null)
			return;

		//������
		packet.setCtrlCode(AFN_SET_TIMING);

		//������
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
		
		//������
		packet.setCtrlCode(AFN_READ_DATA);
		
		//������
		int lenDataBuf = 0;
		byte []dataBuf = new byte[PtclPacket.MAX_PACKET_LENGTH];
		
		//System.arraycopy(dataItemId, 0, dataBuf, lenDataBuf, 4);
		PtclModule.intToBytes(dataItemId, dataBuf, lenDataBuf);
		lenDataBuf = lenDataBuf + 4;
		
		packet.setBufDataArea(dataBuf, lenDataBuf);
		
		packet.formatPacket();
	}
	
	
}
