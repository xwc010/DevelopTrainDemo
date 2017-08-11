package xwc.com.ndkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_jni = (TextView) findViewById(R.id.tv_jni);
//        tv_jni.setText(new JniTest().getString());
        JniTest jniTest = new JniTest();
        tv_jni.setText(jniTest.getString()
            + "\n2+3 = " + jniTest.plus(2,3));
    }
}
