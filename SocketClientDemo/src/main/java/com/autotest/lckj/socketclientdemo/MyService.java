package com.autotest.lckj.socketclientdemo;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ouyangxiangqing on 2017/8/17.
 */

public class MyService extends Service{
    private Socket s;
    OutputStream os;
    private String serverIP = "";
    private static final String TAG = "com.lckj.autotest";
    BluetoothAdapter mAdapter;
    private boolean flag = true;
    private Timer timer;
    private TimerTask task;
    private int reConnectNum = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 1000, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverIP = intent.getStringExtra("ip");
        Log.e(TAG,"serverIP="+serverIP);

        //将serverIP保存到本地
        SharedPreferences sharedPreferences = this.getSharedPreferences("test", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("serverIP",serverIP);
        editor.commit();

        connectServer(serverIP);
        return super.onStartCommand(intent, flags, startId);
    }

    public void connectServer(String serverIP){
        try
        {
            s = new Socket(serverIP, 34567);
            Log.e(TAG,"已连接服务器");
            Intent i = new Intent("com.lckj.autotest.USER_ACTION");
            sendBroadcast(i);
            // 客户端启动ClientThread线程不断读取来自服务器的数据
            new Thread(new ClientThread(s)).start(); // ①
            os = s.getOutputStream();
        }
        catch (Exception e)
        {
            Log.e(TAG,"连接失败" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class ClientThread implements Runnable
    {
        //该线程负责处理的Socket
        private Socket s;
        //该线程所处理的Socket所对应的输入流
        BufferedReader br = null;

        public ClientThread(Socket s) throws IOException
        {
            this.s = s;
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }

        public void run()
        {
            try
            {
                String content = null;
                //不断读取Socket输入流中的内容。
                while ((content = br.readLine()) != null && flag)
                {
                    Log.e(TAG, "接收到服务器消息:"+content);
                    String ip = new Utils().getIp(MyService.this);
                    // 每当读到来自服务器的消息之后，对机器进行操作
                    if (content.contains(ip)){
                        Log.e(TAG, "请对本机进行操作:"+ip);
                        if(content.contains("isOpenBt:true")){
                            mAdapter.enable();
                            Log.e(TAG,"打开蓝牙");
                        }else if(content.contains("isOpenBt:false")){
                            Log.e(TAG,"关闭蓝牙");
                            mAdapter.disable();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        try {
            s.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                Log.e(TAG,"检测与服务器的连接状态");
                s.sendUrgentData(0xFF);
            } catch (IOException e) {
                e.printStackTrace();
                if(reConnectNum<10){
                    Log.e(TAG,"已与服务器断开连接，正在重连");
                    connectServer(serverIP);
                    reConnectNum += 1;
                }else {
                    Log.e(TAG,"重连已超过10次，不再进行重连");
                    SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("isConnect","连接服务器");
                    editor.commit();
                }

            }
        }
    };
}
