package xwc.com.threadpooldemo;

/**
 * Created by xwc on 2017/8/7.
 */

public class SingleOne {

    private static SingleOne mInstance;
    private Tool tool;

    private SingleOne(){
        // 在构造方法中执行多线程操作，易造成空指针
        Pool.executor(new Runnable() {
            @Override
            public void run() {
                System.out.println("Create SingleOne");
                tool = new Tool();
            }
        });
    }

    public static SingleOne getInstance(){
        if(mInstance == null){
            synchronized (SingleOne.class){
                if(mInstance == null){
                    mInstance = new SingleOne();
                }
            }
        }

        return mInstance;
    }

    public void useTool(){
        tool.say();
    }
}
