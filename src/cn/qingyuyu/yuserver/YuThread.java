package cn.qingyuyu.yuserver;
import java.net.*;

public class YuThread implements Runnable
{
	Socket s;
YuThread(Socket s)
{
	this.s=s;
}
	@Override
	public void run()
	{
		// TODO: Implement this method
		System.out.println("son thread"+s.getRemoteSocketAddress());
	}
	
	
}
