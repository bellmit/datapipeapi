package cn.hy.gxpipeapi.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

@Slf4j
public class ZipUtil {

    public static boolean zipFile(String inputFile, String outputFile) {
        boolean reFlag = false;
        byte[] buf=new byte[10240];
        ZipOutputStream out = null;
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩
            out = new ZipOutputStream(new FileOutputStream(outputFile));
            //注意此处编码设置
//            out.setEncoding("gbk");
            out.setEncoding("utf-8");
            File input = new File(inputFile);
            fileWrite(input, out, buf, null);
            out.close();
            out = null;
            reFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return reFlag;
    }

    public static void fileWrite(File input,ZipOutputStream out,byte[] buf,String zipName) {
        if (zipName == null) {
            zipName = input.getName();
        }
        //如果路径为目录（文件夹）
        if (input.isDirectory()) {
            //取出文件夹中的文件（或子文件夹）
            File[] fileList = Optional.ofNullable(input.listFiles()).orElse(new File[0]);
            //如果文件夹为空，则只需在目的地zip文件中写入一个目录进入
            if (fileList.length == 0) {
                try {
                    out.putNextEntry(new ZipEntry(zipName + "/"));
                    out.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                for (File file : fileList) {
                    fileWrite(file, out, buf, zipName + "/" + file.getName());
                }
            }
        } else if (!input.getName().startsWith(".")) {
            try (FileInputStream in = new FileInputStream(input)) {
                //如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
                out.putNextEntry(new ZipEntry(zipName));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 功能:压缩多个文件成一个zip文件
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public static boolean zipFiles(File[] srcfile,File zipfile){
        boolean reFlag = false;
        byte[] buf=new byte[10240];
        ZipOutputStream out = null;
        FileInputStream in = null;
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩
            out=new ZipOutputStream(new FileOutputStream(zipfile));
            //注意此处编码设置
            out.setEncoding("gbk");
            for (File file : srcfile) {
                in = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
                in = null;
            }
            out.close();
            out = null;
            reFlag = true;
            log.info("压缩完成,文件详细信息为："+zipfile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(in!=null){
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return reFlag;
    }


    public static void unZipFile(String inputFile, String destDirPath) {
        File srcFile = new File(inputFile);//获取当前压缩文件
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        byte[] buf=new byte[10240];
        FileOutputStream fileOut;
        File file;
        InputStream inputStream;
        //开始解压
        //构建解压输入流
        try {
            ZipFile zipFile = new ZipFile(inputFile,FileUtil.getFileEncoding(inputFile));
//            ZipFile zipFile = new ZipFile(inputFile,"UTF-8");
            String encoding = zipFile.getEncoding();
            System.out.println(encoding);
            for(Enumeration entries = zipFile.getEntries(); entries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                file = new File(destDirPath, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    //如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    inputStream = zipFile.getInputStream(entry);
                    fileOut = new FileOutputStream(file);
                    int len;
                    while((len=inputStream.read(buf))>0){
                        fileOut.write(buf,0,len);
                    }
                    fileOut.close();
                    inputStream.close();
                }
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, ArchiveException {
        String 法院法律援助 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/法律援助/A4501023300002021090158_20211019_030401_1634607294797.zip";
        String 法院法律援助Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/法律援助/";
        String 检察院调查评估 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/调查评估/检查院/A4501023300002021090158_20211019_030301_1634607455367.zip";
        String 检察院调查评估Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/调查评估/检查院/";
        String 撤销缓刑回复 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/撤销缓刑回复/A4501021100002020100001_20211019_050401_1634642076467.zip";
        String 撤销缓刑回复Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/撤销缓刑回复/";
        String 检察院文件解压乱码 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/法律援助/检察院文件解压乱码/A4501023300002021090158_20211018_030401_1634528722534.zip";
        String 检察院文件解压乱码Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/法律援助/检察院文件解压乱码/";
        String 交付接收 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/交付接收/A4501023300002021030752_20211018_030501_1634610515.zip";
        String 交付接收Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/交付接收/";
        String 提出检察意见 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提出检察意见/A4501023300002021030752_20211021_02040301_1634800723406.zip";
        String 提出检察意见Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提出检察意见/";
        String 反馈刑满释放材料 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/反馈刑满释放材料/A4501023300002021030752_20211021_060402_1634830356.zip";
        String 反馈刑满释放材料Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/反馈刑满释放材料/";
        String 提请减刑要求补充材料 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提请减刑要求补充材料/A4501023300002021030752_20211021_03040301_1634810297423.zip";
        String 提请减刑要求补充材料Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提请减刑要求补充材料/";
        String 提请减刑建议发送裁定结果 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提请减刑建议发送裁定结果/A4501023300002021030752_20211021_050701_1634810151553.zip";
        String 提请减刑建议发送裁定结果Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提请减刑建议发送裁定结果/";
        String a = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/减刑反馈意见采纳/A4501023300002021030752_20211022_04020302_1634886194.zip";
        String aOut = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/减刑反馈意见采纳/";
        String 提请撤销假释建议反馈意见采纳情况 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提请撤销假释建议反馈意见采纳情况/A4501023300002021030752_20211022_03020505_1634882372286.zip";
        String 提请撤销假释建议反馈意见采纳情况Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提请撤销假释建议反馈意见采纳情况/";
        String 治安处罚反馈审查结果 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/治安处罚反馈审查结果/A4501023300002021100632_20211022_030601_01634888103156.zip";
        String 治安处罚反馈审查结果Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/治安处罚反馈审查结果/";
        String 法律援助返回 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/法律援助返回/A4501023300002021090158_20211022_060202_1634889315.zip";
        String 法律援助返回Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/法律援助返回/";

        String 监狱返回刑满释放 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/监狱反馈刑满释放/A4501023300002021030752_20211022_060402_1634885485872.zip";
        String 监狱反馈刑满释放Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/监狱反馈刑满释放/";
        String 撤销缓刑反馈 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/撤销缓刑意见采纳/A4501023300002021030752_20211021_03020504_1634806681449.zip";
        String 撤销缓刑反馈Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/撤销缓刑意见采纳/";

        String 最后1 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/最后/A4501023300002021030752_20211022_03020304_1634892270269.zip";
        String 最后2 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/最后/A4501023300002021090158_20211022_060202_1634893395.zip";
        String 最后1Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/最后/";

        String 监外 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/监外执收监执行/A4501023300002021030752_20211025_060501_1635134473.zip";
        String 监外4 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/监外执收监执行/A4501023300002021030752_20211025_060501_1635134474.zip";
        String 监外z = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/监外执收监执行/A4501023300002021030752_20211020_060702_1634694256.zip";
        String 监外Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/监外执收监执行/";
        String 测试解压 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提请撤销假释建议反馈意见采纳情况/A4501023300002021030752_20211022_03020505_1634882372286.zip";
        String 测试解压Out = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/提请撤销假释建议反馈意见采纳情况/";
//        decompressor(测试解压, 测试解压Out);
//        unZipFile(测试解压, 测试解压Out);
//        decompressor(监外z, 监外Out);
        decompressor(监外4, 监外Out);
//        unZipFile(监外4, 监外Out);
//        decompressor(最后2, 最后1Out);
//        decompressor(最后1, 最后1Out);
//        decompressor(撤销缓刑反馈, 撤销缓刑反馈Out);
//        decompressor(监狱返回刑满释放, 监狱反馈刑满释放Out);
//        decompressor(法律援助返回, 法律援助返回Out);
//        unZipFile(法律援助返回, 法律援助返回Out);
//        decompressor(治安处罚反馈审查结果, 治安处罚反馈审查结果Out);
//        decompressor(提请撤销假释建议反馈意见采纳情况, 提请撤销假释建议反馈意见采纳情况Out);
//        decompressor(法院法律援助, 法院法律援助Out);
//        unZipAndCharge(a, aOut);
//        decompressor(撤销缓刑回复, 撤销缓刑回复Out);
//        decompressor(交付接收, 交付接收Out);
//        decompressor(提出检察意见, 提出检察意见Out);
//        decompressor(检察院文件解压乱码, 检察院文件解压乱码Out);
//        decompressor(反馈刑满释放材料, 反馈刑满释放材料Out);
//        decompressor(提请减刑要求补充材料, 提请减刑要求补充材料Out);
//        decompressor(提请减刑建议发送裁定结果, 提请减刑建议发送裁定结果Out);
//        String zipPath = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/回复文件/A4501023300002021090158_20211018_060101_1634545814";
//        String zipOutPath = zipPath + ".zip";
        String zipPath = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/回复文件/法律援助/回复法院/A4501023300002021030752_20211019_060201_1634610650";
        String zipOutPath = zipPath + ".zip";
        String 回复检察院调查评估 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/回复文件/调查评估/回复检察院/A4501023300002021090158_20211019_060101_1634614250";
        String 缓刑撤销申请 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/撤销缓刑/A4501023300002021030752_20211020_060702_1634694256";
        String 假释撤销申请 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/撤销假释/A4501023300002021030752_20211019_060704_1634631041";
        String 期满解矫 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/期满解矫/A4501021100002021100002_20211019_060601_1634632241";
        String 减刑当地检察区意见 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/减刑当地检察区意见/A4501023300002021030752_20211020_04020301_1634700655";
        String 满期解矫通知 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/期满解矫/A4501023300002021030752_20211020_060601_1634718355";
        String 矫正期满通知 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/矫正期满通知/A4501023300002021030752_20211021_040602_1634778615";
        String 矫正期满通知2 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/矫正期满通知/A4501023300002021030752_20211021_040602_1634782265";
        String 终止矫正 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/终止矫正/A4501023300002021030752_20211021_060701_1634779395";
        String 治安处罚 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/提请治安处罚/A4501023300002021030752_20211021_060401_1634782995";
        String 减刑反馈意见采纳 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/减刑反馈意见采纳/A4501023300002021030752_20211022_04020302_1634886194";
        String 减刑反馈意见采纳2 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/减刑反馈意见采纳/A4501023300002021030752_20211022_04020302_1634889051";
        String 提出减刑建议 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/提出减刑建议/A4501023300002021030752_20211021_060703_1634784315";
        String 法律援助回复检查院 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/回复文件/法律援助/回复检查院/A4501023300002021090158_20211021_060201_1634807141";
        String 减刑补充证据材料 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/减刑补充证据材料/A4501023300002021030752_20211022_04030301_1634866607";
        String 压缩 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/撤销缓刑/A4501023300002021030752_20211022_060702_1634894007";
        String 压缩2 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/减刑反馈意见采纳2/A4501023300002021030752_20211021_04020302_1634786055";
        String 监外压缩 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/监外执收监执行/A4501023300002021030752_20211025_060501_1635134474";
        String 监外压缩2 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/发送文件/发起/监外执收监执行/A4501023300002021030752_20211020_060702_1634694256";
//        zipAndMd51(监外压缩2);
//        zipAndMd51(监外压缩);
//        zipAndMd5(zipPath, zipOutPath);
//        zipAndMd51(回复检察院调查评估);
//        zipAndMd51(缓刑撤销申请);
//        zipAndMd51(假释撤销申请);
//        zipAndMd51(期满解矫);
//        zipAndMd51(期满解矫);
//        zipAndMd51(减刑当地检察区意见);
//        zipAndMd51(满期解矫通知);
//        zipAndMd51(矫正期满通知);
//        zipAndMd51(治安处罚);
//        zipAndMd51(矫正期满通知2);
//        zipAndMd51(减刑反馈意见采纳2);
//        zipAndMd51(反馈刑满释放材料);
//        zipAndMd51(提出减刑建议);
//        zipAndMd51(法律援助回复检查院);
//        zipAndMd51(减刑补充证据材料);
//        zipAndMd51(压缩);
//        zipAndMd51(压缩2);
    }

    private static void zipAndMd51(String zipPath) {
        zipFile(zipPath, zipPath+ ".zip");
        //获取zip的md5编码
        Md5Util.writeMd5File(zipPath+ ".zip");
    }

    private static void zipAndMd5(String zipPath, String zipOutPath) {
        zipFile(zipPath, zipOutPath);
        //获取zip的md5编码
        Md5Util.writeMd5File(zipOutPath);
    }

    private static void unZipAndCharge(String unZipIn, String unZipOut) {
        Md5Util.chargeMd5StrBy2Path(unZipIn);
        unZipFile(unZipIn, unZipOut);
    }

    public static void decompressor(String zipFile, String targetDir) throws IOException, ArchiveException {

        File archiveFile = new File(zipFile);
        // 文件不存在，跳过
        if (!archiveFile.exists())
            return;

        ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        ArchiveEntry entry = null;
        while ((entry = input.getNextEntry()) != null) {
            if (!input.canReadEntryData(entry)) {
                // log something?
                continue;
            }
            String name = Paths.get(targetDir, entry.getName()).toString();
            File f = new File(name);
            if (entry.isDirectory()) {
                if (!f.isDirectory() && !f.mkdirs()) {
                    throw new IOException("failed to create directory " + f);
                }
            } else {
                File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("failed to create directory " + parent);
                }
                try (OutputStream o = Files.newOutputStream(f.toPath())) {
                    IOUtils.copy(input, o);
                }
            }
        }
        input.close();

    }


}
