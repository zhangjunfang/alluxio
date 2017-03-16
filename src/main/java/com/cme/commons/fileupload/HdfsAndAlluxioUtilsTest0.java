package com.cme.commons.fileupload;

public class HdfsAndAlluxioUtilsTest0 {
//    private static HdfsAndAlluxioUtils.FileSystemInfo alluxio = new HdfsAndAlluxioUtils.FileSystemInfo(HdfsAndAlluxioUtils.FileSystemType.ALLUXIO, "qingcheng11", 19998);
//    private static HdfsAndAlluxioUtils.FileSystemInfo hdfs = new HdfsAndAlluxioUtils.FileSystemInfo(HdfsAndAlluxioUtils.FileSystemType.HDFS, "qingcheng11", 9000);

//    public static void main(String[] args) {
//        String t="/input/spark";
//        String l="/input/link";
//        HdfsAndAlluxioUtils.createSymlink(hdfs,t,l,true);
//    }

//    public static void main(String[] args) {
//        boolean b = HdfsAndAlluxioUtils.supportsSymlinks(alluxio);
//        System.out.println(b);
//    }


//    public static void main(String[] args) {
//        String path = "/input/spark/";
//        RemoteIterator<Path> paths = HdfsAndAlluxioUtils.listCorruptFileBlocks(hdfs, path);
//        System.out.println(paths);
//    }


//    public static void main(String[] args) {
//        String path = "/input/spark/";
//        String oname = "testsnape";
//        String nname = "testsnapennn";
//        HdfsAndAlluxioUtils.renameSnapshot(hdfs, path, oname, nname);
//    }

//    public static void main(String[] args) {
//        String path = "/input/kv2";
//        String oname = "ocean";
//        String nname = "root";
//        HdfsAndAlluxioUtils.setOwner(hdfs, path, oname, nname);
//    }


//    public static void main(String[] args) {
//        String path = "/input/spark/";
//        String name = "testsnapennn";
//        HdfsAndAlluxioUtils.deleteSnapshot(hdfs, path, name);
//    }

//    public static void main(String[] args) {
//        String path = "/input/spark/";
//        String name = "testsnape";
//        Path path1 = HdfsAndAlluxioUtils.createSnapshot(hdfs, path, name);
//        System.out.println(path1);
//
//    }

//    public static void main(String[] args) {
//        long used1 = HdfsAndAlluxioUtils.getUsed(alluxio);
//        System.out.println(used1);
//        long used2 = HdfsAndAlluxioUtils.getUsed(hdfs);
//        System.out.println(used2);
//    }


//    public static void main(String[] args) {
//        java.net.URI uri = HdfsAndAlluxioUtils.getUri(alluxio);
//        System.out.println(uri);
//    }


//    public static void main(String[] args) {
//        Path path = HdfsAndAlluxioUtils.getWorkingDirectory(hdfs);
//        System.out.println(path);
//    }


//    public static void main(String[] args) {
//        Path path = HdfsAndAlluxioUtils.getWorkingDirectory(hdfs);
//        System.out.println(path);
//    }


//    public static void main(String[] args) {
//        Path path = HdfsAndAlluxioUtils.getHomeDirectory(alluxio);
//        System.out.println(path);
//    }


//    public static void main(String[] args) {
//        String s1 = "/input/";
//        FileChecksum checksum = HdfsAndAlluxioUtils.getFileChecksum(hdfs, s1);
//        System.out.println(checksum);
//    }

//    public static void main(String[] args) {
//        String s1 = "/input/";
//        ContentSummary summary = HdfsAndAlluxioUtils.getContentSummary(alluxio, s1);
//        System.out.println(summary);
//    }


//    public static void main(String[] args) {
//        String s1 = "/input/kv8";
//        String d1 = "/Users/ocean/Documents/F/code/idea/git/simple-alluxio/";
//        HdfsAndAlluxioUtils.moveToLocalFile(alluxio, s1, d1);
//    }


//    public static void main(String[] args) {
//        String s1 = "/input";
//        final List<FileStatus> statuses = HdfsAndAlluxioUtils.listStatus(alluxio, s1);
//        System.out.println(statuses);
//
//        final List<FileStatus> statuses2 = HdfsAndAlluxioUtils.listStatus(hdfs, s1);
//        System.out.println(statuses2);
//    }


//    public static void main(String[] args) {
//        String s1 = "/input/README.txt";
//        HdfsAndAlluxioUtils.open(alluxio, s1);
//
//        HdfsAndAlluxioUtils.open(hdfs, s1);
//    }

//    public static void main(String[] args) {
//        String s1 = "/Users/ocean/Documents/F/code/idea/git/";
//        String d1 = "/input/kv8";
//        HdfsAndAlluxioUtils.copyFromLocalFile(alluxio, s1, d1);
//        HdfsAndAlluxioUtils.delete(alluxio, d1);
//    }


//    public static void main(String[] args) {
//        String patho = "/input/1.txt";
//        HdfsAndAlluxioUtils.create(alluxio, patho, true, "1");
//        String pathn = "/input/2.txt";
//        HdfsAndAlluxioUtils.create(hdfs, pathn, true, "2");
//    }


//    public static void main(String[] args) {
//        String patho = "/input/c.txt";
//        boolean ab = HdfsAndAlluxioUtils.createNewFile(alluxio, patho);
//        System.out.println(ab);
//        String pathn = "/input/b.cvs";
//        boolean hb = HdfsAndAlluxioUtils.createNewFile(hdfs, pathn);
//        System.out.println(hb);
//    }

//    public static void main(String[] args) {
//        String patho = "/input/";
//        boolean ab = HdfsAndAlluxioUtils.isFile(alluxio, patho);
//        System.out.println(ab);
//        String pathn = "/input/";
//        boolean hb = HdfsAndAlluxioUtils.isFile(hdfs, pathn);
//        System.out.println(hb);
//    }

//    public static void main(String[] args) {
//        String patho = "/input/";
//        boolean ab = HdfsAndAlluxioUtils.isDirectory(alluxio, patho);
//        System.out.println(ab);
//        String pathn = "/input/";
//        boolean hb = HdfsAndAlluxioUtils.isDirectory(hdfs, pathn);
//        System.out.println(hb);
//    }

//    public static void main(String[] args) {
//        String patho = "/input/c.txt";
//        boolean ab = HdfsAndAlluxioUtils.delete(alluxio, patho);
//        System.out.println(ab);
//        String pathn = "/input/b.cvs";
//        boolean hb = HdfsAndAlluxioUtils.delete(hdfs, pathn);
//        System.out.println(hb);
//    }


//    public static void main(String[] args) {
//        String patho = "/input/test1";
//        boolean ab = HdfsAndAlluxioUtils.mkdirs(alluxio, patho);
//        System.out.println(ab);
//        String pathn = "/input/test2";
//        boolean hb = HdfsAndAlluxioUtils.mkdirs(hdfs, pathn);
//        System.out.println(hb);
//    }


//    public static void main(String[] args) {
//        String patho = "/input/README2.txt";
//        String pathn = "/input/README.txt";
//        boolean ab = HdfsAndAlluxioUtils.rename(alluxio, patho,pathn);
//        System.out.println(ab);
//        boolean hb = HdfsAndAlluxioUtils.rename(hdfs, patho, pathn);
//        System.out.println(hb);
//    }


//    public static void main(String[] args) {
//        String path = "/input/README.txt";
//        boolean ab = HdfsAndAlluxioUtils.exists(alluxio, path);
//        System.out.println(ab);
//        boolean hb = HdfsAndAlluxioUtils.exists(hdfs, path);
//        System.out.println(hb);
//    }

}
