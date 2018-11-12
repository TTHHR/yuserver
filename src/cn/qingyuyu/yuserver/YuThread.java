package cn.qingyuyu.yuserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.Callable;

import cn.qingyuyu.yuserver.util.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

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
			JSONObject recJson;
			JSONStringer sendDataJson = new JSONStringer();
			sendDataJson.object();
			InputStream is = s.getInputStream();
			OutputStream os = s.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((tmp = br.readLine()) != null) {
				tmp += "\n";// add a \n to keep original data
				recData += tmp;
				if (Thread.currentThread().isInterrupted())// thread timeout
				{

					sendDataJson.key("code");
					sendDataJson.value("fail");
					sendDataJson.key("msg");
					sendDataJson.value("time out");
					sendDataJson.endObject();
					os.write((sendDataJson.toString() + "\n").getBytes("UTF-8"));
					os.flush();
					s.shutdownOutput();
					s.close();
					return false;
				}
			}
			try {
				recJson = new JSONObject(recData);
				String token = recJson.getString("token");
				if (token == null)// json format error
				{
					sendDataJson.key("code");
					sendDataJson.value("fail");
					sendDataJson.key("msg");
					sendDataJson.value("format error");
				}
				User u = new User(token);
				if (u.checkUser())// user in our database
				{
					if (recJson.getString("need").equals("set")) 
					{
						if (!u.insertIntoDatabase(recJson.getString("data")))// have some error when try to store up this data								
						{
							sendDataJson.key("code");
							sendDataJson.value("fail");
							sendDataJson.key("msg");
							sendDataJson.value("error in data store up");
						} else// every thing ok,maybe
						{
							sendDataJson.key("code");
							sendDataJson.value("ok");
							sendDataJson.key("time");
							sendDataJson.value(System.currentTimeMillis()/1000L);
						}
					}
					else if (recJson.getString("need").equals("get")) {
						sendDataJson.key("code");
						sendDataJson.value("ok");
						sendDataJson.key("time");
						sendDataJson.value(System.currentTimeMillis()/1000L);
						sendDataJson.key("data");
						sendDataJson.value(u.getDataFromBase());
					}
					else
					{
						sendDataJson.key("code");
						sendDataJson.value("fail");
						sendDataJson.key("msg");
						sendDataJson.value("error in need");
					}

				}
				else// user not register in our database
				{
					sendDataJson.key("code");
					sendDataJson.value("fail");
					sendDataJson.key("msg");
					sendDataJson.value("user not exist");
				}

			} catch (JSONException e) {
				sendDataJson.key("code");
				sendDataJson.value("fail");
				sendDataJson.key("msg");
				sendDataJson.value("format error");

			}
			sendDataJson.endObject();

			os.write((sendDataJson.toString() + "\n").getBytes("UTF-8"));
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
