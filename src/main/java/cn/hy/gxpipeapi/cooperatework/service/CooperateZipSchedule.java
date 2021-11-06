package cn.hy.gxpipeapi.cooperatework.service;


import cn.hy.gxpipeapi.util.FtpUtil;
import cn.hy.gxpipeapi.util.Md5Util;
import cn.hy.gxpipeapi.util.ZipUtil;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CooperateZipSchedule {

    public static void getCooperateZip() {
//        FTPFile[] files = Optional.ofNullable(FtpUtil.getDefaultFiles()).orElse(new FTPFile[0]);
//        Map<String, List<FTPFile>> ftpFilesMap = Arrays.stream(files).filter(FTPFile::isFile).collect(Collectors.groupingBy(file -> file.getName().split("\\.")[0]));
//        ftpFilesMap.forEach((key, ftpFiles) -> {
//            if (ftpFiles.size() == 2 && files[0].getName().endsWith(".txt") && files[1].getName().endsWith(".zip")) {
//                FTPFile zipFile = files[1];
//                //符合条件的数据
////                String fileMD5Str = Md5Util.getFileMD5Str((File) files[1]);
//                //1.检查zip是否正确
////                Md5Util.chargeMd5Str();
//            }
//        });
    }

    public static void main(String[] args) {
//        getCooperateZip();
        System.out.println("sdffa.txt".endsWith(".txt"));
    }
}
