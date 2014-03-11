package com.smsc.smartmeter.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Message;


public class ChannelHandler
{	
	protected final static int channelHandlerStateInit         =   0;
	protected final static int channelHandlerStateConnected    =   1;
	protected final static int channelHandlerStateOnline       =   2;
	protected final static int channelHandlerStateOffline      =   3;
	
	protected int state;
	protected String ip;
	protected short port;
	
	Handler handler;
	
	protected LinkedBlockingQueue<MessageBlock> queueRecv = new LinkedBlockingQueue<MessageBlock>();
	
	protected static ChannelHandler instance = null;
	
	public static void setInstance(ChannelHandler rc)
	{
		instance = rc;
	}
	
	public static ChannelHandler getInstance()
	{
		return instance;
	}
	
	public ChannelHandler(Handler handler,String ip,short port)
	{
		state = channelHandlerStateInit;	
		this.handler = handler;
		this.ip = ip;
		this.port = port;
	}
	
	
	void bufferEvent(MessageBlock mb)
	{
		try
		{
			queueRecv.put(mb);						
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public MessageBlock getRead()
	{
		return queueRecv.poll();
		
	}
	
	public class ReceiveThread extends Thread
	{
		protected ChannelHandler handlerChannel;
		protected DataInputStream streamIutput;
		protected int exit;
		
		public ReceiveThread(ChannelHandler handler)
		{
			exit = 0;
			handlerChannel = handler;
			try
			{
				streamIutput = new DataInputStream(handler.getSocket().getInputStream());
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public void exit()
		{
			exit = 1;
		}
		
		@Override
		public void run()
		{
			MessageBlock mb = new MessageBlock();
			int lenRead = 0;
			
			while(exit == 0)
			{
				try {
					byte []buffer = new byte[MessageBlock.MAX_BUFFER_LENGTH];
					int lenTransferred = streamIutput.read(buffer,0,lenRead);
					if (lenTransferred <= 0)
					{
						exit();
						break;
					}
					mb.write(buffer, lenTransferred);
					if (lenTransferred < lenRead)
					{
						lenRead = lenRead - lenTransferred;
					}
					else {
						bufferEvent(mb);
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					exit();
					break;
				}
			}
			
			handlerChannel.exit(true);
		}
	}
	
/*	public class SendThread extends Thread
	{
		protected ChannelHandler handlerChannel;
		protected DataOutputStream streamOutput;
		protected int exit;
		public SendThread(ChannelHandler handler)
		{
			exit = 0;
			handlerChannel = handler;
			try
			{
				streamOutput = new DataOutputStream(handler.getSocket().getOutputStream());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public void exit()
		{
			exit = 1;
		}
		
		public void run()
		{
			while (exit == 0)
			{
				MessageBlock mb = null;
				while ((mb = handlerChannel.getSend()) != null)
				{
					try
					{
						//logger.debug(String.format("send(%1$d:%2$dbytes) --->%3$s",handlerRouter.getID(),mb.length(),mb.toString()));
						streamOutput.write(mb.base(),0,mb.length());
					} 
					catch (IOException e)
					{
						e.printStackTrace();
						exit();
						break;
					}
				}
				
				try
				{
					Thread.sleep(1);
				} 
				catch (InterruptedException e)
				{
					//e.printStackTrace();
				}
			}
			
			//logger.debug(String.format("send failed(%1$d)", handlerRouter.getID()));
			handlerChannel.exit(true);
		}
	}*/
	
	public class ConnectTimerTask extends TimerTask
	{
		protected ChannelHandler handlerRouter;
		
		public ConnectTimerTask(ChannelHandler handler)
		{
			handlerRouter = handler;
		}
		
		@Override
		public void run()
		{
			stopConnectTimer();
			handlerRouter.startConnect();
		}
	}
	

	protected Socket sockClient;
	protected ReceiveThread threadRecv;
	protected DataOutputStream streamOutput;
	//protected SendThread threadSend;
	//protected LinkedBlockingQueue<MessageBlock> queueSend = new LinkedBlockingQueue<MessageBlock>();
	protected int remove;
	protected Timer timerConnect;
	protected Timer timerLogin;
	protected Timer timerHeartbeat;
	

	
	public int put(MessageBlock mb) throws IOException
	{
		if (state >= channelHandlerStateConnected)
		{
			try
			{

				streamOutput = new DataOutputStream(sockClient.getOutputStream());
				
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return 0;
	}
	
	/*public MessageBlock getSend()
	{
		return queueSend.poll();
		
	}*/
	
	
	public Socket getSocket()
	{
		return sockClient;
	}
	

	public void start(int delaySeconds)
	{
		startConnectTimer(delaySeconds);
	}
	
	public synchronized void exit(boolean reConnect)
	{
		if (remove == 0)
		{
			remove = 1;
			try
			{
				if (sockClient !=null && sockClient.isConnected())
					sockClient.close();
			} 
			catch (IOException e)
			{
				//e.printStackTrace();
			}


			state = channelHandlerStateOffline;
			stopThread();
			stopConnectTimer();
			if (reConnect == true)
				reConnect();
		}
	}
	
	protected void startConnectTimer(int delaySeconds)
	{
		timerConnect = new Timer("connect timer");
		ConnectTimerTask task = new ConnectTimerTask(this);
		timerConnect.schedule(task, delaySeconds*1000); 
	}
	
	protected void stopConnectTimer()
	{
		if (timerConnect != null)
			timerConnect.cancel();
	}
	

	protected void startThread()
	{
		threadRecv = new ReceiveThread(this);

		threadRecv.start();
	}
	
	protected void stopThread()
	{
		if (threadRecv != null)
			threadRecv.exit();

	}
	
	
	protected void startConnect()
	{
		try
		{
			sockClient = new Socket(ip,port);
			
			onConnect();
			
			Message msg = new Message();
			msg.what = 0x123;
			msg.obj = "con success";
			handler.sendMessage(msg);
			return ;
		}
		catch (ConnectException e)
		{
			//e.printStackTrace();
		}
		catch (IOException e)
		{
			//e.printStackTrace();
		}


		//logger.debug(String.format("connected failed (%1$d)",id));
		exit(true);
	}
	
	protected void onConnect()
	{
		state = channelHandlerStateConnected;

		startThread();
			
	}
	
	protected void reConnect() {
		start(10);
	}

}
