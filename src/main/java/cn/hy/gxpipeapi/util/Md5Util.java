package cn.hy.gxpipeapi.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import java.util.Locale;

@Slf4j
public class Md5Util {

    public static void chargeMd5StrByZipPath(String zipPath) {
        String zipMd5Str = getFileMD5StrByFilePath(zipPath);
        String xmlFileName = zipPath.replace(".zip", ".txt");
        File xmlFile = new File(xmlFileName);
        if (!xmlFile.exists() || !xmlFile.isFile()) {
            throw new RuntimeException(String.format("没有md5描述文件：%s，请核查！", xmlFileName));
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(xmlFileName));) {
            String line = fileReader.readLine();
            if (line == null || !line.trim().equalsIgnoreCase(zipMd5Str)) {
                throw new RuntimeException(String.format("密码不匹配！程序解析md5:%s,文件md5:%s。zipPath:%s", zipMd5Str, line, zipPath));
            } else {
                log.info(String.format("密码成功匹配！程序解析md5:%s,文件md5:%s。zipPath:%s", zipMd5Str, line, zipPath));
            }
        } catch (IOException e) {
            log.info("匹配文件失败：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void chargeMd5StrBy2Path(String zipPath, String txtPath) {
        String zipMd5Str = getFileMD5StrByFilePath(zipPath);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(txtPath));) {
            String line = fileReader.readLine();
            if (line == null || !line.trim().equalsIgnoreCase(zipMd5Str)) {
                throw new RuntimeException(String.format("密码不匹配！程序解析md5:%s,文件md5:%s。", zipMd5Str, line));
            } else {
                log.info(String.format("密码成功匹配！程序解析md5:%s,文件md5:%s。", zipMd5Str, line));
            }
        } catch (Exception e) {
            log.info("匹配文件失败：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String getFileMD5StrByFilePath(String filePath) {
        File file = new File(filePath);
        return getFileMD5Str(file);
    }
    /**
     * 获取zip文件的MD5校验码
     *
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String getFileMD5Str(File file) {
        String myChecksum = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(file.toPath()));
            byte[] digest = md.digest();
            myChecksum = DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return myChecksum;
    }

    /**
     * 获取byte数组的md5值
     * @param fileBytes bytes
     * @return md5Str
     */
    public static String getByteMD5Str(byte[] fileBytes) {
        String myChecksum = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(fileBytes);
            byte[] digest = md.digest();
            myChecksum = DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return myChecksum;
    }

    public static String getMd5FileName(String sourceFilePath) {
        return sourceFilePath.replace(".zip", ".txt");
    }

    public static void writeMd5File(String sourceFilePath) {
        String md5Str = getFileMD5StrByFilePath(sourceFilePath).toLowerCase(Locale.ROOT);
        FileUtil.writeMd5Txt(sourceFilePath, md5Str, "UTF-8");
    }

    public static String streamToMD5(InputStream inputStream) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[10240];

            int numRead;
            while((numRead = inputStream.read(buffer)) > 0) {
                mdTemp.update(buffer, 0, numRead);
            }

            return toHexString(mdTemp.digest());
        } catch (Exception var4) {
            return null;
        }
    }

    private static String toHexString(byte[] md) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int j = md.length;
        char[] str = new char[j * 2];

        for(int i = 0; i < j; ++i) {
            byte byte0 = md[i];
            str[2 * i] = hexDigits[byte0 >>> 4 & 15];
            str[i * 2 + 1] = hexDigits[byte0 & 15];
        }

        return new String(str);
    }

    public static void main(String[] args) {
        String filePath = "/Users/suzhenchao/浩云/广西社矫/ftp文件/接收文件/a/A4501004325002020050003_20201015_030301_1602765000509.zip";
        filePath = "/Users/suzhenchao/浩云/广西社矫/ftp文件/接收文件/test/zip/A4501004325002020050003_20201015_030301_1602765000509.zip";
//        String fileChecksumMD5First = getFileMD5Str(new File(filePath));
//        System.out.println(fileChecksumMD5First.toLowerCase(Locale.ROOT));
//        System.out.println("b75da1439b101d3ef27f758a873090a4".equals(fileChecksumMD5First.toLowerCase(Locale.ROOT)));
//        System.out.println("文件路径：" + filePath);
//        String fileMD5StrByFilePath = getMd5FileName(filePath);
//        System.out.println("md5文件名：" + fileMD5StrByFilePath);
//        writeMd5File(filePath);
//        String path1 = getFileMD5StrByFilePath(filePath);
//        System.out.println(path1);
//        filePath = "/Users/suzhenchao/浩云/广西社矫/ftp文件/接收文件/test/A4501004325002020050003_20201015_030301_1602765000509.zip";
//        String path2 = getFileMD5StrByFilePath(filePath);
//        System.out.println(path2);
//        System.out.println(path1.equals(path2));
//        chargeMd5StrByZipPath("/Users/suzhenchao/浩云/广西社矫/测试解压文件/A4501021100002020090004_20211029_060402_1635525135.zip");
        chargeMd5StrByZipPath("/Users/suzhenchao/浩云/广西社矫/datapipeapi/src/main/resources/static/zipfile/20211107/A4501023300002021030752_20211022_060402_1634891925696.zip");
    }


}
