package com.xubao.comment.utilTest;

import com.xubao.comment.util.EncryptUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/28
 */
public class EncryptUtilTest {
    @Test
    public void test() throws UnsupportedEncodingException {
        String data = "f5444444";
        String key = "jjjjjjjj";
        byte[] bytes = EncryptUtil.desEncrypt(data.getBytes(), key.getBytes());
        String aa = new String(bytes,"utf-8");
        //System.out.println(aa);
        byte[] bytes1 = EncryptUtil.desDecrypt(bytes, key.getBytes());
        System.out.println(new String(bytes1,"utf-8"));
    }
}
