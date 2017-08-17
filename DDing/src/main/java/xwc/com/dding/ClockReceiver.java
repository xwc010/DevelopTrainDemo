package xwc.com.dding;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xwc on 2017/8/11.
 */

public class ClockReceiver extends BroadcastReceiver {

    public final static int ClockTime = 902; // 小时 分钟
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("DDingClock", "--- onReceive ---");
        if(needDisplay(context)){
            new Thread(new DisplayRunner(context.getApplicationContext())).start();

//            Timer timer = new Timer(true);
//            TimerTask timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    try {
//                        Log.i("DDingClock", "Do StopTask");
//                        DDingAutoClock.execCommand("adb shell am force-stop com.alibaba.android.rimet");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//
//            timer.schedule(timerTask, 20*1000);
        }
    }

    public boolean needDisplay(Context context){
        SharedPreferences cache = context.getSharedPreferences("DDing", Context.MODE_PRIVATE);
        // 打卡时间
        int ddTime = cache.getInt("ClockTime", ClockTime);

        String displayKey = "Display_"+dateFormat.format(new Date());
        Log.i("DDingClock", "displayKey: " + displayKey);

        // 打卡次数
        int displayNum = cache.getInt(displayKey, 0);

        // 当前时间
        int currentTime = ADBAutoClock.getCurrentTime();

        if(displayNum < 3 && currentTime >= (ddTime + displayNum * 2)){
            displayNum ++;
            cache.edit().putInt(displayKey, displayNum).commit();
            Log.i("DDingClock", "needDisplay = true");
            return true;
        }

        Log.i("DDingClock", "needDisplay = false");
        return false;
    }

    class DisplayRunner implements Runnable {
        Context context;

        public DisplayRunner(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
//            try {
//                Log.i("DDingClock", "Do DisplayTask");
//                DDingAutoClock.execCommand("adb shell am start -n com.alibaba.android.rimet/com.alibaba.android.rimet.biz.SplashActivity");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            try {
                Log.i("DDingClock", "Do DisplayTask");
                ComponentName componentName = new ComponentName(
                        //这个是另外一个应用程序的包名
                        "com.alibaba.android.rimet",
                        //这个参数是要启动的Activity
                        "com.alibaba.android.rimet.biz.SplashActivity");
                Intent intent = new Intent();
                intent.setComponent(componentName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            } catch (Exception e) {

                Log.e("nafio",e.getMessage());
            }
        }
    };
}
