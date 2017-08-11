package xwc.com.dding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent sevice = new Intent(this, DDService.class);
        this.startService(sevice);

        textView = (TextView) findViewById(R.id.textView);
        showClockTime();
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(time)){
                    SharedPreferences cache = getSharedPreferences("DDing", Context.MODE_PRIVATE);
                    cache.edit().putInt("ClockTime", Integer.parseInt(time)).commit();
                    showClockTime();
                }else {
                    Toast.makeText(MainActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showClockTime(){
        SharedPreferences cache = getSharedPreferences("DDing", Context.MODE_PRIVATE);
        textView.setText("历史设置：" + cache.getInt("ClockTime", ClockReceiver.ClockTime));
    }
}
