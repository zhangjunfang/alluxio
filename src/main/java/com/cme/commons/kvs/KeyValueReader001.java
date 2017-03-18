package com.cme.commons.kvs;

import alluxio.AlluxioURI;
//import alluxio.client.keyvalue.KeyValueStoreReader;
//import alluxio.client.keyvalue.KeyValueSystem;
import alluxio.exception.AlluxioException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class KeyValueReader001 {
	// public static void main(String[] args) throws Exception, AlluxioException
	// {
	// //1.创建键值对系统实例
	// KeyValueSystem kvs = KeyValueSystem.Factory.create();
	//
	// //2.开启键值对reader
	// KeyValueStoreReader reader = kvs.openStore(new
	// AlluxioURI("/alluxiotest/kvstore"));
	//
	// //3.根据键读取值,byte[]类型数据
	// byte[] test1 = reader.get("100".getBytes());
	// String test1Str = new String(test1);
	// System.out.println(test1Str);
	//
	// //4.根据键读取值,ByteBuffer类型数据
	// ByteBuffer test2 = reader.get(ByteBuffer.wrap("200".getBytes()));
	// Charset charset = Charset.forName("UTF-8");
	// CharsetDecoder decoder = charset.newDecoder();
	// CharBuffer charBuffer = decoder.decode(test2.asReadOnlyBuffer());
	// String test2Str = charBuffer.toString();
	// System.out.println(test2Str);
	//
	// //5.获取键值对存储的实际大小
	// System.out.println("键值对存储的大小=" + reader.size());
	//
	// //6.关闭键值对reader
	// reader.close();
	// }
}
