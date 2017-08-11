package xwc.com.destest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import xwc.com.ndkdemo.JniTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.tv);
        JniTest jniTest = new JniTest();
        tv.setText(getPackageName()+"\n"+ jniTest.getString()
                + "\n3+3 = " + jniTest.plus(3,3));
//        Log.i("JNI", new JaryJNITest().getString());

        Log.i("CVS", "---- DES Start ---- ");
//        AdjustDES.initAdjustIds(this, "yifants");
//        Map<String, String> map = AdjustDES.adjustMap;
//        if(map != null){
//            Set<String> set = map.keySet();
//            Iterator<String> iterator = set.iterator();
//            while (iterator.hasNext()){
//                String key = iterator.next();
//                Log.i("CVS", "Map >> " + key + " - " + map.get(key));
//            }
//        }
//        Log.i("CVS", "---- DES End ---- ");

//        try {
//            FileDES fileDES = new FileDES("com.yi");
//            fileDES.decryptDesAsync(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
