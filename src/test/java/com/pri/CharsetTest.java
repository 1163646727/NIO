package com.pri;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;

/**
 * <BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/14 <BR>
 */
@SpringBootTest
public class CharsetTest {

    //查看Charset字符集列表
    @Test
    public void test(){
        Map<String,Charset> map =  Charset.availableCharsets();
        for (String key :map.keySet()) {
            System.out.println(key+">>>"+map.get(key));
        }
    }

    @Test
    public void test2() throws CharacterCodingException {
        Charset cs1 = Charset.forName("UTF-8");
        //获取编码器
        CharsetEncoder ce = cs1.newEncoder();
        //获取解码器
        CharsetDecoder de = cs1.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("你好 world!");
        cBuf.flip();

        //编码
        ByteBuffer bBuf = ce.encode(cBuf);
        for (int i=0;i<bBuf.limit();i++) {
            System.out.print(bBuf.get(i)+"  ");
        }
        System.out.println();

        //解码
        CharBuffer cBuf2 = de.decode(bBuf);
        System.out.println(cBuf2.toString());

        System.out.println("--------------------------------");
        cs1 = Charset.forName("GBK");
        //获取解码器
        CharsetDecoder de2 = cs1.newDecoder();
        bBuf.flip();
        System.out.println(de2.decode(bBuf));
    }


}
