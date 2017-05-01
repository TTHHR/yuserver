package cn.qingyuyu.yulauncher;

import java.net.ServerSocket;
import java.net.Socket;


public class YuServer {
public static void main(String [] str)
{
	Thread listener = new Thread(new Runnable() {// 新建监听线程
		public void run() {
			try {
				@SuppressWarnings("resource")
				ServerSocket svrSocket = new ServerSocket(2333); // 监听端口
				while (true) {
					Socket socket = svrSocket.accept(); // 阻塞
					new ListenerThread(socket).start();// 开启子线程处理请求
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	});

	lintener.start();// 开启监听线程
}
}
