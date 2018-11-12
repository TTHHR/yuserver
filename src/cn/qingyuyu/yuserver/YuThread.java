package cn.qingyuyu.yuserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.Callable;

import cn.qingyuyu.yuserver.util.Log;

public class YuThread implements Callable<Boolean> {
	Socket s;

	YuThread(Socket s) {
		this.s = s;
	}

	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		try {
			String recData = "", tmp = null;

			InputStream is = s.getInputStream();
			OutputStream os = s.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "ASCII"));
			while ((tmp = br.readLine()) != null) {
				recData += tmp;
				if (Thread.currentThread().isInterrupted())// thread timeout
				{

					os.write(("{\"code\":\"file\",\"msg\":\"time out\"}" + "\n").getBytes("ASCII"));
					os.flush();
					s.shutdownOutput();
					s.close();
					return false;
				}
			}
			String bmsg=MainPresenter.recAndBackMsg(recData);

			os.write(( bmsg+ "\n").getBytes("ASCII"));
			os.flush();
			s.shutdownOutput();
			Log.getInstance().d("recData", recData);
		} catch (Exception e) {
			Log.getInstance().e("runTime", e.toString());
			return false;
		} finally {
			s.close();
		}
		return true;
	}
}
