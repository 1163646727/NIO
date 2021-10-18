package com.pri;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * <BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/15 <BR>
 */
public class NoBlockNIOTest {
    @Test
    public void client() throws IOException {
        //1.获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
        //2.切换非阻塞模式
        sChannel.configureBlocking(false);

        //3.分配缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put("你好，NIO！".getBytes());

        //4.发送数据到服务端
        buf.flip();
        sChannel.write(buf);
        buf.clear();
        //5.关闭通道
        sChannel.close();
    }

    @Test
    public void server() throws IOException {
        // 1.获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        // 2.切换非阻塞模式
        ssChannel.configureBlocking(false);
        // 3.绑定监听的端口
        ssChannel.bind(new InetSocketAddress(9898));
        // 4.获取选择器
        Selector selector = Selector.open();
        // 5.将通道注册到选择器上，并指定监听“接收事件”
        SelectionKey register = ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 6.轮询获取选择器上已经“准备就绪”的事件
        while (selector.select() > 0) {
            // 7.获取当前选择器中所有注册的“选择键(已经就绪的事件)”
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                // 8.获取“准备就绪”的事件
                SelectionKey sk = it.next();
                // 9.判断具体什么事情准备就绪
                // 10.若“接收就绪”，获取客户端的连接
                if (sk.isAcceptable()) {
                    SocketChannel sChannel = ssChannel.accept();
                    // 11.切换非阻塞模式
                    sChannel.configureBlocking(false);
                    // 12.将该通道注册到选择器上
                    sChannel.register(selector,SelectionKey.OP_READ);
                }else if (sk.isReadable()) {
                    // 13.获取当前选择器上“读就绪”状态的通道
                    SocketChannel sChannel = (SocketChannel) sk.channel();
                    // 14.读取数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    while (sChannel.read(buf) > 0) {
                        buf.flip();
                        System.out.println("服务端读取到的数据:"+new String(buf.array(),0,buf.limit()));
                        buf.clear();
                    }
                }
                // 15.取消选择键
                it.remove();
            }
        }
    }
}
