package com.smsc.smartmeter.channel;

public class MessageBlock
{
	public final static int MAX_BUFFER_LENGTH = 1536;
	protected byte []base = new byte[MAX_BUFFER_LENGTH];
	protected int posWrite;
	
	public MessageBlock()
	{
		posWrite = 0;
	}
	
	
	
	public MessageBlock(byte []buffer, int length)
	{
		copy(buffer,length);
	}
	
	public int copy(byte []buffer, int length)
	{
		reset();
		return write(buffer,length);
	}
	
	public int size()
	{
		return MAX_BUFFER_LENGTH;
	}

	public int length()
	{
		return posWrite;
	}
	
	public byte []base()
	{
		return base;
	}
	
	public int write(byte []buffer, int length)
	{
		int len = space();

		if (len >= length)
		{
			System.arraycopy(buffer,0,base,posWrite,length);
			posWrite = posWrite + length;
			return 0;
		}
		else
			return -1;
	}
	

	public void reset()
	{
		posWrite = 0;
	}
	
	int space()
	{
		return MAX_BUFFER_LENGTH - posWrite;
	}
	
	@Override
	public String toString()
	{
		String textPacket = new String();
		for (int n=0;n<posWrite;n++)
		{
			
			textPacket = textPacket + String.format("%1$02x", base[n]).toUpperCase() + " ";
		}
		
		return textPacket;
	}

}
