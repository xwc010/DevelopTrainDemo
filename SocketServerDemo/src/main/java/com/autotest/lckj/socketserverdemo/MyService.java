package com.autotest.lckj.socketserverdemo;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public static String phoneNumber = "10010";
    public static String filename = "";
    private ServerSocket ss;
    private boolean flag = true;
    private static String type = "call";
    private Utils util = new Utils();
    private String voiceIP;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        //启动连接服务器线程
        new Thread(new connectThread()).start();

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(G.OPEN_BT);
        intentFilter.addAction(G.CALL);
        intentFilter.addAction(G.VOICE);
        intentFilter.addAction(G.SELECT_VOICE);
        intentFilter.addAction(G.PLAY_VOICE_INTENT);
        intentFilter.addAction(G.START_INTENT);
        intentFilter.addAction(G.STOP_INTENT);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class SendThread implements Runnable{

        @Override
        public void run() {
            while(true && flag){
                if (isRun){
                    for (Socket s : socketList) {
                        //发送消息给客户端
                        try {
                            OutputStream os = s.getOutputStream();

                            //发消息给需要操作的机器
                            if(s.getInetAddress().getHostAddress().equals(ip)){
                                if(type.equals("call")){
                                    os.write(("type:"+type+"ip:"+ip +"phoneNumber:"+phoneNumber+"\n").getBytes("utf-8"));
                                }else if(type.equals("openBt")){
                                    os.write(("type:"+type+"ip:"+ip +"isOpenBt:"+isOpenBt+"\n").getBytes("utf-8"));
                                }
                                Log.e(G.TAG,"发送消息给客户端:"+ip);
                            }
                            if (s.getInetAddress().getHostAddress().equals(voiceIP)){
                                if(type.equals("play")){
                                    os.write(("type:"+type+"filename:"+filename+"\n").getBytes("utf-8"));
                                }else if (type.equals("stopvolume")) {
                                    os.write(("type:"+type+"\n").getBytes("utf-8"));
                                }else if (type.equals("startvolume")){
                                    os.write(("type:"+type+"\n").getBytes("utf-8"));
                                }
                                Log.e(G.TAG,"发送消息给客户端:"+voiceIP);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            //删除该Socket
                            socketList.remove(s);
                            ipList.remove(s.getInetAddress().getHostAddress());
                        }

                    }
                    isRun = false;
                }

            }
        }
    }

    class VoiceThread implements Runnable{
        // 定义当前线程所处理的Socket
        Socket s = null;
        // 该线程所处理的Socket所对应的输入流
        BufferedReader br = null;

        public VoiceThread(Socket s) throws IOException {
            this.s = s;
            // 初始化该Socket对应的输入流
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String content = null;
                while((content = br.readLine()) != null) {
                    String time = util.getCurrentTime();
                    Log.e(G.TAG, "读到客户端消息:"+time+content);
                    float max = Float.valueOf(content.split("max=")[1].split(",")[0].trim());
                    float average = Float.valueOf(content.split("average=")[1].trim());
                    if(max>80 || average>50){
                        Log.e(G.TAG,"车机有声音输出");
                        util.saveToSDCard("result.txt",util.getCurrentTime()+":success,max="+max+",average="+average,false);
                    }else{
                        Log.e(G.TAG,"当前平均分贝低于50或最大分贝低于80，认为是噪音，车机未发出声音");
                        util.saveToSDCard("result.txt",util.getCurrentTime()+":fail,max="+max+",average="+average,false);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                //删除该Socket
                socketList.remove(s);
                ipList.remove(s.getInetAddress().getHostAddress());
            }

        }
    }

    class connectThread implements Runnable{

        @Override
        public void run() {
            boolean isFirst = true;

            try {
                ss = new ServerSocket(34567);
                Log.e(G.TAG,"服务已启动");

                while (true && flag) {
                    // 此行代码会阻塞,将一直等待别人的连接
                    Socket s = ss.accept();
                    removeSameClient(s);
                    socketList.add(s);
                    ipList.add(s.getInetAddress().getHostAddress());
                    Log.e(G.TAG,"有设备连上服务器,socketList长度为:"+socketList.size());

                    if (isFirst){
                        // 客户端连接后启动一条ServerThread线程为所有客户服务
                        new Thread(new SendThread()).start();
                        Log.e(G.TAG,"启动一条线程通知客户端工作");
                    }
                    isFirst = false;

                    if(s.getInetAddress().getHostAddress().equals(voiceIP)){
                        //专门开一个线程用来语音客户端与服务器交互的
                        new Thread(new VoiceThread(s)).start();
                        Log.e(G.TAG,"启动一条线程与语音客户端交互的");
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String liststr = Utils.SceneList2String(ipList);
                    editor.putString("ipList",liststr);
                    editor.commit();

                    //发送广播，通知已有设备连上服务器
                    Intent it = new Intent(G.CONNECT_ACTION);
                    it.putStringArrayListExtra("ipList", ipList);
                    sendBroadcast(it);
                }
            }catch (IOException e){
                Log.e(G.TAG,"启动服务失败"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    BroadcastReceiver mReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(G.OPEN_BT)){
                //更新数据
                ip = intent.getStringExtra("ip");
                isOpenBt = intent.getBooleanExtra("isOpen", false);
                isRun = true;
                type = "openBt";
                Log.e(G.TAG,"接收到广播"+ip+isOpenBt+isRun+type);
            }else if(intent.getAction().equals(G.CALL)){
                ip = intent.getStringExtra("ip");
                phoneNumber = intent.getStringExtra("phoneNumber");
                isRun = true;
                type = "call";
                Log.e(G.TAG,"接收到广播"+ip+phoneNumber+isRun+type);
            }else if(intent.getAction().equals(G.VOICE)) {
                ip = intent.getStringExtra("ip");
                isRun = true;
                type = "voice";
                Log.e(G.TAG, "接收到广播" + ip + isRun + type);
            }else if(intent.getAction().equals(G.SELECT_VOICE)){
                voiceIP = intent.getStringExtra("voiceIP");
                Log.e(G.TAG, "接收到广播,voiceIP="+voiceIP);
                try {
                    for (Socket s:socketList){
                        Log.e(G.TAG, s.getInetAddress().getHostAddress());
                        if(s.getInetAddress().getHostAddress().equals(voiceIP)){
                            //专门开一个线程用来语音客户端与服务器交互的
                            new Thread(new VoiceThread(s)).start();
                            Log.e(G.TAG,"启动一条线程与语音客户端交互的");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(intent.getAction().equals(G.PLAY_VOICE_INTENT)){
                filename = intent.getStringExtra("filename");
                isRun = true;
                type = "play";
                Log.e(G.TAG, "接收到广播" + filename+ isRun + type);
            }else if(intent.getAction().equals(G.START_INTENT)){
                isRun = true;
                type = "startvolume";
                Log.e(G.TAG, "接收到广播" + isRun + type);
            }else if(intent.getAction().equals(G.STOP_INTENT)){
                isRun = true;
                type = "stopvolume";
                Log.e(G.TAG, "接收到广播" + isRun + type);
            }
        }
    };

    public void removeSameClient(Socket s){
        String ip = s.getInetAddress().getHostAddress();
        for(int i=0;i<ipList.size();i++){
            if(ipList.get(i).equals(ip)){
                ipList.remove(i);
                socketList.remove(i);
            }
        }
    }

    @Override
    public void onDestroy() {
        flag = false;
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}
