package cn.qingyuyu.yuserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


public class YuServer {
public static void main(String [] str)
{
	final int THREAD_MAX=150;//max thread number,you can change it<255
	final int PORT=2333;//listen port
	Thread listener = new Thread(new Runnable() {// new listen thread
		public void run() {
			try {
				@SuppressWarnings("resource")
				ServerSocket svrSocket = new ServerSocket(PORT); // listen socket
				ExecutorService fixedThreadPool = Executors.newFixedThreadPool(THREAD_MAX);  
				while (true) {
					Socket socket = svrSocket.accept(); // wait for request
					fixedThreadPool.execute(new YuThread(socket));
					// 开启子线程处理请求
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	});

	listener.start();// 开启监听线程
}
}
