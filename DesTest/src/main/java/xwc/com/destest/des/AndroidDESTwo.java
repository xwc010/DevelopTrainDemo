package xwc.com.destest.des;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密所用秘钥真实长度为最长8位，不够就用0补
 * Created by xwc on 2017/8/1.
 */

public class AndroidDESTwo {

    private Key mKey; // 加密解密的key
    private Cipher mDecryptCipher; // 解密的密码
    private Cipher mEncryptCipher; // 加密的密码
    private Context context;

    public AndroidDESTwo(String key) throws Exception {
        initKey(key);
        initCipher();
    }

    public AndroidDESTwo(Context context, String key) throws Exception {
        this.context = context;
        initKey(key);
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
    public void encryptFile(String filePath, String savePath) throws FileNotFoundException {
        encryptFile(new FileInputStream(filePath), savePath);
    }

    /**
     * 加密文件
     * @param in
     * @param savePath 加密后保存的位置
     */
    public void encryptFile(InputStream in, String savePath) {
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
     * 同步解密文件
     * @param context
     * @param assetsFileName assets目录下DES加密后的文件
     */
    public void decryptDesSync(Context context, String assetsFileName) {

        try {
            InputStream in = context.getAssets().open(assetsFileName);
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

    /**
     * 异步解密文件
     * @param context
     * @param assetsFileName assets目录下DES加密后的文件
     */
    public void decryptDesAsync(final Context context, final String assetsFileName){

        new Thread(new Runnable() {
            @Override
            public void run() {
                decryptDesSync(context, assetsFileName);
            }
        }).start();
    }

    /**
     * 解密文件
     * @param filePath 文件路径
     * @throws Exception
     */
    public void decryptFile(String filePath) throws Exception {
        decryptFile(new FileInputStream(filePath));
    }

    /**
     * 解密文件
     * @param in
     */
    public void decryptFile(InputStream in) {
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

    public static void main(String[] args) throws Exception {
        AndroidDESTwo fileDES = new AndroidDESTwo("com.xwc.des");
        fileDES.encryptFile("D:/encrypt.txt", "D:/decrypt");  //加密
        fileDES.decryptFile("D:/decrypt"); //解密
    }
}
