package com.pri;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 通道测试类<BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/12 <BR>
 */
@SpringBootTest
public class ChannelTest {

    //利用通道完成文件的复制(非直接缓冲区)
    @Test
    public void test(){
        Long start = System.currentTimeMillis();

        //文件输入、输出流
        FileInputStream fis = null;
        FileOutputStream fos = null;

        //通道
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            fis = new FileInputStream("D:/1.iso");
            fos = new FileOutputStream("D:/2.iso");

            //获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);
            //讲通道中的数据存入缓冲区中
            while (inChannel.read(buf) != -1){
                buf.flip();
                //将缓冲区中的数据写入到通道中
                outChannel.write(buf);
                buf.clear();//清空缓冲区
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                fis.close();
                fos.close();
                inChannel.close();
                outChannel.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Long end = System.currentTimeMillis();
        System.out.println("非直接缓冲区耗时："+(end - start));// 37240 38933 34509  35575
    }

    //使用直接缓冲区完成文件复制(内存映射文件)
    @Test
    public void test2() throws IOException {//772
        Long start = System.currentTimeMillis();
        //创建通道
        FileChannel inChannel = FileChannel.open(Paths.get("D:/1.iso"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("D:/3.iso"),StandardOpenOption.WRITE,
                StandardOpenOption.READ,StandardOpenOption.CREATE);

        //创建内存映射文件，注意：MappedByteBuffer的容量不能超过2GB,否则抛出异常
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY,0,inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE,0,inChannel.size());

        //直接对缓冲区进行数据的读写操作
        byte[] bytes = new byte[inMappedBuf.limit()];
        inMappedBuf.get(bytes);//将缓冲区的数据读到 字节数组 bytes 中
        outMappedBuf.put(bytes);

        inChannel.close();
        outChannel.close();

        Long end = System.currentTimeMillis();
        System.out.println("非直接缓冲区耗时："+(end - start));
    }

    //通道之间的数据传输(直接缓冲区)
    @Test
    public void test3() throws IOException {
        Long start = System.currentTimeMillis();
        //创建通道
        FileChannel inChannel = FileChannel.open(Paths.get("D:/1.iso"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("D:/3.iso"),StandardOpenOption.WRITE,
                StandardOpenOption.READ,StandardOpenOption.CREATE);
        //通道之间进行数据传输，inChannel 传输到 outChannel

        //第一种：transferTo 元数据不能超过2GB,否则会丢失精度，内部有个操作((int)Math.min(count, 2147483647L))
        // inChannel.transferTo(0,inChannel.size(),outChannel);

        //第二种：transferFrom 不会丢失精度
        outChannel.transferFrom(inChannel,0,inChannel.size());

        inChannel.close();
        outChannel.close();

        Long end = System.currentTimeMillis();
        System.out.println("非直接缓冲区耗时："+(end - start));//4358 4298 1872    10318
    }

    //分散和聚集
    @Test
    public void test4() throws IOException {
        RandomAccessFile rafIn = new RandomAccessFile("D:/1.exe","rw");
        RandomAccessFile rafOut = new RandomAccessFile("D:/2.exe","rw");

        //获取通道
        FileChannel inChannel = rafIn.getChannel();
        FileChannel outChannel = rafOut.getChannel();

        //创建指定大小的缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);
        ByteBuffer[] bufs = {buf1,buf2};

        /*//动态分配缓冲区数量和容量，受目标文件大小的影响，测试多次遇到异常
        int bufSize = 15;
        ByteBuffer[] bufs = new ByteBuffer[bufSize];
        for (int i=0;i<bufSize;i++) {
            bufs[i] = ByteBuffer.allocate((int) (inChannel.size()/bufSize));
        }*/

        //分散读取
        inChannel.read(bufs);
        for (ByteBuffer item:bufs) {
            item.flip();
        }
        //聚集写入
        outChannel.write(bufs);

        inChannel.close();
        outChannel.close();
    }
}
