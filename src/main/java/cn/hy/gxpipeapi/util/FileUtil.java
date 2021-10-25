package cn.hy.gxpipeapi.util;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    /**
     * 生成zip的md5加密文件
     * @param filePath
     * @param md5Str
     * @param charsetName
     */
    public static void writeMd5Txt(String filePath, String md5Str,String charsetName) {
        String md5FileName = Md5Util.getMd5FileName(filePath);
        System.out.println("md5FileName:" + md5FileName);
        File file = new File(md5FileName);
        if (file.exists()) {
            file.delete();
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charsetName))){
            writer.write(md5Str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * zip文件压缩
     * @param inputFile 待压缩文件夹/文件名
     * @param outputFile 生成的压缩包名字
     */

    public static void zipCompress(String inputFile, String outputFile) {
        try (//创建zip输出流
             ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));

             //创建缓冲输出流
             BufferedOutputStream bos = new BufferedOutputStream(out);) {
            File input = new File(inputFile);
            compress(out, bos, input, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @param zipName 压缩文件名，可以写为null保持默认
     */
    //递归压缩
    public static void compress(ZipOutputStream out, BufferedOutputStream bos, File input, String zipName) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                for (File file : fileList) {
                    compress(out, bos, file, zipName + "/" + file.getName());
                }
            }
        } else {
            //如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
            try {
                out.putNextEntry(new ZipEntry(zipName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (FileInputStream fos = new FileInputStream(input);
                 BufferedInputStream bis = new BufferedInputStream(fos);) {
                int len = -1;
                //将源文件写入到zip文件中
                byte[] buf = new byte[1024];
                while ((len = bis.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * zip解压
     * @param inputFile 待解压文件名
     * @param destDirPath  解压路径
     */

    public static void zipUncompress(String inputFile, String destDirPath) {
        File srcFile = new File(inputFile);//获取当前压缩文件
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        //开始解压
        //构建解压输入流
        try (ZipInputStream zIn = new ZipInputStream(new FileInputStream(srcFile));) {
            ZipEntry entry = null;
            File file = null;
            while ((entry = zIn.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    file = new File(destDirPath, entry.getName());
                    if (!file.exists()) {
                        new File(file.getParent()).mkdirs();//创建此文件的上级目录
                    }
                    OutputStream out = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(out);
                    int len = -1;
                    byte[] buf = new byte[1024];
                    while ((len = zIn.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    bos.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getFileEncoding(String filePath) {
        File file = new File(filePath);
        try (InputStream in = new java.io.FileInputStream(file)) {
            byte[] b = new byte[10];
            in.read(b);
            for (byte b1 : b) {
                System.out.print(b1 + "，");
            }
            if (b[0] == -17 && b[1] == -69 && b[2] == -65)
                return "utf-8";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "gbk";
    }

    public static void main(String[] args) {
        String path = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/法律援助/检察院文件解压乱码/A4501023300002021090158_20211018_030401_1634528722534.zip";
        System.out.println(getFileEncoding(path));

        String path2 = "/Users/suzhenchao/浩云/广西社矫/ftp文件/1018之前到数据文件/1018ftp文件/接收文件/法律援助/法院/A4501023300002021030752_20211018_030401_1634538437.zip";
        System.out.println(getFileEncoding(path2));

    }

}
