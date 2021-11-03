package cn.hy.gxpipeapi.xmlfile.dto;

import lombok.Data;

import java.util.List;

@Data
public class CaseInfo {
    /**
     * 案件编号
     */
    private String ajbh;

    /**
     * 案件状态/移送状态
     */
    private String case_status;

    /**
     * 案件移送记录列表
     */
    private List<CaseStatusMain> CaseStatus_List;

    /**
     * 电子卷宗
     */
    private List<DzjzCatalogMain> DzjzCatalog_List;
}
