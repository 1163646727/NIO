package com.pri;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * <BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/14 <BR>
 */
@SpringBootTest
public class BlockingNIOTest2_1 {

    //客户端测试
    @Test
    public void client() throws IOException {
        // 1.获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
        // 2.分配缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        // 3.读取本地文件，并发送到服务端
        FileChannel inChannel = FileChannel.open(Paths.get("D:/1.txt"), StandardOpenOption.READ);
        while (true) {
            System.out.println("客户端等待发送数据到服务端...");
            if (inChannel.read(buf) != -1) {
                buf.flip();
                sChannel.write(buf);//写入SocketChannel，发送到服务端
                buf.clear();
                break;
            }else {
                System.out.println("客户端等待发送数据到服务端...");
            }
        }

        //关闭写连接
        sChannel.shutdownOutput();

        //接收服务端的反馈
        int len = 0 ;
        while (true){//阻塞等待服务端的反馈
            System.out.println("客户端等待服务端的反馈");
            if ((len = sChannel.read(buf)) != -1) {
                buf.flip();
                System.out.println("客户端接收到了服务端的反馈："+new String(buf.array(),0, len));
                buf.clear();
                break;
            }else {
                System.out.println("客户端等待服务端的反馈");
            }
        }

        // 4.关闭通道
        inChannel.close();
        sChannel.close();
    }

    //服务端测试
    @Test
    public void server() throws IOException {
        // 1.获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        // 2.绑定连接
        ssChannel.bind(new InetSocketAddress(9898));
        // 3.分配缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        // 4.获取客户端连接的通道
        SocketChannel sChannel = ssChannel.accept();
        // 5.接收客户端的数据，并保存到本地
        FileChannel outChannel = FileChannel.open(Paths.get("D:/2.txt"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        while (true) { //阻塞等待客户端有数据发送过来
            System.out.println("服务端 等待着客户端有数据发送过来...");
            if (sChannel.read(buf) != -1) {
                buf.flip();
                System.out.println("服务端 接收到了客户端有数据："+new String(buf.array(),0 ,buf.limit()));
                outChannel.write(buf);
                buf.clear();
                break;
            }else {
                System.out.println("服务端 等待着客户端有数据发送过来...");
            }
        }

        //发送反馈给客户端
        buf.put("服务端接收数据成功！".getBytes());
        buf.flip();
        sChannel.write(buf);
        buf.clear();

        // 6.关闭通道
        sChannel.close();
        outChannel.close();
        ssChannel.close();
    }
}
