package cn.hy.gxpipeapi.common;

import cn.hy.gxpipeapi.util.Md5Util;
import cn.hy.gxpipeapi.util.ZipUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

@RestController
public class CommonController {

    @GetMapping("/getFileMd5StrGet")
    public String getFileMd5StrGet(String filePath) {
        return Md5Util.getFileMD5StrByFilePath(filePath);
    }

    @PostMapping("/getFileMd5StrPose")
    public String getFileMd5StrPose(String filePath) {
        return Md5Util.getFileMD5StrByFilePath(filePath);
    }

    @GetMapping("unZip")
    public String unZipAndCharge(String zipPath, String desPath) {
        //获取zip的md5编码
        String zipMd5Str = Md5Util.getFileMD5StrByFilePath(zipPath);
        String xmlFileName = zipPath.replace("zip", "txt");
        File xmlFile = new File(xmlFileName);
        if (!xmlFile.exists() || !xmlFile.isFile()) {
            throw new RuntimeException(String.format("没有md5描述文件：%s，请核查！", xmlFileName));
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(xmlFileName));) {
            String line = fileReader.readLine();
            if (line == null || !line.trim().equals(zipMd5Str)) {
                throw new RuntimeException(String.format("密码不匹配！程序解析md5:%s,文件md5:%s。", zipMd5Str, line));
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        ZipUtil.unZipFile(zipPath, desPath);
        return "解压成功！";
    }

    @GetMapping("zip")
    public String zipAndCharge(String zipPath, String desPath) {
        ZipUtil.zipFile(zipPath, desPath);
        //获取zip的md5编码
        Md5Util.writeMd5File(desPath);
        return "解压成功！";
    }
}
