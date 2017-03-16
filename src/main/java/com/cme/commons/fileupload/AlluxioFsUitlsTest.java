package com.cme.commons.fileupload;


import java.util.List;

/**
 * Created by ocean on 20/12/2016.
 */
public class AlluxioFsUitlsTest {
    public static void main(String[] args) {
        List<String> list=AlluxioFsUitls.openFileDefalutReadType("/alluxiotest/README.MD");
        for (String s:list){
            System.out.println(s);
        }

    }
}
