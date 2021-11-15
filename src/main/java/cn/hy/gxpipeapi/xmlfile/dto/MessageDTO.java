package cn.hy.gxpipeapi.xmlfile.dto;

import cn.hy.gxpipeapi.util.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
public class MessageDTO {
    @NotBlank(message = "企业编号,必填！")
    private String SpCode;
    @NotBlank(message = "用户名,必填！")
    private String LoginName;
    @NotBlank(message = "接口密钥,必填！")
    private String Password;
    @NotBlank(message = "短信内容,必填！")
    private String MessageContent;
    @NotBlank(message = "手机号码,必填！")
    private String UserNumber;
    private String templateId;
    @NotBlank(message = "流水号(20位数字),必填！")
    private String SerialNumber;
    private String ScheduleTime = "";
    private String f = "1";

    public MessageDTO(String spCode, String loginName, String password, String messageContent, String userNumber, String templateId, String serialNumber) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        ScheduleTime = simpleDateFormat.format(new Date());
        SpCode = spCode;
        LoginName = loginName;
        Password = password;
        MessageContent = MessageUtil.getStrGbk(messageContent);
        UserNumber = userNumber;
        this.templateId = templateId;
        SerialNumber = serialNumber;
        f = "1";
    }
}
