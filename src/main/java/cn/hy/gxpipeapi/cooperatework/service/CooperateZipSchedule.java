package cn.hy.gxpipeapi.cooperatework.service;


import cn.hy.gxpipeapi.util.FtpUtil;
import org.apache.commons.net.ftp.FTPFile;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CooperateZipSchedule {

    public void getCooperateZip() {
        FTPFile[] files = FtpUtil.getFiles(FtpUtil.getDownloadPath());
        Map<String, List<FTPFile>> ftpFiles = Arrays.stream(files).filter(FTPFile::isFile).collect(Collectors.groupingBy(FTPFile::getName));
        ftpFiles.forEach((key, ftpFile) -> {

        });
    }
}
