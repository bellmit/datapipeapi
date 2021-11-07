package cn.hy.gxpipeapi.util;/**
 * @author: szc
 * @date: 2021/10/26 09:29
 * @description:
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @project   : datapipeapi
 * @className : FtpUtil
 * @author    : szc
 * @time      : 2021年10月26日 09:29
 * @desc      : ftp连接
 **/
@Slf4j
@Component
public class FtpUtil {
    private static String ip = "59.211.16.162";
    private static int port = 16788;
    private static String username = "dapp";
    private static String password = "GXjh12#$";
    private static String encoding = "UTF-8";
    private static String downloadPath = "/file_data_exchange/file_receive/xtba_receive/sft/";
    private static String uploadPath = "/file_data_exchange/file_send/sft_send/";
    private static String zipPath = "/Users/suzhenchao/浩云/广西社矫/datapipeapi/src/main/resources/static/zipfile";

    public static Logger getLog() {
        return log;
    }

    public static String getIp() {
        return ip;
    }

    public static int getPort() {
        return port;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static String getEncoding() {
        return encoding;
    }

    public static String getDownloadPath() {
        return downloadPath;
    }

    public static String getUploadPath() {
        return uploadPath;
    }

    public static FTPClient getFtpClient() {
        return ftpClient;
    }

    public static String getZipPath() {
        return zipPath;
    }

    @Value("${ftp.zipPath}")
    public static void setZipPath(String zipPath) {
        FtpUtil.zipPath = zipPath;
    }

    @Value("${ftp.uploadPath}")
    public void setUploadPath(String uploadPath) {
        FtpUtil.uploadPath = uploadPath;
    }

    @Value("${ftp.downloadPath}")
    public void setDownloadPath(String downloadPath) {
        FtpUtil.downloadPath = downloadPath;
    }

    @Value("${ftp.ip}")
    public void setIp(String ip) {
        FtpUtil.ip = ip;
    }
    @Value("${ftp.port}")
    public void setPort(int port) {
        FtpUtil.port = port;
    }
    @Value("${ftp.username}")
    public void setUsername(String username) {
        FtpUtil.username = username;
    }
    @Value("${ftp.password}")
    public void setPassword(String password) {
        FtpUtil.password = password;
    }
    @Value("${ftp.encoding}")
    public void setEncoding(String encoding) {
        FtpUtil.encoding = encoding;
    }

    @PostConstruct
    public static void ftpClientInit() {
        log.info("连接ftp:" + connect());
        getDownloadFiles();
    }

    private final static FTPClient ftpClient = new FTPClient();

    private static boolean connect() {
        boolean result = false;
        try {
            ftpClient.connect(ip, port);
            ftpClient.login(username, password);
            ftpClient.setControlEncoding(encoding);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                log.info("连接失败");
                ftpClient.disconnect();
                return false;
            }
            result = true;
        } catch (IOException e) {
            log.info("连接失败：{}", e.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        //  ip: 59.211.16.162
        //  port: 16788
        //  username: dapp
        //  password: GXjh12#$
        //  encoding: utf-8
        getDefaultFiles();
    }

    @Transactional
    public static void getDefaultFiles() {
        FTPClient ftpClient = getFTPClient();
        String txtFilePath = "";
        String zipFilePath = "";
        try {
            boolean changePath = ftpClient.changeWorkingDirectory(downloadPath);
            if (!changePath) {
                throw new RuntimeException(String.format("切换目录失败：%s", downloadPath));
            }
            ftpClient.enterLocalPassiveMode();
            FTPFile[] ftpFilesAll = ftpClient.listFiles();
            if (ftpFilesAll.length == 0) {
                throw new RuntimeException(String.format("该目录[%s]下没有文件！", downloadPath));
            } else {
                Map<String, List<FTPFile>> ftpFilesMap = Arrays.stream(ftpFilesAll).filter(FTPFile::isFile)
                        .collect(Collectors.groupingBy(fileF -> fileF.getName().split("\\.")[0]));
                for (Map.Entry<String, List<FTPFile>> entry : ftpFilesMap.entrySet()) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
                    String todayDirName = sf.format(new Date());
                    String todayZipPath = zipPath + File.separator + todayDirName + File.separator;
                    File file = new File(todayZipPath);
                    if (!file.exists()) {
                        boolean mkdirs = file.mkdirs();
                        if (mkdirs) {
                            log.info("创建文件夹成功：{}", file.getPath());
                        } else {
                            throw new RuntimeException("创建文件夹失败：{}" + todayZipPath);
                        }
                    }
                    List<FTPFile> ftpFiles = entry.getValue();
                    if (ftpFiles.size() == 2 && ftpFiles.get(0).getName().endsWith(".txt") && ftpFiles.get(1).getName().endsWith(".zip")
                            && ftpFiles.get(0).getName().equals("A4501023300002021030752_20211022_060402_1634891925696.txt")) {
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
                        try (FileOutputStream txtOut = new FileOutputStream(txtFilePath);
                             FileOutputStream zipOut = new FileOutputStream(zipFilePath);) {
                            ftpClient.retrieveFile(txtFile.getName(), txtOut);
                            ftpClient.retrieveFile(zipFile.getName(), zipOut);
                            Md5Util.chargeMd5StrByZipPath(zipFilePath);
                            //解析xml文件
                            ZipFile unZipFile = new ZipFile(zipFilePath, "UTF-8");
                            boolean isWithXml = false;
                            for(Enumeration entries = unZipFile.getEntries(); entries.hasMoreElements();) {
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
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void disConnect() {
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            log.info("关闭连接失败：{}", e.getMessage());
        }
    }

    public static FTPClient getFTPClient() {
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            connect();
        }
        return ftpClient;
    }

    public static void getDownloadFiles() {
        FTPFile[] files = getFiles(downloadPath);
        for (FTPFile file : files) {
            log.info(file.getName());
        }
    }
    public static FTPFile[] getFiles(String path) {
        FTPClient ftpClient = getFTPClient();
        try {
            boolean changePath = ftpClient.changeWorkingDirectory(path);
            if (!changePath) {
                throw new RuntimeException(String.format("切换目录失败：%s", path));
            }
            ftpClient.enterLocalPassiveMode();
            FTPFile[] ftpFiles = ftpClient.listFiles();
            ftpClient.completePendingCommand();
            if (ftpFiles.length == 0) {
                throw new RuntimeException(String.format("该目录[%s]下没有文件！", path));
            } else {
                return ftpFiles;
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("获取文件失败：%s", e.getMessage()));
        }
    }
}
