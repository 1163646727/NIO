package com.pri;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件锁 <BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/16 <BR>
 */
public class FileLockTest {
    //共享锁测试
    @Test
    public void test() throws IOException {

        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put("Hello World!".getBytes());
        FileChannel fChannel = FileChannel.open(Paths.get("D:/1.txt"),
                StandardOpenOption.WRITE,StandardOpenOption.APPEND);
        fChannel.position(fChannel.size() - 1);
        // 加锁 排他锁
        // FileLock fileLock = fChannel.lock();
        // 共享锁，其他进程只能读，不能写
        FileLock fileLock = fChannel.lock(0L,Long.MAX_VALUE,true);
        System.out.println("fileLock是否是共享锁："+fileLock.isShared());

        // 写文件
        fChannel.write(buf);
        fChannel.close();

        // 读文件
        FileReader fileReader = new FileReader("D:/1.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String st = bufferedReader.readLine();
        while (st != null) {
            System.out.println(st);
            st = bufferedReader.readLine();
        }

        fileReader.close();
        bufferedReader.close();
    }


}
