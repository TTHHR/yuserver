package cn.qingyuyu.yuserver;

import cn.qingyuyu.yuserver.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpThread implements Runnable {
     private int UDPPORT = 1234;// listen port
    public UdpThread(int UDPPORT)
    {
        this.UDPPORT=UDPPORT;
    }
    @Override
    public void run() {
        DatagramSocket datagramSocket;
        DatagramPacket datagramPacket;

        try {
            /******* 接收数据流程**/
            byte[] receMsgs=new byte[1024];
            // 创建一个数据报套接字，并将其绑定到指定port上
            datagramSocket = new DatagramSocket(UDPPORT);
            // DatagramPacket(byte buf[], int length),建立一个字节数组来接收UDP包
            datagramPacket = new DatagramPacket(receMsgs, receMsgs.length);
            // receive()来等待接收UDP数据报
            while(true) {//udp不能简单的多线程，只能一个个的处理了
                datagramSocket.receive(datagramPacket);
                /****** 解析数据报****/
                String receStr = new String(datagramPacket.getData(), 0 , datagramPacket.getLength());


                byte[] bmsg;
                if(!receStr.equals(""))
                 bmsg=MainPresenter.recAndBackMsg(receStr);
                else
                    bmsg="{\"code\":\"file\",\"msg\":\"time out\"}".getBytes();
                /***** 返回ACK消息数据报*/
                // 组装数据报
                byte[] buf = (bmsg+"\n").getBytes();
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, datagramPacket.getAddress(), datagramPacket.getPort());
                // 发送消息
                datagramSocket.send(sendPacket);
            }
        }
        catch (Exception e)
        {
            Log.getInstance().e("udp",e.toString());
        }
    }
}
