package cn.hy.gxpipeapi.util;

import cn.hy.gxpipeapi.xmlfile.dto.MessageDTO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class MessageUtil {
    private static String SpCode;
    private static String LoginName;
    private static String Password;
    private static String url;

    @Value("${message.SpCode:244531}")
    public void setSpCode(String SpCode) {
        MessageUtil.SpCode = SpCode;
    }

    @Value("${message.LoginName:sqjzxt}")
    public void setLoginName(String LoginName) {
        MessageUtil.LoginName = LoginName;
    }

    @Value("${message.Password:f6c2d1f1fac0e1f0b77cd640694d809e}")
    public void setPassword(String password) {
        MessageUtil.Password = password;
    }

    @Value("${message.url:https://api.ums86.com:9600/sms/Api/Send.do}")
    public void setUrl(String url) {
        MessageUtil.url = url;
    }

    @PostConstruct
    public static void doPose() {
        doPoseMessage();
//        get();

    }

    public static String poseMessage(MessageDTO messageDTO) {
        String info = null;
        try{
            HttpClient httpclient = new HttpClient();
            PostMethod post = new PostMethod(url);//
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"gbk");
            post.addParameter("SpCode", SpCode);
            post.addParameter("LoginName", LoginName);
            post.addParameter("Password", Password);
            post.addParameter("MessageContent", messageDTO.getMessageContent());
            post.addParameter("UserNumber", messageDTO.getUserNumber());
            post.addParameter("templateId", messageDTO.getTemplateId());
            post.addParameter("SerialNumber", messageDTO.getSerialNumber());
            post.addParameter("ScheduleTime", messageDTO.getScheduleTime());
            post.addParameter("f", messageDTO.getF());
            httpclient.executeMethod(post);
            info = new String(post.getResponseBody(),"gbk");
            return info;
        }catch (Exception e) {
            throw new RuntimeException("发送手机短信失败！");
        }
    }

    public static void main(String[] args) {
        String info = null;
        try{
            HttpClient httpclient = new HttpClient();
            PostMethod post = new PostMethod("https://api.ums86.com:9600/sms/Api/Send.do");//
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"gbk");
            post.addParameter("SpCode", "244531");
            post.addParameter("LoginName", "sqjzxt");
            post.addParameter("Password", "f6c2d1f1fac0e1f0b77cd640694d809e");
            post.addParameter("MessageContent", "【好好先生】您申请的探视预约已撤销！撤销原因【这是原因。】");
            post.addParameter("UserNumber", "13411993590");
            post.addParameter("templateId", "2201012125062");
            post.addParameter("SerialNumber", "");
            post.addParameter("ScheduleTime", "");
            post.addParameter("f", "1");
            httpclient.executeMethod(post);
            info = new String(post.getResponseBody(),"gbk");
            System.out.println(info);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean doPoseMessage() {
        String messageContent = "【好好先生】您申请的探视预约已撤销！撤销原因【这是原因。】";
        String userNumber = "13411993590";
        String templateId = "2201012125062";
        String serialNumber = String.valueOf(System.currentTimeMillis());
        MessageDTO messageDTO = new MessageDTO(SpCode, LoginName, Password, messageContent, userNumber, templateId, serialNumber);
        Map<String, String> poseMap = new HashMap<>();
        poseMap.put("MessageContent", messageDTO.getMessageContent());
        poseMap.put("userNumber", "13411993590");
        poseMap.put("templateId", "2201012125062");
        poseMap.put("SpCode", SpCode);
        poseMap.put("LoginName", LoginName);
        poseMap.put("Password", Password);
        poseMap.put("f", "1");
//        String returnStr = HttpClientUtil.doPostJson(url, JSON.toJSONString(messageDTO));
        String returnStr = HttpClientUtil.postMap(url, poseMap);
        System.out.println("返回结果：" + returnStr);
        return true;
    }

    public static boolean get() {
        String messageContent = "好好先生向您发送了短信测试数据的传阅消息。请及时查收。";
        messageContent = getStrGbk(messageContent);
        HttpClientUtil.get("https://api.ums86.com:9600/sms/Api/Send.do?SpCode=244531&LoginName=sqjzxt&Password=f6c2d1f1fac0e1f0b77cd640694d809e&MessageContent=" + messageContent + "&UserNumber=13411993590&templateId=2201012041437&SerialNumber=&ScheduleTime=&f=1");
        return true;
    }

    public static String getStrGbk(String messageContent) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] bytes = messageContent.getBytes("GBK");
            for (byte b : bytes) {
                sb.append("%").append(Integer.toHexString((b & 0xff)).toUpperCase());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return sb.toString();
    }


}
