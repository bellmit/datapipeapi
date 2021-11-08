package cn.hy.gxpipeapi.cooperatework.service;


import cn.hy.gxpipeapi.util.FileUtil;
import cn.hy.gxpipeapi.util.FtpUtil;
import cn.hy.gxpipeapi.util.Md5Util;
import cn.hy.gxpipeapi.util.XmlUtil;
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
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public class CooperateZipSchedule {

    @Transactional
    public static void downloadFtpZipFilesByDownload() {
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

    @Transactional
    public static void getFtoZipFiles() {
        FTPClient ftpClient = FtpUtil.getFTPClient();
        try {
            String destDirPath = "/Users/suzhenchao/浩云/广西社矫/测试解压文件/测试解压";
            byte[] buf=new byte[10240];
            FileOutputStream fileOut;
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
                    List<FTPFile> ftpFiles = entry.getValue();
                    if (ftpFiles.size() == 2 && ftpFiles.get(0).getName().endsWith(".txt") && ftpFiles.get(1).getName().endsWith(".zip")
                            && ftpFiles.get(0).getName().equals("A4501023300002021030752_20211022_060402_1634891925696.txt")) {
                        FTPFile txtFile = ftpFiles.get(0);
                        FTPFile zipFile = ftpFiles.get(1);
                        InputStream txtIs = ftpClient.retrieveFileStream(txtFile.getName());
                        ArrayList<String[]> txtMd5StrList = FileUtil.csvGBK(txtIs);
                        if (txtMd5StrList.size() != 1 || txtMd5StrList.get(0).length != 1) {
                            throw new RuntimeException("获取txt文件值异常！");
                        }
                        txtIs.close();
                        ftpClient.completePendingCommand();
                        String txtMd5Str = txtMd5StrList.get(0)[0];
                        log.info("txtMd5StrList：{}", txtMd5StrList);
                        log.info("txtMd5Str：{}", txtMd5Str);
                        InputStream zipIs = ftpClient.retrieveFileStream(zipFile.getName());
                        String streamToMD5 = Md5Util.streamToMD5(zipIs);
                        log.info("streamToMD5：{}" ,streamToMD5);
                        zipIs.close();
                        ftpClient.completePendingCommand();
                        log.info("Md5对比结果：{}", txtMd5Str.equals(streamToMD5));
                        //解析xml文件
                        ZipInputStream zipInputStream = new ZipInputStream(ftpClient.retrieveFileStream(zipFile.getName()));
                        File fileTemp = null;
                        while (zipInputStream.getNextEntry() != null) {
                            java.util.zip.ZipEntry nextEntry = zipInputStream.getNextEntry();
                            fileTemp = new File(destDirPath, nextEntry.getName());
                            if (fileTemp.isDirectory()) {
                                fileTemp.mkdirs();
                            }else {
                                if (nextEntry.getName().endsWith(".xml")) {
                                    JSONObject xmlFileJson = XmlUtil.xmlFile2JsonByIs(zipInputStream);
                                    log.info("解压的xmlJSONStr:{}", JSON.toJSONString(xmlFileJson));
                                }
                                //如果指定文件的目录不存在,则创建
                                File parent = fileTemp.getParentFile();
                                if (parent != null && !parent.exists()) {
                                    parent.mkdirs();
                                }
                                fileOut = new FileOutputStream(fileTemp);
                                int len;
                                while ((len = zipInputStream.read(buf)) != -1) {
                                    fileOut.write(buf, 0, len);
                                }
                                fileOut.close();
                            }
                        }
                    }
                }
            }
            ftpClient.completePendingCommand();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public static void uploadFtpZipFiles() {

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
//        downloadFtpZipFilesByDownload();
        getFtoZipFiles();
    }
}
