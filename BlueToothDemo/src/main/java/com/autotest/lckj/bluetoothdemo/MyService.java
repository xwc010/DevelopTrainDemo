package com.autotest.lckj.bluetoothdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ouyangxiangqing on 2017/8/15.
 */

public class MyService extends Service {
    // 定义保存所有Socket的ArrayList
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();
    public static boolean isRun = true;
    public static String  ip = "127.0.0.1";
    public static boolean isOpenBt = false;
    private final String OPEN_BT = "com.lckj.autotest.sendmsg";

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        new AsyncTaskThread().execute();

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

            while(true){
                if (isRun){
                    Log.e("com.lckj.autotest","发送消息给客户端");
                    try{
                        for (Socket s : socketList) {
                            //发送消息给客户端
                            OutputStream os = s.getOutputStream();
                            os.write(("ip:"+ip +"isOpenBt:"+isOpenBt+"\n").getBytes("utf-8"));
                            Log.e("com.lckj.autotest","发送消息给客户端"+ip+isOpenBt);
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    isRun = false;
                }

            }
        }
    }

    class AsyncTaskThread extends AsyncTask{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                ServerSocket ss = new ServerSocket(34567);
                while (true) {
                    // 此行代码会阻塞,将一直等待别人的连接
                    Socket s = ss.accept();
                    socketList.add(s);
                    // 每当客户端连接后启动一条ServerThread线程为该客户服务
                    new Thread(new ServerThread()).start();
                    Log.e("com.lckj.autotest","服务已启动");
                }
            }catch (IOException e){
                Log.e("com.lckj.autotest","启动服务失败"+e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    BroadcastReceiver mReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            Log.e("com.lckj.autotest","接收到广播");
            //更新数据
            MyService.ip = intent.getStringExtra("ip");
            MyService.isOpenBt = intent.getBooleanExtra("isOpen", false);
            MyService.isRun = true;
        }
    };
}
