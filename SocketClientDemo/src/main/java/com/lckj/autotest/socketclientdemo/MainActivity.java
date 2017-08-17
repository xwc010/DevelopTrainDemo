package com.lckj.autotest.socketclientdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView tv_ip;
    private EditText et_ip;
    private Button btn_connect;
    private Socket s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        findViewById();

        IntentFilter intentFilter = new IntentFilter("com.lckj.autotest.USER_ACTION");
        registerReceiver(mReceiver, intentFilter);
    }

    private void findViewById(){
        tv_ip = (TextView)findViewById(R.id.tv_ip);
        et_ip = (EditText)findViewById(R.id.et_ip);
        btn_connect = (Button)findViewById(R.id.btn_connect);

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = et_ip.getText().toString();
                if(ip.equals("")){
                    Toast.makeText(MainActivity.this, "请输入服务器IP!!!",Toast.LENGTH_LONG).show();
                }

                //启动service，连接socket
                Intent it = new Intent(MainActivity.this, MyService.class);
                it.putExtra("ip",ip);
                startService(it);

            }
        });
    }

    BroadcastReceiver mReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            //刷新主Activity界面
            btn_connect.setText("已连接");
            btn_connect.setClickable(false);
        }
    };

}
