package com.autotest.lckj.socketclientdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText et_ip;
    private Button btn_connect;

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
        et_ip = (EditText)findViewById(R.id.et_ip);
        btn_connect = (Button)findViewById(R.id.btn_connect);

        //获取上次连接成功的服务器ip地址
        SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
        String serverIP = sharedPreferences.getString("serverIP","");
        String isConnect = sharedPreferences.getString("isConnect","连接服务器");
        et_ip.setText(serverIP);
        btn_connect.setText(isConnect);

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = et_ip.getText().toString();
                if(ip == null || ip.equals("")){
                    Toast.makeText(MainActivity.this, "请输入服务器IP!!!",Toast.LENGTH_LONG).show();
                    return;
                }

                if (btn_connect.getText().toString().equals("已连接")){
                    Toast.makeText(MainActivity.this, "已连接服务器",Toast.LENGTH_LONG).show();;
                    return;
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

            SharedPreferences sharedPreferences = context.getSharedPreferences("test",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("isConnect","已连接");
            editor.commit();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
