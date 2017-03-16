package com.cme.commons.fileupload;

import alluxio.AlluxioURI;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;
import alluxio.exception.AlluxioException;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class AlluxioWriteFile002 {
    public static void main(String[] args) {
        //1.获取文件系统FileSystem
        FileSystem fs = FileSystem.Factory.get();
        //2.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI("/alluxiotest/writetest002.txt");
        FileOutStream out = null;
        try {
            //3.打开文件输出流,使用BufferedOutputStream输出
            out = fs.createFile(path);
            BufferedOutputStream bo = new BufferedOutputStream(out);
            //4.输出文件内容
            bo.write("this is test ".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        } finally {
            try {
                //5.关闭输入流，释放资源
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
