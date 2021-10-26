package cn.hy.gxpipeapi.util;/**
 * @author: szc
 * @date: 2021/10/26 09:29
 * @description:
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
    private static String ip;
    private static int port;
    private static String username;
    private static String password;
    private static String encoding;

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
        getFiles("/tools");
    }

    private static FTPClient ftpClient = new FTPClient();

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

    public static void getFiles(String path) {
        FTPClient ftpClient = getFTPClient();
        try {
            ftpClient.changeWorkingDirectory(path);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                log.info(ftpFile.getName());
            }
        } catch (IOException e) {
            log.info("获取文件失败：{}", e.getMessage());
        }
    }
}
