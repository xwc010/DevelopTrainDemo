package com.autotest.lckj.socketclientdemo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
        openPermissions();
        findViewById();

        IntentFilter intentFilter = new IntentFilter(G.USER_ACTION);
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
                    Dialog dialog=new AlertDialog.Builder(MainActivity.this)
                            .setTitle("询问框")
                            .setMessage("确定断开与服务器连接吗？")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(G.DISCONNECT);
                                    sendBroadcast(i);

                                    btn_connect.setText("连接服务器");
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
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

            SharedPreferences sharedPreferences = context.getSharedPreferences("test",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("isConnect","已连接");
            editor.commit();
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void openPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)  //拨打电话
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)   //蓝牙管理
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)  //蓝牙
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)  //网络
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH
                            ,Manifest.permission.INTERNET},
                    1);}
    }

}
