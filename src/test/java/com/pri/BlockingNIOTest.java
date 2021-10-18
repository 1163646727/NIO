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
public class BlockingNIOTest {

    //客户端测试
    @Test
    public void client() throws IOException {
        // 1.获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
        // 2.分配缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        // 3.读取本地文件，并发送到服务端
        FileChannel inChannel = FileChannel.open(Paths.get("D:/1.txt"), StandardOpenOption.READ);
        while (inChannel.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);//写入SocketChannel，发送到服务端
            buf.clear();
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
        while (sChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }
        // 6.关闭通道
        sChannel.close();
        outChannel.close();
        ssChannel.close();
    }
}
