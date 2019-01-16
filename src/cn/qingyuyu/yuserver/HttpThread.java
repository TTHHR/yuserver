package cn.qingyuyu.yuserver;

import cn.qingyuyu.yuserver.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class HttpThread implements Callable<Boolean> {
    Socket s;

    HttpThread(Socket s) {
        this.s = s;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            String recData = "";

            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            recData=br.readLine();
            if (Thread.currentThread().isInterrupted())// thread timeout
                {
                    System.out.println("超时");
                    os.write(("{\"code\":\"file\",\"msg\":\"time out\"}" + "\n").getBytes("utf-8"));
                    os.flush();
                    s.shutdownOutput();
                    s.close();
                    return false;
                }
            s.shutdownInput();
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
