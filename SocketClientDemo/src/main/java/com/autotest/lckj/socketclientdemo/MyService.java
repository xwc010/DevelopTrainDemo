package com.autotest.lckj.socketclientdemo;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ouyangxiangqing on 2017/8/17.
 */

public class MyService extends Service {
    private Socket s;
    private String serverIP = "";
    BluetoothAdapter mAdapter;
    private boolean flag = true;
    private Timer timer;
    private TimerTask task;
    private boolean timerState = false;
    private int reConnectNum = 0;
    MediaPlayer mPlayer;
    private String recordFilePath;
    private MyMediaRecorder mr;

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        recordFilePath = sdPath + "/Autotest/AAC/";
        mr = new MyMediaRecorder(MyService.this);

        IntentFilter intentFilter = new IntentFilter(G.USER_ACTION);
        registerReceiver(mReceiver, intentFilter);

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverIP = intent.getStringExtra("ip");
        Log.e(G.TAG,"serverIP="+serverIP);

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
            Log.e(G.TAG,"已连接服务器");

            if (!timerState){
                //启动定时器，每隔一分钟检测下是否断开连接
                timer.schedule(task, 60*1000, 60*1000);
                timerState = true;
            }

            //发送广播通知机器已连上服务器
            Intent i = new Intent(G.USER_ACTION);
            sendBroadcast(i);
            // 客户端启动ClientThread线程不断读取来自服务器的数据
            new Thread(new ClientThread(s)).start(); // ①
        }
        catch (Exception e)
        {
            Log.e(G.TAG,"连接失败" + e.getMessage());
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
                    Log.e(G.TAG, "****接收到服务器消息:"+content);
                    if(content.contains("type:call")){
                        String phoneNumber = content.split("phoneNumber:")[1].trim();
                        //拨打电话
                        Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
                        phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyService.this.startActivity(phoneIntent);
                    }else if(content.contains("type:openBt")){
                        if(content.contains("isOpenBt:true")){
                            mAdapter.enable();
                            Log.e(G.TAG,"打开蓝牙");
                        }else if(content.contains("isOpenBt:false")){
                            Log.e(G.TAG,"关闭蓝牙");
                            mAdapter.disable();
                        }
                    }else if (content.contains("startvolume")){
                        Log.e(G.TAG,"开始记录当前音量");
                        mr.startRecord();
                    }else if (content.contains("stopvolume")){
                        Log.e(G.TAG,"停止记录当前音量");
                        mr.stopRecord();

                        SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
                        float average = sharedPreferences.getFloat("average",0);
                        float max = sharedPreferences.getFloat("max",0);
                        try {
                            OutputStream os = s.getOutputStream();
                            os.write(("max="+max+",average="+average+"\n").getBytes("utf-8"));
                            Log.e(G.TAG,"已发送消息给服务器:max="+max+",average="+average+"......");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(G.TAG,"发送消息失败");
                        }

                    } else if(content.contains("type:play")){
                        Log.e(G.TAG,"开始播放语音文件");
                        String filename = content.split("filename:")[1].trim();
                        if(mPlayer != null){
                            mPlayer.release();
                        }
                        playFile(filename);
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
        flag = false;
        if(s != null){
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(timer != null){
            timer.cancel();
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                Log.e(G.TAG,"检测与服务器的连接状态");
                s.sendUrgentData(0xFF);
            } catch (IOException e) {
                e.printStackTrace();
                if(reConnectNum<10){
                    Log.e(G.TAG,"已与服务器断开连接，正在重连");
                    connectServer(serverIP);
                    reConnectNum += 1;
                }else {
                    Log.e(G.TAG,"重连已超过10次，不再进行重连");
                    SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("isConnect","连接服务器");
                    editor.commit();
                    timer.cancel();
                    reConnectNum = 0;
                }

            }
        }
    };


    public void playFile(String FileName){
        mPlayer = new MediaPlayer();
        try{
            File file = new File(recordFilePath+FileName);
            FileInputStream fis = new FileInputStream(file);
            mPlayer.setDataSource(fis.getFD());
            mPlayer.prepare();
            mPlayer.start();
        }catch(IOException e){
            Log.e(G.TAG,"播放失败"+e.getMessage());
        }
    }

    BroadcastReceiver mReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(G.DISCONNECT)){
                Log.e(G.TAG,"收到断开连接通知");
                try {
                    if(s != null){
                        s.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
