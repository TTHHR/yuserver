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
            byte[] tmp=new byte[500];
            byte[] recData = new byte[500];

            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            int i=is.read(tmp);
            System.arraycopy(tmp,0,recData,0,i);
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
