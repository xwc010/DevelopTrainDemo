package com.autotest.lckj.bluetoothdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ouyangxiangqing on 2017/8/15.
 */

public class MyService extends Service {
    // 定义保存所有Socket的ArrayList
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();
    public static ArrayList<String> ipList = new ArrayList<String>();
    public static boolean isRun = true;
    public static String  ip = "127.0.0.1";
    public static boolean isOpenBt = false;
    private final String OPEN_BT = "com.lckj.autotest.sendmsg";
    private static final String ACTION = "com.lckj.autotest.connectmsg";
    ServerSocket ss;
    private boolean flag = true;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        //启动连接服务器线程
        new Thread(new connectThread()).start();

        //注册广播
        IntentFilter intentFilter = new IntentFilter(OPEN_BT);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ServerThread implements Runnable{

        @Override
        public void run() {

            while(true && flag){
                if (isRun){
                    try{
                        for (Socket s : socketList) {
                            //发送消息给客户端
                            OutputStream os = s.getOutputStream();
                            os.write(("ip:"+ip +"isOpenBt:"+isOpenBt+"\n").getBytes("utf-8"));
                            Log.e("com.lckj.autotest","发送消息给客户端:"+s.getInetAddress().getHostAddress()+"需要操作的机器:"+ip+"是否开关蓝牙:"+isOpenBt);
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    isRun = false;
                }

            }
        }
    }

    class connectThread implements Runnable{

        @Override
        public void run() {
            try {
                ss = new ServerSocket(34567);
                boolean isFirst = true;
                while (true && flag) {
                    // 此行代码会阻塞,将一直等待别人的连接
                    Socket s = ss.accept();
                    removeSameClient(s);
                    socketList.add(s);
                    ipList.add(s.getInetAddress().getHostAddress());

                    if (isFirst){
                        // 客户端连接后启动一条ServerThread线程为所有客户服务
                        new Thread(new ServerThread()).start();
                        Log.e("com.lckj.autotest","服务已启动");
                    }
                    isFirst = false;

                    //发送广播，通知已有设备连上服务器
                    Intent it = new Intent(ACTION);
                    it.putStringArrayListExtra("ipList", ipList);
                    sendBroadcast(it);
                }
            }catch (IOException e){
                Log.e("com.lckj.autotest","启动服务失败"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    BroadcastReceiver mReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            //更新数据
            ip = intent.getStringExtra("ip");
            isOpenBt = intent.getBooleanExtra("isOpen", false);
            isRun = true;
            Log.e("com.lckj.autotest","接收到广播"+ip+isOpenBt+isRun);
        }
    };
    
    public void removeSameClient(Socket socket){
        String hostIP = socket.getInetAddress().getHostAddress();
        Log.e("com.lckj.autotest","hostIP="+hostIP);
        for (Socket s:socketList){
            if (s.getInetAddress().getHostAddress().equals(hostIP)){
                socketList.remove(s);
                ipList.remove(s);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(mReceiver);
    }
}
