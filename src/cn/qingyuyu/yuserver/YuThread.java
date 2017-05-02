package cn.qingyuyu.yuserver;

import java.net.*;
import java.util.Random;
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
			Random random = new Random();
			int rum = random.nextInt(10);
			Thread.sleep(rum * 1000);
			Log.getInstance().d("socket", s.toString());
		} catch (Exception e) {
			Log.getInstance().e("runTime", e.toString());
			return false;
		}
		return true;
	}
}
