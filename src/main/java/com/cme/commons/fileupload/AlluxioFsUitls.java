package com.cme.commons.fileupload;

import alluxio.AlluxioURI;
import alluxio.client.ReadType;
import alluxio.client.WriteType;
import alluxio.client.file.FileSystem;
import alluxio.client.file.URIStatus;
import alluxio.client.file.options.CreateFileOptions;
import alluxio.client.file.options.OpenFileOptions;
import alluxio.exception.AlluxioException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AlluxioFsUitls {
    //获取文件系统FileSystem
    private static final FileSystem fs = FileSystem.Factory.get();

    /**
     * 此方法用于添加挂载点
     *
     * @param alluxioFilePath 文件路径
     */
    public static void mount(String alluxioFilePath, String underFileSystemPath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI apath = new AlluxioURI(alluxioFilePath);
        AlluxioURI upath = new AlluxioURI(alluxioFilePath);
        try {
            //2.添加挂载点
            if (!fs.exists(apath)) {
                fs.mount(apath, upath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
    }

    /**
     * 此方法用于删除挂载点
     *
     * @param filePath 文件路径
     */
    public static void unmount(String filePath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(filePath);
        try {
            //2.删除挂载点
            if (fs.exists(path)) {
                fs.unmount(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
    }

    /**
     * 此方法用于创建文件，并向文件中输出内容WriteType.ASYNC_THROUGH
     * 数据被同步地写入到Alluxio的Worker，并异步地写入到底层存储系统。处于实验阶段。
     *
     * @param filePath 文件路径
     * @param contents 向文件中输出的内容
     * @return 文件创建，是否成功
     */
    public static boolean createFileMustAsysncThroughWriteTpye(String filePath, List<String> contents) {
        return createFile(filePath, contents,
                CreateFileOptions.defaults().setWriteType(WriteType.ASYNC_THROUGH));
    }

    /**
     * 此方法用于创建文件，并向文件中输出内容WriteType.CACHE_THROUGH
     * 数据被同步地写入到Alluxio的Worker和底层存储系统。
     *
     * @param filePath 文件路径
     * @param contents 向文件中输出的内容
     * @return 文件创建，是否成功
     */
    public static boolean createFileMustCacheThroughWriteTpye(String filePath, List<String> contents) {
        return createFile(filePath, contents,
                CreateFileOptions.defaults().setWriteType(WriteType.CACHE_THROUGH));
    }

    /**
     * 此方法用于创建文件，并向文件中输出内容WriteType.THROUGH
     * 数据被同步地写入到底层存储系统。但不会被写入到Alluxio的Worker。
     *
     * @param filePath 文件路径
     * @param contents 向文件中输出的内容
     * @return 文件创建，是否成功
     */
    public static boolean createFileMustThroughWriteTpye(String filePath, List<String> contents) {
        return createFile(filePath, contents,
                CreateFileOptions.defaults().setWriteType(WriteType.THROUGH));
    }

    /**
     * 此方法用于创建文件，并向文件中输出内容WriteType.MUST_CACHE
     * 数据被同步地写入到Alluxio的Worker。但不会被写入到底层存储系统。这是默认写类型。
     *
     * @param filePath 文件路径
     * @param contents 向文件中输出的内容
     * @return 文件创建，是否成功
     */
    public static boolean createFileMustCacheWriteTpye(String filePath, List<String> contents) {
        return createFile(filePath, contents,
                CreateFileOptions.defaults().setWriteType(WriteType.MUST_CACHE));
    }

    /**
     * 方法用于创建文件，并向文件中输出内容
     *
     * @param filePath 文件路径
     * @param contents 向文件中输出的内容
     * @param options  CreateFileOptions
     * @return 文件创建，是否成功
     */
    public static boolean createFile(String filePath, List<String> contents, CreateFileOptions options) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(filePath);
        BufferedWriter writer = null;
        try {
            //2.打开文件输出流,使用BufferedWriter输出
            if (!fs.exists(path)) {
                writer = new BufferedWriter(new OutputStreamWriter(fs.createFile(path, options)));
                //3.输出文件内容
                for (String line : contents) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            //3.如果文件存在，则表示执行成功
            return fs.exists(path);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        } finally {
            try {
                //4.关闭输入流，释放资源
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 此方法用于读取alluxio文件ReadType.CACHE_PROMOTE
     * 如果读取的数据在Worker上时，该数据被移动到Worker的最高层。如果该数据不在本地Worker的Alluxio存储中，
     * 那么就将一个副本添加到本地Alluxio Worker中，用于每次完整地读取数据块。这是默认的读类型。
     *
     * @param filePath 文件路径
     * @return 文件的内容
     */
    public static List<String> openFilePromoteCacheReadType(String filePath) {
        return openFile(filePath, OpenFileOptions.defaults().setReadType(ReadType.CACHE_PROMOTE));
    }

    /**
     * 此方法用于读取alluxio文件ReadType.NO_CACHE
     * 不会创建副本
     *
     * @param filePath 文件路径
     * @return 文件的内容
     */
    public static List<String> openFileNoCacheReadType(String filePath) {
        return openFile(filePath, OpenFileOptions.defaults().setReadType(ReadType.NO_CACHE));
    }

    /**
     * 此方法用于读取alluxio文件ReadType.CACHE
     * 如果该数据不在本地Worker的Alluxio存储中，那么就将一个副本添加到本地Alluxio Worker中，
     * 用于每次完整地读取数据块。
     *
     * @param filePath 文件路径
     * @return 文件的内容
     */
    public static List<String> openFileCacheReadType(String filePath) {
        return openFile(filePath, OpenFileOptions.defaults().setReadType(ReadType.CACHE));
    }

    /**
     * 此方法用于读取alluxio文件DefalutReadType
     *
     * @param filePath 文件路径
     * @return 文件的内容
     */
    public static List<String> openFileDefalutReadType(String filePath) {
        return openFile(filePath, OpenFileOptions.defaults());
    }


    /**
     * 此方法用于读取alluxio文件
     *
     * @param filePath 文件路径
     * @param options  文件读取选项
     * @return 文件的内容
     */
    public static List<String> openFile(String filePath, OpenFileOptions options) {
        List<String> list = new ArrayList<>();
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(filePath);
        BufferedReader reader = null;
        try {
            //2.打开文件输入流，使用 BufferedReader按行读取
            if (fs.exists(path)) {
                reader = new BufferedReader(new InputStreamReader(fs.openFile(path, options)));
                for (String line = null; (line = reader.readLine()) != null; ) {
                    list.add(line);
                }
                return list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        } finally {
            try {
                //3.关闭输入流，释放资源
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 此方法用于释放alluxio中的文件或路径
     *
     * @param filePath 文件路径
     * @return 释放文件, 是否成功
     */
    public static boolean free(String filePath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(filePath);
        try {
            //2.释放文件
            if (fs.exists(path)) {
                fs.free(path);
            }
            //3.判定文件是否不存在，如果不存在则删除成功！
            return !fs.exists(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 此方法用于删除文件或路径
     *
     * @param filePath 文件路径
     * @return 删除文件, 是否成功
     */
    public static boolean delete(String filePath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(filePath);
        try {
            //2.删除文件
            if (fs.exists(path)) {
                fs.delete(path);
            }
            //3.判定文件是否不存在，如果不存在则删除成功！
            return !fs.exists(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 此方法用于创建文件夹
     *
     * @param dirPath 文件夹路径
     * @return 创建文件夹, 是否成功
     */
    public static boolean createDirectory(String dirPath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(dirPath);
        try {
            //2.创建文件夹
            if (!fs.exists(path)) {
                fs.createDirectory(path);
            }
            //3.再次判定文件夹是否存在，来确定文件夹是否创建成功
            return fs.exists(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 此方法用于获取文件状态信息
     *
     * @param filePath 文件路径
     * @return List<URIStatus>
     */
    public static List<URIStatus> listStatus(String filePath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(filePath);
        try {
            //2.获取文件状态信息
            if (fs.exists(path)) {
                return fs.listStatus(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此方法用于获取文件状态信息
     *
     * @param filePath 文件路径
     * @return URIStatus
     */
    public static URIStatus getStatus(String filePath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(filePath);
        try {
            //2.获取文件状态信息
            if (fs.exists(path)) {
                return fs.getStatus(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此方法用于判定文件是否存在
     *
     * @param filePath 文件路径
     * @return 文件是否存在
     */
    public static boolean exists(String filePath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI path = new AlluxioURI(filePath);
        try {
            //2.获取文件状态信息
            return fs.exists(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 此方法用于重命名文件
     *
     * @param sourcePath 原文件路径
     * @param distPath   目的文件路径
     * @return 重命名是否成功
     */
    public static boolean rename(String sourcePath, String distPath) {
        //1.创建文件路径 AlluxioURI
        AlluxioURI sourcepath = new AlluxioURI(sourcePath);
        AlluxioURI distpath = new AlluxioURI(distPath);
        try {
            //2.重命名操作
            if (fs.exists(sourcepath)) {
                fs.rename(sourcepath, distpath);
            }
            //3.判定目标文件是否存在，来判定重命名是否成功
            return ((fs.exists(distpath))&&(!fs.exists(sourcepath)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlluxioException e) {
            e.printStackTrace();
        }
        return false;
    }
}
