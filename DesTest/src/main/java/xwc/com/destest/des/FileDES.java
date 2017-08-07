package xwc.com.destest.des;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密所用秘钥真实长度为最长8位，不够就用0补
 * Created by xwc on 2017/8/1.
 */

public class FileDES {

    /**
     * 加密解密的key
     */
    private Key mKey;
    /**
     * 解密的密码
     */
    private Cipher mDecryptCipher;
    /**
     * 加密的密码
     */
    private Cipher mEncryptCipher;

    private Context context;

    public FileDES(String key) throws Exception {
        initKey(key);
        initCipher();
    }

    public FileDES(Context context) throws Exception {
        this.context = context;
        initKey(context.getPackageName());
        initCipher();
    }

    /**
     * 创建一个加密解密的key
     * @param keyRule
     */
    public void initKey(String keyRule) {
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        mKey = new SecretKeySpec(byteTemp, "DES");
    }

    /***
     * 初始化加载密码
     * @throws Exception
     */
    private void initCipher() throws Exception {
        mEncryptCipher = Cipher.getInstance("DES");
        mEncryptCipher.init(Cipher.ENCRYPT_MODE, mKey);

        mDecryptCipher = Cipher.getInstance("DES");
        mDecryptCipher.init(Cipher.DECRYPT_MODE, mKey);
    }

    /**
     * 加密文件
     * @param filePath 需要加密的文件路径
     * @param savePath 加密后保存的位置
     * @throws FileNotFoundException
     */
    public void doEncryptFile(String filePath, String savePath) throws FileNotFoundException {
        doEncryptFile(new FileInputStream(filePath), savePath);
    }

    /**
     * 加密文件
     * @param in
     * @param savePath 加密后保存的位置
     */
    public void doEncryptFile(InputStream in, String savePath) {
        if (in == null) {
            System.out.println("inputstream is null");
            return;
        }
        try {
            CipherInputStream cin = new CipherInputStream(in, mEncryptCipher);
            OutputStream os = new FileOutputStream(savePath);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = cin.read(bytes)) > 0) {
                os.write(bytes, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            in.close();
            System.out.println("加密成功");
        } catch (Exception e) {
            System.out.println("加密失败");
            e.printStackTrace();
        }
    }


    /**
     * 解密文件
     * @param filePath 文件路径
     * @throws Exception
     */
    public void doDecryptFile(String filePath) throws Exception {
        doDecryptFile(new FileInputStream(filePath));
    }

    /**
     * 解密文件
     *
     * @param in
     */
    public void doDecryptFile(InputStream in) {
        if (in == null) {
            System.out.println("inputstream is null");
            return;
        }
        try {
            CipherInputStream cin = new CipherInputStream(in, mDecryptCipher);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cin));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            cin.close();
            in.close();
            System.out.println("解密成功");
        } catch (Exception e) {
            System.out.println("解密失败");
            e.printStackTrace();
        }
    }


    public static Map<String, String> adjustMap;
    private static final String secretFileName = "adjust";
    /**
     * 解密文件
     */
    public void decryptDesSync(Context context) {

        try {
            Map<String, String> map = new HashMap<>();
            InputStream in = context.getAssets().open(secretFileName);
            CipherInputStream cin = new CipherInputStream(in, mDecryptCipher);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cin));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] kv = line.split(",");
                Log.i("CVS", "-- map key --\n" + kv[0]);
                if(kv.length == 2){
                    map.put(kv[0], kv[1]);
                    Log.i("CVS", "-- map item --\n" + kv[0] + " - " + kv[1]);
                }
            }
            reader.close();
            cin.close();
            in.close();
            System.out.println("解密成功");
            adjustMap = map;
        } catch (Exception e) {
            System.out.println("解密失败");
            e.printStackTrace();
        }
    }

    /**
     * 异步解析Adjust广告配置文件
     * @param context
     */
    public void decryptDesAsync(final Context context){

        new Thread(new Runnable() {
            @Override
            public void run() {
                decryptDesSync(context);
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        FileDES fileDES = new FileDES("spring.sky");
        fileDES.doEncryptFile("d:/a.txt", "d:/b");  //加密
        fileDES.doDecryptFile("d:/b"); //解密
    }
}
