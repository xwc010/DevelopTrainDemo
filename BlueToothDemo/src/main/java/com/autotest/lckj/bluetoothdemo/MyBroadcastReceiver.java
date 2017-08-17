package com.autotest.lckj.bluetoothdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by ouyangxiangqing on 2017/8/14.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    private final String DAIL_INTENT = "com.lckj.autotest.dail" ;
    private final String BLUETTOTH_INTENT = "com.lckj.autotest.bluetooth" ;
    private final String OPEN_BT = "com.lckj.autotest.sendmsg";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(DAIL_INTENT)){
            Log.i("com.lckj.autotest.dail","接收到广播");
            //获取电话号码
            String phoneNumber = intent.getStringExtra("phoneNumber");
            //拨打电话
            Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
            phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(phoneIntent);
        }
        else if(intent.getAction().equals(BLUETTOTH_INTENT)){
            Toast.makeText(context,"收到开关蓝牙的广播:"+MyService.ip +MyService.isOpenBt+MyService.isRun,Toast.LENGTH_LONG).show();
            Intent i = new Intent(OPEN_BT);
            i.putExtra("ip",intent.getStringExtra("ip"));
            i.putExtra("isOpen", intent.getBooleanExtra("isOpen", false));
            context.sendBroadcast(i);
        }

    }

}
