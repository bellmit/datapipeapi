package cn.hy.gxpipeapi.util;

import cn.hy.gxpipeapi.xmlfile.dto.MessageDTO;
import cn.hy.gxpipeapi.xmlfile.dto.MessageResultDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.util.Optional;


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

//    @PostConstruct
    public static void doPose() {
//        doPoseMessage();
//        get();

    }

    public static MessageResultDTO poseMessage(MessageDTO messageDTO) {
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
            String returnStr = Optional.of(info).orElseThrow(() -> new RuntimeException("发送手机短信失败！"));
            System.out.println("返回结果：" + returnStr);
            MessageResultDTO messageResultDTO = getMessageResultDTO(returnStr);
            if (!"0".equalsIgnoreCase(messageResultDTO.getResult())) {
                throw new RuntimeException("发送手机短信失败:" + messageResultDTO.getDescription());
            }
            return messageResultDTO;
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

    public static MessageResultDTO getMessageResultDTO(String result) {
        String[] resultStrArr = result.split("&");
        Map<String, Object> resultMap = new HashMap<>();
        for (String resultStr : resultStrArr) {
            String[] resultArr = resultStr.split("=");
            String value = "";
            if (resultArr.length == 2) {
                value = Optional.ofNullable(resultArr[1]).orElse("");
            }
            resultMap.put(resultArr[0], value);
        }
        JSONObject resultJSON = new JSONObject(resultMap);
        return JSONObject.parseObject(resultJSON.toJSONString(), MessageResultDTO.class);
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
