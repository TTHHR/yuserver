package cn.qingyuyu.yuserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import cn.qingyuyu.yuserver.util.Log;

public class YuServer {
	public static void main(String[] str) {
		final int THREAD_MAX = 150;// max thread number,you can change it<255
		final int PORT = 2333;// listen port
		try {
			@SuppressWarnings("resource")
			ServerSocket svrSocket = new ServerSocket(PORT); // listen socket
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(THREAD_MAX); // Thread
																						// pool
			while (true) {
				Socket socket = svrSocket.accept(); // wait for request
				Thread manager = new Thread(new Runnable() {// new manager
															// thread
					FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new YuThread(socket));
					public void run() {
						try {
							
							fixedThreadPool.execute(futureTask);
							futureTask.get(5000, TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							futureTask.cancel(true);//cancel this timeout thread
							Log.getInstance().e("threadManager", e.toString());
						}
					}
				});
				manager.start();
				// luncher thread to deal the request
			}
		}

		catch (Exception e) {
			Log.getInstance().e("listenPort", e.toString());
		}

	}
}
