package com.autotest.lckj.bluetoothdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView show;
    private static final String ACTION = "com.lckj.autotest.connectmsg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册广播
        IntentFilter intentFilter = new IntentFilter(ACTION);
        registerReceiver(mReceiver, intentFilter);

        Utils utils = new Utils();
        String ip = utils.getIp(this);
        textView = (TextView) findViewById(R.id.ip);
        show = (TextView) findViewById(R.id.tv_show);
        textView.setText("服务器ip为："+ip);

        Intent it = new Intent(this, MyService.class);
        startService(it);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            //更新数据
            Log.e("com.lckj.autotest","有设备连上服务器");
            ArrayList<String> ipList = intent.getStringArrayListExtra("ipList");
            String str = "";
            for(String s:ipList){
                str += s +"\n";
            }
            show.setText(str);
        }
    };
}
