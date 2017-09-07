package xwc.com.dding;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by xwc on 2017/8/11.
 */

public class ClockReceiver extends BroadcastReceiver {

    Handler mHandler = new Handler(Looper.getMainLooper());
    public final static int ClockTime = 905; // 小时 分钟
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
    private final static int randomSpace = 3;

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

        Random random = new Random();

        boolean boo = (System.currentTimeMillis() % 2) > 0;
        if(boo){
            ddTime += random.nextInt(randomSpace);
        }else {
            ddTime -= random.nextInt(randomSpace);
        }

        Log.i("DDingClock", "ddTime: " + ddTime);

        String displayKey = "Display_"+dateFormat.format(new Date());
        Log.i("DDingClock", "displayKey: " + displayKey);

        // 打卡次数
        int displayNum = cache.getInt(displayKey, 0);

        // 当前时间
        int currentTime = ADBAutoClock.getCurrentTime();

        boolean isWorkDate = true; // 是否是工作日
        Calendar c = Calendar.getInstance();
        if(c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            isWorkDate = false;
        }

        if(isWorkDate && displayNum < 3 && currentTime >= (ddTime + displayNum * 2)){
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

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        wakeUpAndUnlock(context);
                    }
                });
            } catch (Exception e) {

                Log.e("nafio",e.getMessage());
            }
        }
    };

    public void wakeUpAndUnlock(Context context){
        Log.i("DDingClock", "Do wakeUpAndUnlock");
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }
}
