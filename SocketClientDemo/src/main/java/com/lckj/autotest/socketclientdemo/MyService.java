package com.lckj.autotest.socketclientdemo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ouyangxiangqing on 2017/8/17.
 */

public class MyService extends Service{
    private Socket s;
    OutputStream os;
    private String serverIP = "";
    private static final String TAG = "com.lckj.autotest";
    BluetoothAdapter mAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverIP = intent.getStringExtra("ip");
        Log.e(TAG,"serverIP="+serverIP);
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
        return super.onStartCommand(intent, flags, startId);
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

        public ClientThread(Socket s)
                throws IOException
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
                while ((content = br.readLine()) != null)
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
}
