package com.xubao.comment.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/28
 */
public final class EncryptUtil {
    private EncryptUtil(){}

    /**
     * DES加密
     *
     * @param data 需要加密的数据
     * @param key  加密使用的密钥
     * @return 加密后获取的字节数组
     */
    public static byte[] desEncrypt(byte[] data, byte[] key) {
        //恢复密钥
        SecretKey secretKey = new SecretKeySpec(key, "DES");
        try {
            //Cipher完成加密或解密工作
            Cipher cipher = Cipher.getInstance("DES");
            //根据密钥对Cipher进行初始化 ENCRYPT_MODE, DECRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            //加密
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密
     */
    /**
     * @param data 密文对应的字节数组
     * @param key  算法名字
     * @return 解密后的字节数组
     */
    public static byte[] desDecrypt(byte[] data, byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "DES");
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
