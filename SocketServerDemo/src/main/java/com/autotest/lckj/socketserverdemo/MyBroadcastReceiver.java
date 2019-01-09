package com.autotest.lckj.socketserverdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ouyangxiangqing on 2017/8/14.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(G.DAIL_INTENT)){
            Log.i(G.TAG,"接收拨打电话的广播");
            Intent i = new Intent(G.CALL);
            i.putExtra("ip",intent.getStringExtra("ip"));
            i.putExtra("phoneNumber", intent.getStringExtra("phoneNumber"));
            context.sendBroadcast(i);
        }
        else if(intent.getAction().equals(G.BLUETTOTH_INTENT)){
            Log.i(G.TAG,"收到开关蓝牙的广播");
            Intent i = new Intent(G.OPEN_BT);
            i.putExtra("ip",intent.getStringExtra("ip"));
            i.putExtra("isOpen", intent.getBooleanExtra("isOpen", false));
            context.sendBroadcast(i);
        }
//        else if (intent.getAction().equals(G.VOICE_INTENT)){
//            new Utils().saveToSDCard("voice.txt","",false);
//            Log.i(G.TAG,"收到语音识别的广播");
//            Intent i = new Intent(G.VOICE);
//            i.putExtra("ip",intent.getStringExtra("ip"));
//            context.sendBroadcast(i);
//        }
        else if(intent.getAction().equals(G.PLAY_VOICE)){
            Log.i(G.TAG,"收到播报语音的广播");
            Intent i = new Intent(G.PLAY_VOICE_INTENT);
            i.putExtra("filename",intent.getStringExtra("filename"));
            context.sendBroadcast(i);
        }
        else if(intent.getAction().equals(G.START_VOLUME)){
            Log.i(G.TAG,"收到开始录音的广播");
            Intent i = new Intent(G.START_INTENT);
            context.sendBroadcast(i);
        }
        else if(intent.getAction().equals(G.STOP_VOLUME)){
            Log.i(G.TAG,"收到停止录音的广播");
            Intent i = new Intent(G.STOP_INTENT);
            context.sendBroadcast(i);
        }

    }

}
