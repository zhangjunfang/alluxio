package com.cme.commons.fileupload;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.IOUtils;

/**
 * Created by ocean on 22/12/2016.
 */
public class HdfsAndAlluxioUtils_update {

    private static final String NOT_EXEXTS_EXECEPTION_MSG = "Path is not exist:";
    private static final String NOT_DIR_EXECEPTION_MSG = "Path is not a directory:";
    private static final String NOT_FILE_EXECEPTION_MSG = "path is not a file:";
    private static String PATH_DELIMER = "/";

    /**
     * 此方法用于move文件或文件夹到分布式系统
     *
     * @param fileSystemInfo 文件系统信息
     * @param src            分布式系统路径
     * @param dist           本地路径
     */
    public static void moveToLocalFile(FileSystemInfo fileSystemInfo, String src, String dist) {
        copyOrMoveToLocalFile(fileSystemInfo, src, dist, true, true);
    }

    /**
     * 此方法用于copy文件或文件夹到分布式系统
     *
     * @param fileSystemInfo 文件系统信息
     * @param src            分布式系统路径
     * @param dist           本地路径
     */
    public static void copyToLocalFile(FileSystemInfo fileSystemInfo, String src, String dist) {
        copyOrMoveToLocalFile(fileSystemInfo, src, dist, true, false);
    }

    /**
     * 此方法用于copy或remove文件或文件夹到分布式系统
     *
     * @param fileSystemInfo 文件系统信息
     * @param src            分布式系统路径
     * @param dist           本地路径
     * @param deleteCrc      是否删除本地生成的crc检验文件
     * @param deleteSrcDir   是否删除分布式系统上的文件
     */
    public static void copyOrMoveToLocalFile(FileSystemInfo fileSystemInfo, String src, String dist, boolean deleteCrc, boolean deleteSrcDir) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path hdfsPath = new Path(src);
        try {
            //文件不存在的异常返回
            pathNotExistCheck(src, fs, hdfsPath);
            //文件存在的情况下进行download操作
            FileStatus fileStatus = fs.getFileStatus(hdfsPath);
            if (fileStatus.isDirectory()) {
                //如果是dir
                dist = convertToPath(dist) + fileStatus.getPath().getName();
                File localFileDir = new File(dist);
                if (!localFileDir.exists()) {
                    localFileDir.mkdirs();
                }
                //遍历hdfs的dir中的所有文件
                FileStatus contents[] = fs.listStatus(hdfsPath);
                for (int i = 0; i < contents.length; i++) {
                    copyOrMoveToLocalFile(fileSystemInfo, contents[i].getPath().toString(), dist, deleteCrc, deleteSrcDir);
                }
            } else {
                //如果是file
                Path localPathOrFilePath = new Path(dist);
                fs.copyToLocalFile(hdfsPath, localPathOrFilePath);
                //删除local生成的crc校验文件
                if (deleteCrc) {
                    String crcFileName = "." + hdfsPath.getName() + ".crc";
                    String crcFileAbsolutePath = convertToPath(dist) + crcFileName;
                    File crcFile = new File(crcFileAbsolutePath);
                    crcFile.deleteOnExit();
                }
            }
            if (deleteSrcDir) {
                fs.delete(hdfsPath, true);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }

    }

    /**
     * 此方法用于move本地文件或文件夹到分布式系统
     * <p>
     * alluxio是否能穿透到hdfs要根据它的配置情况而定
     *
     * @param fileSystemInfo 文件系统信息
     * @param src            本地路径
     * @param dist           分布式系统路径
     */
    public static void moveFromLocalFile(FileSystemInfo fileSystemInfo, String src, String dist) {
        copyOrMoveFromLocalFile(fileSystemInfo, src, dist, true, true);
    }

    /**
     * 此方法用于copy本地文件或文件夹到分布式系统
     * <p>
     * alluxio是否能穿透到hdfs要根据它的配置情况而定
     *
     * @param fileSystemInfo 文件系统信息
     * @param src            本地路径
     * @param dist           分布式系统路径
     */
    public static void copyFromLocalFile(FileSystemInfo fileSystemInfo, String src, String dist) {
        copyOrMoveFromLocalFile(fileSystemInfo, src, dist, true, false);
    }

