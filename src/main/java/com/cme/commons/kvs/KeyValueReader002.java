package com.cme.commons.kvs;

//import alluxio.AlluxioURI;
//import alluxio.client.keyvalue.KeyValueIterator;
//import alluxio.client.keyvalue.KeyValuePair;
//import alluxio.client.keyvalue.KeyValueStoreReader;
//import alluxio.client.keyvalue.KeyValueSystem;
//import alluxio.exception.AlluxioException;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import alluxio.AlluxioURI;
import alluxio.client.keyvalue.KeyValueIterator;
import alluxio.client.keyvalue.KeyValuePair;
import alluxio.client.keyvalue.KeyValueStoreReader;
import alluxio.client.keyvalue.KeyValueSystem;
import alluxio.exception.AlluxioException;

public class KeyValueReader002 {
	public static void main(String[] args) throws Exception, AlluxioException {
		// 1.创建键值对系统实例
		KeyValueSystem kvs = KeyValueSystem.Factory.create();

		// 2.开启键值对reader
		KeyValueStoreReader reader = kvs.openStore(new AlluxioURI("/alluxiotest/kvstore"));

		// 3.通过迭代器遍历存储中的键值对
		// 3.1创建解码器
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();

		// 3.2遍历并解码
		KeyValueIterator iterator = reader.iterator();
		while (iterator.hasNext()) {
			KeyValuePair pair = iterator.next();
			String key = decoder.decode(pair.getKey().asReadOnlyBuffer()).toString();
			String value = decoder.decode(pair.getValue().asReadOnlyBuffer()).toString();
			System.out.println("key=" + key + ",value=" + value);
		}
		// 4.关闭键值对reader
		reader.close();
	}
}
