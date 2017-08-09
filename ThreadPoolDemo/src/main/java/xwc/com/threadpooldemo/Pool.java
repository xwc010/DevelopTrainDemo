package xwc.com.threadpooldemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xwc on 2017/8/7.
 */

public class Pool {
    private static ExecutorService executorService;

    public static void init(){
        executorService = Executors.newFixedThreadPool(3);
    }

    public static void executor(Runnable command){
        executorService.execute(command);
    }
}
