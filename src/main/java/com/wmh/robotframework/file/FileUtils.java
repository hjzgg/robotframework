package com.wmh.robotframework.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static ReadSign readFile(String filePath, long pos) throws IOException {
        final int length = 1024;//一次 读取 字节数
        int total = 0;//读取的总字节数
        StringBuilder sb = new StringBuilder();//读取内容
        RandomAccessFile raf = new RandomAccessFile(filePath, "r");
        raf.seek(pos);
        byte[] content = new byte[length];
        while (true) {
            int ll = raf.read(content);
            //文件已经读取完毕
            if (ll < 0) {
                break;
            }
            total += ll;
            sb.append(new String(content, StandardCharsets.UTF_8));
        }

        return new ReadSign(sb.toString(), pos + total);
    }
}
