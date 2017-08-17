import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ADBAutoClock {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static String dataStr = "8-45"; // "小时-�"
    private final static int randomSpace = 5;

    public static void main(String[] args) {

        String[] dataArray = dataStr.split("-");
        int hour = Integer.parseInt(dataArray[0]);
        int minute = Integer.parseInt(dataArray[1]);

        Random random = new Random();
        int b = random.nextInt(1);

        if(b < 1){
            minute += random.nextInt(randomSpace);
        }else {
            minute -= random.nextInt(randomSpace);
        }

        display(new DisplayTask(), hour, minute);
        display(new StopTask(), hour, minute + 1);
        display(new DisplayTask(), hour, minute + 2);
        display(new StopTask(), hour, minute + 3);
        display(new DisplayTask(), hour, minute + 4);
    }

    private static void display(TimerTask timerTask, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,1);//把日期往后增加一�.整数�后推,负数�前移�
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        Date displayTime = calendar.getTime();
        System.out.println("ADBAutoClock displayTime: " + format.format(displayTime));
        Timer dTimer = new Timer();
        dTimer.schedule(timerTask, displayTime);
    }


    static class DisplayTask extends TimerTask {
        public void run() {
            System.out.println("Do DisplayTask");
            //启动叮叮
            try {
                execCommand("adb wait-for-device");
                execCommand("adb shell am start -n com.alibaba.android.rimet/com.alibaba.android.rimet.biz.SplashActivity");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cancel();
        }
    }

    static class StopTask extends TimerTask {
        public void run() {
            System.out.println("Do StopTask");
            try {
                execCommand("adb wait-for-device");
                execCommand("adb shell am force-stop com.alibaba.android.rimet");
            } catch (IOException e) {
                e.printStackTrace();
            }
            cancel();
        }
    }


    /**
     * @return HHmm
     */
    public static int getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return Integer.parseInt(str);
    }

    public static void execCommand(String command) throws IOException {
        // start the ls command running
        //String[] args =  new String[]{"sh", "-c", command};
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);        //这句话就是shell与高级语�间的调用
        //如果有参数的话可以用另外�个被重载的exec方法
        //实际上这样执行时启动了一个子进程,它没有父进程的控制台
        //也就看不到输�,�以我们需要用输出流来得到shell执行后的输出
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        // read the ls output
        String line = "";
        StringBuilder sb = new StringBuilder(line);
        while ((line = bufferedreader.readLine()) != null) {
            //System.out.println(line);
            sb.append(line);
            sb.append("\n");
        }

        //使用exec执行不会等执行成功以后才返回,它会立即返回
        //�以在某些情况下是很要命的(比如复制文件的时�)
        //使用wairFor()可以等待命令执行完成以后才返�
        try {
            if (proc.waitFor() != 0) {
                System.out.println(proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
