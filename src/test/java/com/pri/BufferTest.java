package com.pri;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.vm.VM;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * <BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/12 <BR>
 */
@SpringBootTest
public class BufferTest {


    @Test
    public void test(){
        //通过allocate() 获取缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println("buf---allocate() :"+buf.isDirect()+",capacity:"+buf.capacity()+",内存地址："+ VM.current().addressOf(buf));
        buf = ByteBuffer.allocateDirect(1024);
        System.out.println("buf---allocateDirect() :"+buf.isDirect()+",capacity:"+buf.capacity()+",内存地址："+VM.current().addressOf(buf));
        buf = ByteBuffer.allocateDirect(2024);
        System.out.println("buf---allocateDirect() :"+buf.isDirect()+",capacity:"+buf.capacity()+",内存地址："+VM.current().addressOf(buf));
        ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);
        System.out.println("allocateDirect() :"+buf2.isDirect());
    }

    @Test
    public void rest2(){
        String str = "abcdef";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println(buf);
        buf.put(str.getBytes());
        System.out.println("put()后："+buf+"---内存地址："+VM.current().addressOf(buf));

        buf.flip(); //flip()将缓冲区的界限设置为当前位置，并将当前位置设为0
        System.out.println("flip()切换后："+buf+"---内存地址："+VM.current().addressOf(buf));

        /*buf.mark();
        System.out.println("第1次 mark() 的位置:"+buf);*/

        System.out.println(buf.get(2));
        System.out.println("get(int index)后:"+buf+"---内存地址："+VM.current().addressOf(buf));

        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes,0,2);
        System.out.println(new String(bytes,0,2));
        System.out.println("get(byte[] dst, 0, 2)后:"+buf+"---内存地址："+VM.current().addressOf(buf));

        buf = (ByteBuffer) buf.mark();//mark()对缓冲区的位置标记，通过reset()可以将位置position转移到这个mark的位置
        System.out.println("第2次 mark() 的位置:"+buf+"---内存地址："+VM.current().addressOf(buf));
        buf.get(bytes,2,2);
        System.out.println(new String(bytes,2,2));
        System.out.println("get(byte[] dst, 2, 2)后:"+buf+"---内存地址："+VM.current().addressOf(buf));

        buf.reset();//reset()，将position转移到上一次 mark 的位置
        System.out.println("reset() 后:"+buf+"---内存地址："+VM.current().addressOf(buf));

        if (buf.hasRemaining()) {
            //remaining 返回position 和 limit 之间的元素个数
            System.out.println("剩余可以操作的数据长度："+buf.remaining());
        }

        buf.clear();//clear() 清空缓冲区，变成 allocate 的状态，但是缓冲区的数据依然存在，只是出于"被遗忘"状态
        System.out.println("clear() 后:"+buf+"---内存地址："+VM.current().addressOf(buf));

        buf = (ByteBuffer) buf.limit(4);
        System.out.println("limit(int n) 后:"+buf+"---内存地址："+VM.current().addressOf(buf));

        buf = (ByteBuffer) buf.position(2);
        System.out.println("position(int n) 后:"+buf+"---内存地址："+VM.current().addressOf(buf));

        buf.rewind();
        System.out.println("rewind() 后:"+buf+"---内存地址："+VM.current().addressOf(buf));
    }


    @Test
    public void test3(){
        String str = "abcdef";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println(buf);
        buf.put(str.getBytes());
        System.out.println("put(byte[] src)后："+buf+"---内存地址："+VM.current().addressOf(buf));

        System.out.println("buf.get(6):"+buf.get(6));
        System.out.println("buf.get(6)后："+buf+"---内存地址："+VM.current().addressOf(buf));
        buf.put(str.getBytes()[0]);
        System.out.println("put(byte b)后："+buf+"---内存地址："+VM.current().addressOf(buf));
        System.out.println("buf.get(6):"+buf.get(6));

        buf.position(5);
        System.out.println("buf.position(5)后："+buf+"---内存地址："+VM.current().addressOf(buf));
        System.out.println("buf.get():"+buf.get());
        System.out.println("buf.get()后："+buf+"---内存地址："+VM.current().addressOf(buf));

        byte[] bytes = new byte[5];
        System.out.println("buf.get(byte[] bytes)后："+buf+"---内存地址："+VM.current().addressOf(buf));
        System.out.println(buf.get(bytes));

    }


}
