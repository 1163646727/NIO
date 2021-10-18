package com.pri;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/17 <BR>
 */
public class PathTest {

    @Test
    public void test(){
        Path path = Paths.get("D:/1.txt");
        System.out.println(path);
    }

    @Test
    public void FilesTest() throws IOException {

        /*// 创建目录
        Path directory = Files.createDirectory(Paths.get("D:/test"));
        System.out.println(directory);*/

        // Files.copy(Paths.get("D:/1.txt"),Paths.get("D:/2.txt"));

        Path file = Files.createFile(Paths.get("D:/2.txt"));
        System.out.println(file);

    }
}
