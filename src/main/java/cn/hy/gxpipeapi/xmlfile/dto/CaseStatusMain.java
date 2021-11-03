package cn.hy.gxpipeapi.xmlfile.dto;

import lombok.Data;

import java.util.List;

@Data
public class CaseStatusMain {
    private List<CaseStatus> CaseStatus;

    @Data
    public static class CaseStatus {
        private String send_dept;
        private String send_deptname;
        private String receive_dept;
        private String receive_deptname;
        private String send_time;
        private String send_type;
        private String send_type_zh;
    }

}
