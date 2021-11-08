package cn.hy.gxpipeapi.cooperatework.service;


import cn.hy.gxpipeapi.util.FtpUtil;
import cn.hy.gxpipeapi.util.Md5Util;
import cn.hy.gxpipeapi.util.XmlUtil;
import cn.hy.gxpipeapi.util.ZipUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CooperateZipSchedule {

    @Transactional
    public static void downloadFtpZipFiles() {
        FTPClient ftpClient = FtpUtil.getFTPClient();
        String txtFilePath = "";
        String zipFilePath = "";
        try {
            boolean changePath = ftpClient.changeWorkingDirectory(FtpUtil.getDownloadPath());
            if (!changePath) {
                throw new RuntimeException(String.format("切换目录失败：%s", FtpUtil.getDownloadPath()));
            }
            ftpClient.enterLocalPassiveMode();
            FTPFile[] ftpFilesAll = ftpClient.listFiles();
            if (ftpFilesAll.length == 0) {
                throw new RuntimeException(String.format("该目录[%s]下没有文件！", FtpUtil.getDownloadPath()));
            } else {
                Map<String, List<FTPFile>> ftpFilesMap = Arrays.stream(ftpFilesAll).filter(FTPFile::isFile)
                        .collect(Collectors.groupingBy(fileF -> fileF.getName().split("\\.")[0]));
                for (Map.Entry<String, List<FTPFile>> entry : ftpFilesMap.entrySet()) {
                    //当天的下载文件夹目录
                    String todayZipPath = getTodayZipPath();
                    List<FTPFile> ftpFiles = entry.getValue();
                    if (ftpFiles.size() == 2 && ftpFiles.get(0).getName().endsWith(".txt") && ftpFiles.get(1).getName().endsWith(".zip")) {
                        FTPFile txtFile = ftpFiles.get(0);
                        FTPFile zipFile = ftpFiles.get(1);
                        txtFilePath = todayZipPath + txtFile.getName();
                        zipFilePath = todayZipPath + zipFile.getName();
                        File txtFileDownload = new File(txtFilePath);
                        File zipFileDownload = new File(zipFilePath);
                        if (txtFileDownload.exists() && txtFileDownload.isFile() && zipFileDownload.exists() && zipFileDownload.isFile()) {
                            log.info("文件已存在：{},{}", txtFileDownload.getName(), zipFileDownload.getName());
                            continue;
                        }
                        //下载zip文件并解析
                        downloadZipAndAnalyse(ftpClient, txtFilePath, zipFilePath, txtFile, zipFile);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void downloadZipAndAnalyse(FTPClient ftpClient, String txtFilePath, String zipFilePath, FTPFile txtFile, FTPFile zipFile) {
        try (FileOutputStream txtOut = new FileOutputStream(txtFilePath);
             FileOutputStream zipOut = new FileOutputStream(zipFilePath);) {
            ftpClient.retrieveFile(txtFile.getName(), txtOut);
            ftpClient.retrieveFile(zipFile.getName(), zipOut);
            Md5Util.chargeMd5StrByZipPath(zipFilePath);
            //解析xml文件
            ZipFile unZipFile = new ZipFile(zipFilePath, "UTF-8");
            boolean isWithXml = false;
            for (Enumeration entries = unZipFile.getEntries(); entries.hasMoreElements(); ) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                InputStream inputStream = unZipFile.getInputStream(zipEntry);
                if (zipEntry.getName().endsWith(".xml")) {
                    JSONObject xmlFileJson = XmlUtil.xmlFile2JsonByIs(inputStream);
                    log.info("解压的xmlJSONStr:{}", JSON.toJSONString(xmlFileJson));
                    isWithXml = true;
                }
            }
            if (!isWithXml) {
                throw new RuntimeException("没有xml文件！");
            }
            unZipFile.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getTodayZipPath() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String todayDirName = sf.format(new Date());
        String todayZipPath = FtpUtil.getZipPath() + File.separator + todayDirName + File.separator;
        File file = new File(todayZipPath);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                log.info("创建文件夹成功：{}", file.getPath());
            } else {
                throw new RuntimeException("创建文件夹失败：{}" + todayZipPath);
            }
        }
        return todayZipPath;
    }

    public static void main(String[] args) {
//        getCooperateZip();
        System.out.println("sdffa.txt".endsWith(".txt"));
    }
}
