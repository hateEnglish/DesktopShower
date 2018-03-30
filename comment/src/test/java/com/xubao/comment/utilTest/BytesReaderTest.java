package com.xubao.comment.utilTest;

import com.xubao.comment.util.BytesReader;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/30
 */
public class BytesReaderTest {
    @Test
    public void test() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(408);
        dos.writeInt(408);

        byte[] data = baos.toByteArray();

        System.out.println(BytesReader.readInt(data,0));
        System.out.println(BytesReader.readInt(data,4));
    }
}
