package com.autotest.lckj.socketserverdemo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by ouyangxiangqing on 2017/8/16.
 */

public class Utils {

    public String getIp(Context context){
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    public static String SceneList2String(List SceneList) throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(SceneList);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();
        return SceneListString;

    }

    @SuppressWarnings("unchecked")
    public static ArrayList String2SceneList(String SceneListString) throws StreamCorruptedException, IOException, ClassNotFoundException {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        ArrayList SceneList = (ArrayList) objectInputStream.readObject();
        objectInputStream.close();
        return SceneList;
    }

    public void saveToSDCard(String filename, String content,boolean append) {
        String en = Environment.getExternalStorageState();
        try {
            if (en.equals(Environment.MEDIA_MOUNTED)) {
                File file = new File(Environment.getExternalStorageDirectory(),"Autotest");
                if (!file.exists()) {
                    Log.i(TAG, "is createNewFloder:" + file.mkdirs());
                }
                file = new File(file.getPath(), filename);
                if (!file.exists()) {
                    Log.i(TAG, "is createNewFile:" + file.createNewFile());
                } else {
                    if (!append) {
                        Log.i(TAG, "is delete:" + file.delete());
                        Log.i(TAG, "is createNewFile:" + file.createNewFile());
                    }
                }
                OutputStream out = new FileOutputStream(file, append);
                System.out.println(content.toString());
                Log.i(G.TAG, "content:" + content.toString());
				out.write(content.toString().getBytes("utf-8"));
                out.close();
            } else {
                Log.i(TAG, "error:" + "没有sd卡");
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "error:" + e.getLocalizedMessage());
        }
    }

    public String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String currentTime = df.format(new Date());// new Date()为获取当前系统时间
        return currentTime;
    }
}
