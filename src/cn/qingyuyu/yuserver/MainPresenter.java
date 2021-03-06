package cn.qingyuyu.yuserver;

import cn.qingyuyu.yuserver.util.HttpUtils;
import cn.qingyuyu.yuserver.util.Log;
import cn.qingyuyu.yuserver.util.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MainPresenter {
    public static byte[] recAndBackMsg(byte[] bmsg)
    {
        String msg=new String (bmsg);
        if(msg.startsWith("GET"))//处理http内容
        {
                int begin = msg.indexOf("/");
                int end = msg.indexOf("HTTP/");
                String condition=msg.substring(begin, end);
                Log.getInstance().i("GET",condition);
                String bakMsg="";
                if(condition.equals("/ "))
                {
                     bakMsg="HTTP/1.1 200 OK\n";
                    bakMsg+="type:text/html\n\n";
                    bakMsg+="<head>\n" +
                            "<meta charset=\"UTF-8\">\n" +
                            "<title>登录查看信息</title>\n" +
                            "</head>";
                    bakMsg+="<form action=\"/login.jsp\" method=\"get\">\n" +
                            "Token: <input type=\"password\" name=\"user_pass\" /><br />\n" +
                            "<input type=\"submit\" />\n" +
                            "</form>";

                }
                else if(condition.startsWith("/login.jsp"))
                {
                    bakMsg="HTTP/1.1 200 OK\n";
                    bakMsg+="type:text/html\n\n";
                    bakMsg+="<head>\n" +
                            "<meta charset=\"UTF-8\">\n" +
                            "<title>查看信息</title>\n" +
                            "</head>";
                     begin = msg.indexOf("s=")+2;
                     end = msg.lastIndexOf(" ");
                     if(end==-1)
                         end=msg.length();
                     String token=msg.substring(begin, end);
                    User u = new User(token);
                    if (u.checkUser())// user in our database
                    {
                        bakMsg+="<h1>"+u.getDataFromBase()+"</h1><br />";
                    }
                    bakMsg+="<h1>"+token+"</h1>";
                }
               else
                {
                    bakMsg="HTTP/1.1 404 NOT FOUND\n\n";
                }


                return bakMsg.getBytes() ;
        }



        JSONStringer sendDataJson = new JSONStringer();
        try {
            JSONObject recJson=null;
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
                        String data=recJson.getString("data");
                        if(recJson.has("encrypt"))
                        {
                            data=new String(bmsg,recJson.getString("encrypt"));
                            recJson=new JSONObject(data);
                            data=recJson.getString("data");
                        }
                        if (!u.insertIntoDatabase(data))// have some error when try to store up this data
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
                        System.out.println("数据库拿出来"+u.getDataFromBase());
                    } else if(recJson.getString("need").equals("ai"))
                    {
                        //与图灵机器人交流
                        try {
                            JSONObject param = new JSONObject();

                            param.put("reqType",0);

                            JSONObject inputText = new JSONObject();
                            String data=recJson.getString("data");
                            if(recJson.has("encrypt"))
                            {
                                data=new String(bmsg,recJson.getString("encrypt"));
                                recJson=new JSONObject(data);
                                data=recJson.getString("data");
                            }
                            inputText.put("text",data);

                            JSONObject perception = new JSONObject();
                            perception.put("inputText",inputText);

                            param.put("perception",perception);

                            JSONObject userInfo = new JSONObject();
                            userInfo.put("apiKey","feda");
                            userInfo.put("userId","2");

                            param.put("userInfo",userInfo);

                            String result=HttpUtils.doPost("http://openapi.tuling123.com/openapi/api/v2",param.toString());

                            JSONObject jsonObject =new JSONObject(result);
                            JSONArray ja=jsonObject.getJSONArray("results");
                            JSONObject value=ja.getJSONObject(0).getJSONObject("values");

                            sendDataJson.key("code");
                            sendDataJson.value("ok");
                            sendDataJson.key("time");
                            sendDataJson.value(System.currentTimeMillis() / 1000L);
                            sendDataJson.key("data");
                            sendDataJson.value(value.getString("text"));
                        }catch (Exception e)
                        {
                            Log.getInstance().e("ai",e.toString());
                        }
                    }
                    else {
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
            if(recJson!=null&&recJson.has("encrypt"))
            {
                return sendDataJson.toString().getBytes(recJson.getString("encrypt"));
            }
        }catch (Exception e)
        {
            Log.getInstance().e("recandback",e.toString());
        }

        return sendDataJson.toString().getBytes();
    }
}
