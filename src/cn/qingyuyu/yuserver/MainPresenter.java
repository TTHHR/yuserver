package cn.qingyuyu.yuserver;

import cn.qingyuyu.yuserver.util.Log;
import cn.qingyuyu.yuserver.util.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MainPresenter {
    public static String recAndBackMsg(String msg)
    {
        JSONStringer sendDataJson = new JSONStringer();
        try {
            JSONObject recJson;
            sendDataJson.object();
            try {
                recJson = new JSONObject(msg);
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
                    if (recJson.getString("need").equals("set")) {
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
                            sendDataJson.value(System.currentTimeMillis() / 1000L);
                        }
                    } else if (recJson.getString("need").equals("get")) {
                        sendDataJson.key("code");
                        sendDataJson.value("ok");
                        sendDataJson.key("time");
                        sendDataJson.value(System.currentTimeMillis() / 1000L);
                        sendDataJson.key("data");
                        sendDataJson.value(u.getDataFromBase());
                    } else {
                        sendDataJson.key("code");
                        sendDataJson.value("fail");
                        sendDataJson.key("msg");
                        sendDataJson.value("error in need");
                    }

                } else// user not register in our database
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
                Log.getInstance().e("json", e.toString());
            }
            sendDataJson.endObject();
        }catch (Exception e)
        {
            Log.getInstance().e("recandback",e.toString());
        }
        return sendDataJson.toString();
    }
}
