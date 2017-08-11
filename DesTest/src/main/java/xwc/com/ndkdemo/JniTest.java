package xwc.com.ndkdemo;

/**
 * Created by xwc on 2017/8/10.
 */

public class JniTest {
    static {
        System.loadLibrary("jary");
    }
    public native String getString();
    public native int plus(int a, int b);
}
