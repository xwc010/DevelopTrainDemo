package xwc.com.destest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
