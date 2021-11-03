package cn.hy.gxpipeapi.util;
import cn.hy.gxpipeapi.xmlfile.dto.CaseInfoMain;
import cn.hy.gxpipeapi.xmlfile.dto.UploadFile;

import cn.hy.gxpipeapi.xmlfile.dto.DzjzCatalogMain;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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

    /**
     * 将xml转换为JSON对象
     * @param xmlPath xml文件地址
     * @return
     * @throws Exception
     */
    public static JSONObject xml2Json(String xmlPath){
        String xml;
        try (InputStream in = new FileInputStream(xmlPath)) {
            xml = IOUtils.toString(in);
            JSONObject jsonObject = new JSONObject();
            Document document = DocumentHelper.parseText(xml);
            //获取根节点元素对象
            Element root = document.getRootElement();
            iterateNodes(root, jsonObject);
            return jsonObject;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }
    /**
     * 遍历元素
     * @param node 元素
     * @param json 将元素遍历完成之后放的JSON对象
     */
    @SuppressWarnings("unchecked")
    public static void iterateNodes(Element node,JSONObject json){
        //获取当前元素的名称
        String nodeName = node.getName();
        //判断已遍历的JSON中是否已经有了该元素的名称
        if(json.containsKey(nodeName)){
            //该元素在同级下有多个
            Object Object = json.get(nodeName);
            JSONArray array = null;
            if(Object instanceof JSONArray){
                array = (JSONArray) Object;
            }else {
                array = new JSONArray();
                array.add(Object);
            }
            //获取该元素下所有子元素
            List<Element> listElement = node.elements();
            if(listElement.isEmpty()){
                //该元素无子元素，获取元素的值
                String nodeValue = node.getTextTrim();
                array.add(nodeValue);
                json.put(nodeName, array);
                return ;
            }
            //有子元素
            JSONObject newJson = new JSONObject();
            //遍历所有子元素
            for(Element e:listElement){
                //递归
                iterateNodes(e,newJson);
            }
            array.add(newJson);
            json.put(nodeName, array);
            return ;
        }
        //该元素同级下第一次遍历
        //获取该元素下所有子元素
        List<Element> listElement = node.elements();
        if(listElement.isEmpty()){
            //该元素无子元素，获取元素的值
            String nodeValue = node.getTextTrim();
            json.put(nodeName, nodeValue);
            return ;
        }
        //有子节点，新建一个JSONObject来存储该节点下子节点的值
        JSONObject object = new JSONObject();
        //遍历所有一级子节点
        for(Element e:listElement){
            //递归
            iterateNodes(e,object);
        }
        json.put(nodeName, object);
    }

    public static void main(String[] args) {
//        fillXML();
        String path = "/Users/suzhenchao/浩云/广西社矫/datapipeapi/src/main/resources/xmlfile/evaluationReportResponse.xml";
        JSONObject jsonObject = xml2Json(path);
        CaseInfoMain caseInfoMain = (CaseInfoMain) JSONObject.parseObject(JSON.toJSONString(jsonObject), CaseInfoMain.class);
        System.out.println(caseInfoMain);

    }

    private static void fillXML() {
        Map<String, Object> dataMap = new HashMap<>();
        List<DzjzCatalogMain> dzjzCatalogList = new ArrayList<>();
        List<UploadFile> uploadFileList = new ArrayList<>();
        UploadFile uploadFile = new UploadFile();
        uploadFile.setPath("/司法卷宗/调查评估意见书.pdf");
        uploadFile.setName("调查评估意见书.pdf");
        uploadFile.setSort_no("1");
        uploadFileList.add(uploadFile);

        UploadFile uploadFile1 = new UploadFile();
        uploadFile1.setPath("/司法卷宗/调查评估意见书2.pdf");
        uploadFile1.setName("调查评估意见书2.pdf");
        uploadFile1.setSort_no("2");
        uploadFileList.add(uploadFile1);

        DzjzCatalogMain catalog = new DzjzCatalogMain();
//        catalog.setName("司法卷宗");
//        catalog.setCode("SFJZ");
//        catalog.setPath("/司法卷宗");
//        catalog.setAy_oid("201");
//        catalog.setAy_name("盗窃罪");
//        catalog.setUploadFileList(uploadFileList);
        dzjzCatalogList.add(catalog);
        dataMap.put("dzjzCatalogList", dzjzCatalogList);
        writeXmlOut("evaluationReportResponse.ftl",
                "F:\\datapipeapi\\src\\main\\resources\\xmlfile\\evaluationReportResponse.xml",
                dataMap);
//        writeXmlOut("document.ftl",
//                "/Users/suzhenchao/浩云/广西社矫/datapipeapi/src/main/resources/xmlfile/evaluationReportResponse.xml",
//                dataMap);
    }
}
