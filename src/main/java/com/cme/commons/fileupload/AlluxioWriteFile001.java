package com.cme.commons.fileupload;

import alluxio.AlluxioURI;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;
import alluxio.exception.AlluxioException;

import java.io.IOException;

public class AlluxioWriteFile001 {
	public static void main(String[] args) {
		// 1.获取文件系统FileSystem
		FileSystem fs = FileSystem.Factory.get();
		// 2.创建文件路径 AlluxioURI
		AlluxioURI path = new AlluxioURI("/files/writetest001.txt");
		FileOutStream out = null;
		try {
			// file is not exist
			if (!fs.exists(path)) {
				// 3.打开文件输出流
				out = fs.createFile(path);
				// 4.输出文件内容
				out.write("this is test ".getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AlluxioException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != out) {
					// 5.关闭输入流，释放资源
					out.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
