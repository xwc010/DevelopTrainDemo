package xwc.com.dding;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

public class DDService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ClockReceiver clockReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("DDingClock", "onCreate");
        clockReceiver = new ClockReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(clockReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DDingClock", "onStartCommand");
        flags = Service.START_STICKY; // START_STICKY
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Log.i("DDingClock", "onDestroy");
        unregisterReceiver(clockReceiver);
        Intent sevice = new Intent(this, DDService.class);
        this.startService(sevice);
        super.onDestroy();
    }
}
