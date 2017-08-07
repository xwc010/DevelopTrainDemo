package xwc.com.destest.des;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 加解密所用秘钥长度不限
 * Created by xwc on 2017/8/1.
 */
public class AndroidDESOne {

    private static SecretKey key;
    private static byte[] iv = {1,2,3,4,5,6,7,8};


    /**
     * 生成密钥key对象
     * @param keyStr 密钥字符串
     * @return 密钥对象
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws Exception
     */
    private static SecretKey keyGenerator(String keyStr) throws Exception {
//        byte input[] = HexStr2Bytes(keyStr);
        DESKeySpec desKey = new DESKeySpec(keyStr.getBytes());
        //创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(desKey);
        return securekey;
    }


    /**
     * 同步解析Adjust广告配置文件
     * @param context
     */
    private static void decryptDesSync(Context context, String assetsFileName){

        Map<String, String> map = new HashMap<>();

        String savePath = context.getExternalCacheDir().getPath()+ File.separator + "Cache.csv";
        Log.i("CSV", "savePath\n" + savePath);
        File saveFile = new File(savePath);

        InputStream encryptIn = null;
        OutputStream decryptOut = null;
        CipherOutputStream decryptCOS = null;

        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
//          IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
//            SecretKeySpec key = new SecretKeySpec(SecretKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);

            // 把解密后的数据保存至制定文件
            encryptIn = context.getAssets().open(assetsFileName);
            decryptOut = new FileOutputStream(saveFile);
            decryptCOS = new CipherOutputStream(decryptOut, cipher);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = encryptIn.read(buffer)) >= 0) {
                decryptCOS.write(buffer, 0, length);
            }

            // 直接读取解密后的数据
//            InputStream in = context.getAssets().open(secretFileName);
//            CipherInputStream cin = new CipherInputStream(in, cipher);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(cin));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {

                if(encryptIn != null){
                    encryptIn.close();
                }

                if(decryptOut != null){
                    decryptOut.close();
                }

                if(decryptCOS != null){
                    decryptCOS.close();
                }

                if(saveFile.exists()){
                    saveFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 异步解密文件
     * @param context
     * @param assetsFileName assets目录下DES加密后的文件
     */
    private static void decryptDesAsync(final Context context, final String assetsFileName){

        new Thread(new Runnable() {
            @Override
            public void run() {
                decryptDesSync(context, assetsFileName);
            }
        }).start();
    }


    /**
     * 初始化秘钥
     * @param pkg
     */
    private static void initSecretKey(String pkg){
        try {
            key = keyGenerator(str2HexStr(pkg)); // new String(pkg.getBytes())
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(">>> initSecretKey Exception");
        }
    }


    /**
     * 文件file进行加密并保存目标文件destFile中
     * @param file   要加密的文件 如c:/test/srcFile.txt
     * @param destFile 加密后存放的文件名 如c:/加密后文件.txt
     */
    private static void encrypt(String file, String destFile) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
//        IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
//        SecretKeySpec key = new SecretKeySpec(SecretKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);

        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(destFile);
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        cis.close();
        is.close();
        out.close();
    }


    /**
     * 文件采用DES算法解密文件
     * @param file 已加密的文件 如c:/加密后文件.txt
     *         * @param destFile
     *         解密后存放的文件名 如c:/ test/解密后文件.txt
     */
    private static void decrypt(String file, String dest) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
//      IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
//        SecretKeySpec key = new SecretKeySpec(SecretKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);

        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(dest);
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = is.read(buffer)) >= 0) {
            System.out.println();
            cos.write(buffer, 0, r);
        }
        cos.close();
        out.close();
        is.close();
    }

    /**
     * 字符串转换成十六进制字符串
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    private static String str2HexStr(String str){
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    // 从十六进制字符串到字节数组转换
    public static byte[] HexStr2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    private static int parse(char c) {
        if (c >= 'a') return (c - 'a' + 10) & 0x0f;
        if (c >= 'A') return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    public static void main(String[] args) throws Exception {
        initSecretKey("com.yifan");
        encrypt("E:\\test.csv", "E:\\adjust"); //加密
        System.out.println("加密完成");
        decrypt("E:\\adjust", "E:\\adjust.csv"); //解密
        System.out.println("解密完成");
    }
}
