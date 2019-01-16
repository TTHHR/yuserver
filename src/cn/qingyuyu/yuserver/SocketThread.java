package cn.qingyuyu.yuserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.Callable;

import cn.qingyuyu.yuserver.util.Log;

public class SocketThread implements Callable<Boolean> {
	Socket s;

	SocketThread(Socket s) {
		this.s = s;
	}

	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		try {
			String recData = "", tmp = null;

			InputStream is = s.getInputStream();
			OutputStream os = s.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			recData=br.readLine();
				if (Thread.currentThread().isInterrupted())// thread timeout
				{

					os.write(("{\"code\":\"file\",\"msg\":\"time out\"}" + "\n").getBytes("utf-8"));
					os.flush();
					s.shutdownOutput();
					s.close();
					return false;
				}
			byte[] bmsg=MainPresenter.recAndBackMsg(recData);
			os.write( bmsg);
			os.write('\n');
			os.flush();
			s.shutdownOutput();
		} catch (Exception e) {
			Log.getInstance().e("runTime", e.toString());
			return false;
		} finally {
			s.close();
		}
		return true;
	}
}
