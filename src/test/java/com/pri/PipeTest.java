package com.pri;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * <BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/16 <BR>
 */
public class PipeTest {

    @Test
    public void test() throws IOException {
        // 1.获取管道
        Pipe pipe = Pipe.open();
        // 2.将缓冲区的数据写入管道
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put("Hello World!".getBytes());

        buf.flip();
        Pipe.SinkChannel sinkChannel = pipe.sink();
        sinkChannel.write(buf);

        // 3.读取管道中的缓冲区数据
        Pipe.SourceChannel sourceChannel = pipe.source();
        buf.clear();
        sourceChannel.read(buf);
        buf.flip();
        System.out.println("读取管道中的缓冲区数据:"+new String(buf.array(),0, buf.limit()));

        sourceChannel.close();
        sinkChannel.close();
    }

}
