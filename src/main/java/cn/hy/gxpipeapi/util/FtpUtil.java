package cn.hy.gxpipeapi.util;/**
 * @author: szc
 * @date: 2021/10/26 09:29
 * @description:
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

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
    public void setZipPath(String zipPath) {
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

//    @PostConstruct
    public static void ftpClientInit() {
        log.info("连接ftp:" + connect());
    }

    private final static FTPClient ftpClient = new FTPClient();

    private static boolean connect() {
        boolean result = false;
        try {
            ftpClient.connect(ip, port);
            ftpClient.login(username, password);
            ftpClient.setControlEncoding(encoding);
            ftpClient.setBufferSize(1024);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
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
        getDefaultFiles2();
    }

    public static boolean uploadFiles2FTP(List<File> uploadFiles, String uploadPath) {
        FileInputStream fileIs = null;
        try {
            ftpClient.changeWorkingDirectory(uploadPath);
            ftpClient.enterLocalActiveMode();
            for (File uploadFile : uploadFiles) {
                fileIs = new FileInputStream(uploadFile);
                ftpClient.storeFile(uploadFile.getName(), fileIs);
            }
            ftpClient.completePendingCommand();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败：" + e.getMessage());
        } finally {
            try {
                assert fileIs != null;
                fileIs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public static void getDefaultFiles2() {
        FTPClient ftpClient = getFTPClient();
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
                for (FTPFile ftpFile : ftpFilesAll) {
                    if (ftpFile.getName().endsWith("链路传输测试.txt")) {
                        System.out.println(ftpFile.getName());
                        boolean rename = ftpClient.rename(ftpFile.getName(), "链路传输测试2.txt");
                        System.out.println("是否改名成功：" + rename);
                    }
                }
            }
            ftpClient.completePendingCommand();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public static void getDefaultFiles() {
        FTPClient ftpClient = getFTPClient();
        try {
            String destDirPath = "/Users/suzhenchao/浩云/广西社矫/测试解压文件/测试解压";
            byte[] buf=new byte[10240];
            FileOutputStream fileOut;
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
                    List<FTPFile> ftpFiles = entry.getValue();
                    if (ftpFiles.size() == 2 && ftpFiles.get(0).getName().endsWith(".txt") && ftpFiles.get(1).getName().endsWith(".zip")
                            && ftpFiles.get(0).getName().equals("A4501023300002021030752_20211022_060402_1634891925696.txt")) {
                        FTPFile txtFile = ftpFiles.get(0);
                        FTPFile zipFile = ftpFiles.get(1);
                        InputStream txtIs = ftpClient.retrieveFileStream(txtFile.getName());
                        ArrayList<String[]> txtMd5StrList = FileUtil.csvGBK(txtIs);
                        if (txtMd5StrList.size() != 1 || txtMd5StrList.get(0).length != 1) {
                            log.info(txtMd5StrList.toString());
                            throw new RuntimeException("获取txt文件值异常！");
                        }
                        txtIs.close();
                        ftpClient.completePendingCommand();
                        String txtMd5Str = txtMd5StrList.get(0)[0];
                        System.out.println("txtMd5StrList："+txtMd5StrList);
                        System.out.println("txtMd5Str："+txtMd5Str);
                        InputStream zipIs = ftpClient.retrieveFileStream(zipFile.getName());
                        String streamToMD5 = Md5Util.streamToMD5(zipIs);
                        System.out.println("streamToMD5："+streamToMD5);
                        zipIs.close();
                        ftpClient.completePendingCommand();
                        System.out.println("Md5对比结果："+txtMd5Str.equals(streamToMD5));
                        //解析xml文件
                        ZipInputStream zipInputStream = new ZipInputStream(ftpClient.retrieveFileStream(zipFile.getName()));
                        File fileTemp = null;
                        while (zipInputStream.getNextEntry() != null) {
                            java.util.zip.ZipEntry nextEntry = zipInputStream.getNextEntry();
                            log.info("循环输出：{},isDirectory：{}",nextEntry.getName(),nextEntry.isDirectory());
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
