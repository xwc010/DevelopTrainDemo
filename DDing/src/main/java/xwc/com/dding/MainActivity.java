package xwc.com.dding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText_up;
    private EditText editText_down;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent sevice = new Intent(this, DDService.class);
        this.startService(sevice);

        textView = (TextView) findViewById(R.id.textView);
        showClockTime();
        editText_up = (EditText) findViewById(R.id.editText_up);
        editText_down = (EditText) findViewById(R.id.editText_down);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time_up = editText_up.getText().toString().trim();
                String time_down = editText_down.getText().toString().trim();
                SharedPreferences cache = getSharedPreferences("DDing", Context.MODE_PRIVATE);
                if (!TextUtils.isEmpty(time_up)) {
                    cache.edit().putInt("ClockTime_UP", Integer.parseInt(time_up)).commit();
                }

                if (!TextUtils.isEmpty(time_down)) {
                    cache.edit().putInt("ClockTime_DOWN", Integer.parseInt(time_down)).commit();
                }

                showClockTime();
            }
        });
    }

    private void showClockTime() {
        SharedPreferences cache = getSharedPreferences("DDing", Context.MODE_PRIVATE);
        textView.setText("【UPDATE SETTINGS】\n  CLOCK  IN：" + cache.getInt("ClockTime_UP", ClockReceiver.ClockTime_UP)
                + "\n  CLOCK OUT：" + cache.getInt("ClockTime_DOWN", ClockReceiver.ClockTime_DOWN));
    }
}
