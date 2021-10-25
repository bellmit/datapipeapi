package cn.hy.gxpipeapi.xmlfile.dto;

import lombok.Data;

@Data
public class CaseStatus {
    private String sendDept;
    private String sendDeptName;
    private String receiveDept;
    private String receiveDeptName;
    private String sendTime;
    private String sendType;
    private String sendTypeZH;
}
