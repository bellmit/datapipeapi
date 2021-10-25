package cn.hy.gxpipeapi.xmlfile.dto;

import lombok.Data;

import java.util.List;

@Data
public class CaseInfo {
    /**
     * 案件编号
     */
    private String caseNo;

    /**
     * 案件状态/移送状态
     */
    private String caseStatus;

    /**
     * 案件移送记录列表
     */
    private List<CaseStatus> caseStatusList;

    /**
     * 电子卷宗
     */
    private List<DzjzCatalog> dzjzCatalogList;
}
