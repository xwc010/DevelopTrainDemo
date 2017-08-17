package com.autotest.lckj.bluetoothdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils utils = new Utils();
        String ip = utils.getIp(this);
        textView = (TextView) findViewById(R.id.ip);
        textView.setText("服务器ip为："+ip);

        Intent it = new Intent(this, MyService.class);
        startService(it);

    }

}
