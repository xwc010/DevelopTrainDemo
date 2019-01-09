package com.autotest.lckj.socketserverdemo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView show;
    private EditText et;
    private Button btn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册广播
        IntentFilter intentFilter = new IntentFilter(G.CONNECT_ACTION);
        registerReceiver(mReceiver, intentFilter);

        Utils utils = new Utils();
        String ip = utils.getIp(this);
        textView = (TextView) findViewById(R.id.ip);
        show = (TextView) findViewById(R.id.tv_show);
        et = (EditText) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.button);
        textView.setText("服务器ip为："+ip);

        sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
        String vip = sharedPreferences.getString("voiceIP","192.168.33.");
        et.setText(vip);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = et.getText().toString();
                if(ip == null || ip.equals("")){
                    Toast.makeText(MainActivity.this,"请输入客户端IP地址",Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(MainActivity.this,"语音客户端IP为："+ip,Toast.LENGTH_LONG).show();
                //通知service语音识别设备ip
                Intent i = new Intent(G.SELECT_VOICE);
                i.putExtra("voiceIP",ip);
                MainActivity.this.sendBroadcast(i);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("voiceIP",ip);
                editor.commit();
            }
        });

        String liststr = sharedPreferences.getString("ipList","");
        try {
            ArrayList<String> ipList = Utils.String2SceneList(liststr);
            refreshShow(ipList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        Intent it = new Intent(this, MyService.class);
        startService(it);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    BroadcastReceiver mReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            //更新数据
            ArrayList<String> ipList = intent.getStringArrayListExtra("ipList");
            refreshShow(ipList);
        }
    };

    public void refreshShow(ArrayList<String> ipList){
        String str = "";
        for(String s:ipList){
            str += s +"\n";
        }
        show.setText(str);
    }

}
