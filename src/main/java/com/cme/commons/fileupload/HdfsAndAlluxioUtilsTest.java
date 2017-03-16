package com.cme.commons.fileupload;

public class HdfsAndAlluxioUtilsTest {
    //0.创建文件系统信息
    private static HdfsAndAlluxioUtils.FileSystemInfo alluxio = new HdfsAndAlluxioUtils.FileSystemInfo(HdfsAndAlluxioUtils.FileSystemType.ALLUXIO, "qingcheng11", 19998);
    private static HdfsAndAlluxioUtils.FileSystemInfo hdfs = new HdfsAndAlluxioUtils.FileSystemInfo(HdfsAndAlluxioUtils.FileSystemType.HDFS, "qingcheng11", 9000);

    public static void main(String[] args) {
        String path = "/input/README.txt";

        //1.对alluxio进行测试
        boolean ab = HdfsAndAlluxioUtils.exists(alluxio, path);
        System.out.println(ab);

        //2.对hdfs进行测试
        boolean hb = HdfsAndAlluxioUtils.exists(hdfs, path);
        System.out.println(hb);
    }
}