    /**
     * 此方法用于copy或move本地文件或文件夹到分布式系统
     * <p>
     * alluxio是否能穿透到hdfs要根据它的配置情况而定
     *
     * @param fileSystemInfo 文件系统信息
     * @param src            本地路径
     * @param dist           分布式系统路径
     * @param delSrc         是否删除本地文件
     */
    public static void copyOrMoveFromLocalFile(FileSystemInfo fileSystemInfo, String src, String dist, boolean overwrite, boolean delSrc) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        //local
        Path localDirOrFilePath = new Path(src);
        //hdfs
        Path remoteDirPath = new Path(dist);
        try {
            File localFile = new File(src);
            //如果本地文件不存在，直接返回
            if (!localFile.exists()) {
                return;
            }
            if (isPath(dist)) {
                if (!fs.exists(remoteDirPath)) {
                    fs.mkdirs(remoteDirPath);
                }
            }
            if (localFile.isDirectory()) {
                //dir
                dist = convertToPath(dist) + localFile.getName();
                remoteDirPath = new Path(dist);
                if (!fs.exists(remoteDirPath)) {
                    fs.mkdirs(remoteDirPath);
                }
                //遍历本地dir中的所有文件
                File[] listFiles = localFile.listFiles();
                for (File f : listFiles) {
                    copyOrMoveFromLocalFile(fileSystemInfo, f.getAbsolutePath(), convertToPath(dist), overwrite, delSrc);
                }
            } else {
                //file
                fs.copyFromLocalFile(delSrc, overwrite, localDirOrFilePath, remoteDirPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于创建新文件，并向文件写入内容
     * <p>
     * alluxio是否能穿透到hdfs要根据它的配置情况而定
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @param overWrite      文件存在是否覆盖
     * @param fileContent    写入文件的内容
     */
    public static void create(FileSystemInfo fileSystemInfo, String path, boolean overWrite, String fileContent) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        FSDataOutputStream output = null;
        try {
            byte[] bytes = fileContent.getBytes();
            //创建文件
            output = fs.create(uri, overWrite);
            //写入文件
            output.write(bytes);
            //刷出缓存
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于创建新文件
     * <p>
     * alluxio是否能穿透到hdfs要根据它的配置情况而定
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return 创建新文件是否成功
     */
    public static boolean createNewFile(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            if (!fs.exists(uri)) {
                return fs.createNewFile(uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return false;
    }

    /**
     * 此方法用于判断文件是否是file
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return 是否是file
     */
    public static boolean isFile(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            return fs.isFile(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return false;
    }

    /**
     * 此方法用于判断文件是否是dir
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return 是否是dir
     */
    public static boolean isDirectory(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            return fs.isDirectory(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return false;
    }

    /**
     * 此方法用于删除文件
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return 删除文件是否成功
     */
    public static boolean delete(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            if (fs.exists(uri)) {
                return fs.delete(uri, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return false;
    }

    /**
     * 此方法用于查看文件中损坏的块
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return 查看文件中损坏的块
     */
    public static RemoteIterator<Path> listCorruptFileBlocks(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            return fs.listCorruptFileBlocks(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return null;
    }

    /**
     * 此方法用于查看文件中损坏的块
     *
     * @param fileSystemInfo 文件系统信息
     * @param target         目标文件路径
     * @param link           link文件路径
     * @param createParent   是否创建父目录
     */
    public static void createSymlink(FileSystemInfo fileSystemInfo, String target, String link, boolean createParent) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path urit = new Path(target);
        Path uril = new Path(link);
        try {
            pathNotExistCheck(target, fs, urit);
            fs.createSymlink(urit, uril, createParent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于判断是否支持Symlink
     *
     * @param fileSystemInfo 文件系统信息
     * @return 是否支持Symlink
     */
    public static boolean supportsSymlinks(FileSystemInfo fileSystemInfo) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        try {
            return fs.supportsSymlinks();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于创建文件夹
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return 创建文件夹是否成功
     */
    public static boolean mkdirs(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            if (!fs.exists(uri)) {
                return fs.mkdirs(uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return false;
    }

    /**
     * 此方法用于创建文件快照
     * 需要开启快照支持hdfs dfsadmin -allowSnapshot /path/to/snapshot
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @param snapshotName   快照名称
     * @return 快照路径
     */
    public static Path createSnapshot(FileSystemInfo fileSystemInfo, String path, String snapshotName) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            pathNotDirectoryCheck(path, fs, uri);
            return fs.createSnapshot(uri, snapshotName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return null;
    }


    /**
     * 此方法用于重命名文件快照
     *
     * @param fileSystemInfo  文件系统信息
     * @param path            文件路径
     * @param snapshotOldName 旧快照名称
     * @param snapshotNewName 新快照名称
     */
    public static void renameSnapshot(FileSystemInfo fileSystemInfo, String path, String snapshotOldName, String snapshotNewName) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            pathNotDirectoryCheck(path, fs, uri);
            fs.renameSnapshot(uri, snapshotOldName, snapshotNewName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于删除文件快照
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @param snapshotName   快照名称
     */
    public static void deleteSnapshot(FileSystemInfo fileSystemInfo, String path, String snapshotName) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            pathNotDirectoryCheck(path, fs, uri);
            fs.deleteSnapshot(uri, snapshotName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于设置文件的拥有者
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @param user           用户
     * @param group          用户组
     */
    public static void setOwner(FileSystemInfo fileSystemInfo, String path, String user, String group) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            fs.setOwner(uri, user, group);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于重命名文件
     *
     * @param fileSystemInfo 文件系统信息
     * @param patho          原文件路径
     * @param pathn          目标文件路径
     * @return 文件是否重命名成功
     */
    public static boolean rename(FileSystemInfo fileSystemInfo, String patho, String pathn) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path urio = new Path(patho);
        Path urin = new Path(pathn);
        try {
            //重命名
            if (fs.exists(urio)) {
                return fs.rename(urio, urin);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return false;
    }

    /**
     * 此方法用于读取文件内容,到标准输出流
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     */
    public static void open(FileSystemInfo fileSystemInfo, String path) {
        open(fileSystemInfo, path, System.out, 4096, true);
    }

    /**
     * 此方法用于读取文件内容,到输出流
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @param outputStream   输出流
     */
    public static void open(FileSystemInfo fileSystemInfo, String path, OutputStream outputStream) {
        open(fileSystemInfo, path, outputStream, 4096, true);
    }

    /**
     * 此方法用于读取文件内容，到输出流
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @param outputStream   输出流
     * @param bufferSize     输出缓存大小
     * @param close          文件输出后是否关闭输出流
     */
    public static void open(FileSystemInfo fileSystemInfo, String path, OutputStream outputStream, int bufferSize, boolean close) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            //1.检查文件是否合法
            pathNotExistCheck(path, fs, uri);
            pathNotFileCheck(path, fs, uri);
            //2.读取文件内容
            IOUtils.copyBytes(fs.open(uri), outputStream, bufferSize, close);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于判断是否存在文件
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return 文件是否存在
     */
    public static boolean exists(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            return fs.exists(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return false;
    }

    /**
     * 此方法用于获取文件系统的使用容量
     *
     * @param fileSystemInfo 文件系统信息
     * @return 文件系统的使用容量
     */
    public static long getUsed(FileSystemInfo fileSystemInfo) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        try {
            return fs.getUsed();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return -1;
    }

    /**
     * 此方法用于获取文件的 Uri
     *
     * @param fileSystemInfo 文件系统信息
     * @return Uri
     */
    public static URI getUri(FileSystemInfo fileSystemInfo) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        try {
            return fs.getUri();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于获取文件的 ContentSummary
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return ContentSummary
     */
    public static ContentSummary getContentSummary(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            return fs.getContentSummary(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return null;
    }

    /**
     * 此方法用于获取文件的FileChecksum
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return FileChecksum
     */
    public static FileChecksum getFileChecksum(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            return fs.getFileChecksum(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return null;
    }

    /**
     * 此方法用于获取文件的FsStatus
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return FsStatus
     */
    public static FsStatus getStatus(FileSystemInfo fileSystemInfo, String path) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            pathNotExistCheck(path, fs, uri);
            return fs.getStatus(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return null;
    }


    /**
     * 此方法用于获取文件系统的HomeDirectory
     *
     * @param fileSystemInfo 文件系统信息
     * @return 文件系统的HomeDirectory
     */
    public static Path getHomeDirectory(FileSystemInfo fileSystemInfo) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        try {
            return fs.getHomeDirectory();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于获取文件系统的HomeDirectory
     *
     * @param fileSystemInfo 文件系统信息
     * @return 文件系统的WorkingDirectory
     */
    public static Path getWorkingDirectory(FileSystemInfo fileSystemInfo) {
        FileSystem fs = getFileSystem(fileSystemInfo);
        try {
            return fs.getWorkingDirectory();
        } finally {
            closeFileSystem(fs);
        }
    }

    /**
     * 此方法用于获取文件信息
     *
     * @param fileSystemInfo 文件系统信息
     * @param path           文件路径
     * @return 文件是否存在
     */
    public static List<FileStatus> listStatus(FileSystemInfo fileSystemInfo, String path) {
        List<FileStatus> info = new ArrayList<FileStatus>();
        FileSystem fs = getFileSystem(fileSystemInfo);
        Path uri = new Path(path);
        try {
            FileStatus[] list = fs.listStatus(uri);
            for (FileStatus f : list) {
                info.add(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileSystem(fs);
        }
        return info;
    }
    /**
     * 此方法用于，获取FileSystem
     *
     * @param fileSystemInfo 文件系统信息
     * @return FileSystem
     */
    public static FileSystem getFileSystem(FileSystemInfo fileSystemInfo) {
        Configuration conf = getConfigration(fileSystemInfo);
        try {
            return FileSystem.get(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此方法用户获取配置信息
     *
     * @param fileSystemInfo 文件系统信息
     * @return Configuration
     */
    public static Configuration getConfigration(FileSystemInfo fileSystemInfo) {
        System.setProperty("HADOOP_USER_NAME", "root");
        System.setProperty("alluxio.security.login.username", "root");
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", fileSystemInfo.getFileSystemType().toLowerCase().trim() + "://" + fileSystemInfo.getMaster().trim() + ":" + fileSystemInfo.getPort());
        return conf;
    }

    /**
     * 关闭FileSystem
     *
     * @param fs FileSystem
     */
    private static void closeFileSystem(FileSystem fs) {
        try {
            if (fs != null) {
                fs.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 此方法用于将string转化为path
     *
     * @param pathOrFile pathOrFile
     * @return path
     */
    private static String convertToPath(String pathOrFile) {
        pathOrFile = pathOrFile.trim();
        if (!pathOrFile.endsWith(PATH_DELIMER)) {
            pathOrFile = pathOrFile + PATH_DELIMER;
        }
        return pathOrFile;
    }

    /**
     * 此方法用于判断string是否为path
     *
     * @param pathOrFile pathOrFile
     * @return 是否为path
     */
    private static boolean isPath(String pathOrFile) {
        return pathOrFile.trim().endsWith(PATH_DELIMER);
    }

    /**
     * 此方法用于检测分布式系统中文件是否存在
     *
     * @param uri  uri
     * @param fs   FileSystem
     * @param path path
     * @throws IOException
     */
    private static void pathNotExistCheck(String uri, FileSystem fs, Path path) throws IOException {
        if (!fs.exists(path)) {
            throw new RuntimeException(NOT_EXEXTS_EXECEPTION_MSG + uri);
        }
    }

    /**
     * 此方法用于检测分布式系统中是否是文件夹
     *
     * @param uri  uri
     * @param fs   FileSystem
     * @param path path
     * @throws IOException
     */
    private static void pathNotDirectoryCheck(String uri, FileSystem fs, Path path) throws IOException {
        if (!fs.isDirectory(path)) {
            throw new RuntimeException(NOT_DIR_EXECEPTION_MSG + uri);
        }
    }

    /**
     * 此方法用于检测分布式系统中是否是文件
     *
     * @param uri  uri
     * @param fs   FileSystem
     * @param path path
     * @throws IOException
     */
    private static void pathNotFileCheck(String uri, FileSystem fs, Path path) throws IOException {
        if (!fs.isFile(path)) {
            throw new RuntimeException(NOT_FILE_EXECEPTION_MSG + uri);
        }
    }

    /**
     * 用于限定文件系统类型
     */
    public interface FileSystemType {
        String ALLUXIO = "alluxio";
        String HDFS = "hdfs";
    }

    /**
     * 用于封装文件系统信息
     */
    public static class FileSystemInfo {
        //文件系统协议信息
        private String FileSystemType;
        //文件系统master节点的IP或name
        private String master;
        //文件系统master节点的端口
        private int port;

        public FileSystemInfo(String fileSystemType, String master, int port) {
            FileSystemType = fileSystemType;
            this.master = master;
            this.port = port;
        }

        public String getFileSystemType() {
            return FileSystemType;
        }

        public String getMaster() {
            return master;
        }

        public int getPort() {
            return port;
        }
    }

}
