package cn.hy.gxpipeapi.util;
import cn.hy.gxpipeapi.xmlfile.dto.UploadFile;

import cn.hy.gxpipeapi.xmlfile.dto.DzjzCatalog;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Slf4j
public class XmlUtil {

    public static void writeXmlOut(String templateName,String outFilePath,Map<String, Object> dataMap) {
        //创建配置实例
        Configuration configuration = new Configuration();
        //设置编码
        configuration.setDefaultEncoding("UTF-8");
        //ftl模板文件统一放至/包下面
        configuration.setClassForTemplateLoading(XmlUtil.class,"/xmltemplate/");
        // 获取模板
        try {
            Template template = configuration.getTemplate(templateName);
            template.setOutputEncoding("UTF-8");
            File outFile = new File(outFilePath);
            //将模板和数据模型合并生成文件
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8));
            template.process(dataMap,out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String, Object> dataMap = new HashMap<>();
        List<DzjzCatalog> dzjzCatalogList = new ArrayList<>();
        List<UploadFile> uploadFileList = new ArrayList<>();
        UploadFile uploadFile = new UploadFile();
        uploadFile.setPath("/司法卷宗/调查评估意见书.pdf");
        uploadFile.setName("调查评估意见书.pdf");
        uploadFile.setSortNo("1");
        uploadFileList.add(uploadFile);

        UploadFile uploadFile1 = new UploadFile();
        uploadFile1.setPath("/司法卷宗/调查评估意见书2.pdf");
        uploadFile1.setName("调查评估意见书2.pdf");
        uploadFile1.setSortNo("2");
        uploadFileList.add(uploadFile1);

        DzjzCatalog catalog = new DzjzCatalog();
        catalog.setName("司法卷宗");
        catalog.setCode("SFJZ");
        catalog.setPath("/司法卷宗");
        catalog.setAyOid("201");
        catalog.setAyName("盗窃罪");
        catalog.setUploadFileList(uploadFileList);
        dzjzCatalogList.add(catalog);
        dataMap.put("dzjzCatalogList", dzjzCatalogList);
        writeXmlOut("evaluationReportResponse.ftl",
                "/Users/suzhenchao/浩云/广西社矫/datapipeapi/src/main/resources/xmlfile/evaluationReportResponse.xml",
                dataMap);
//        writeXmlOut("document.ftl",
//                "/Users/suzhenchao/浩云/广西社矫/datapipeapi/src/main/resources/xmlfile/evaluationReportResponse.xml",
//                dataMap);
    }
}
