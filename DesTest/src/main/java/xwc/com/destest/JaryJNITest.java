package xwc.com.destest;

/**
 * 错误的调用方式，与jni中定义的方法在路径与命名上都不同
 * Created by xwc on 2017/8/11.
 */

public class JaryJNITest {
    static {
        System.loadLibrary("jary");
    }
    public native String getString();
}
