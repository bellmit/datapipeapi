<?xml version="1.0" encoding="utf-8"?>

<CaseInfo name="案件基本信息">
    <ajbh name="公安机关案件编号" required="true">A4501023300002021090158</ajbh>
    <case_status name="案件状态/移送状态" required="true">060101</case_status>


    <CaseSuspect_List name="嫌疑人/被告人列表">
        <CaseSuspect name="嫌疑人/被告人信息">
            <sfrybh name="司法人员编号" required="true">002</sfrybh>


            <Dcpgyj_List name="调查评估意见信息列表">
                <Dcpgyj name="调查评估意见信息">
                    <xtoid name="协同唯一标识编号" required="true">4501030321000074</xtoid>

                    <wtdw name="委托单位">青秀区人民检察院</wtdw>
                    <wtdwbh name="委托单位编号">450103</wtdwbh>
                    <lxdh name="联系电话"></lxdh>
                    <zzyj name="最终意见">建议适用社区矫正</zzyj>
                    <dcdwxj name="调查单位(县级司法行政机关)"></dcdwxj>
                    <dcdw name="调查单位(司法所)">南宁市青秀区司法局</dcdw>
                    <dcdwbh name="调查单位编号">4500060400</dcdwbh>
                    <dcr name="调查人"></dcr>
                    <dcyjshr name="调查意见审核人"></dcyjshr>
                    <cjsj name="创建时间"></cjsj>
                    <xgsj name="修改时间"></xgsj>
                    <sjzt name="数据状态">0</sjzt>
                </Dcpgyj>
            </Dcpgyj_List>
        </CaseSuspect>

    </CaseSuspect_List>
    <CaseStatus_List name="案件移送记录列表">
            <CaseStatus name="移送记录">
                <send_dept name="发送部门代码" required="true">4500060400</send_dept>
                <send_deptname name="发送部门名称" required="true">南宁市青秀区司法局</send_deptname>
                <receive_dept name="接收部门代码" required="true">4500030200</receive_dept>
                <receive_deptname name="接收部门名称" required="true">青秀区人民检察院</receive_deptname>
                <send_time name="发送时间">2021-10-18 16:30:15</send_time>
                <send_type name="移送状态" required="true">060101</send_type>
                <send_type_zh name="移送状态名称" required="true">反馈调查评估报告</send_type_zh>
            </CaseStatus>
    </CaseStatus_List>

    <DzjzCatalog_List name="电子卷宗">
            <#list dzjzCatalogList as dzjzCatalog>
            <DzjzCatalog name="司法卷宗">
                    <name name="目录名称" required="true">${dzjzCatalog.name}</name>
                    <code name="目录代码">${dzjzCatalog.code}</code>
                    <path required="true">${dzjzCatalog.path}</path>
                    <ay_oid name="案由ID" required="true">${dzjzCatalog.ayOid}</ay_oid>
                    <ay_name name="案由名称" required="true">${dzjzCatalog.ayName}</ay_name>
                    <level name="等级">${dzjzCatalog.level}</level>
                    <is_required name="是否必须">${dzjzCatalog.isRequired}</is_required>
                    <jg_code name="机构标识" required="true">${dzjzCatalog.jgCode}</jg_code>
                    <sort_no name="排序">${dzjzCatalog.sortNo}</sort_no>
                    <data_type name="数据类型" required="true">${dzjzCatalog.dataType}</data_type>
                    <UploadFile_List name="附件列表">
                        <#list dzjzCatalog.uploadFileList as uploadFile>
                        <UploadFile name="调查评估意见书.pdf">
                            <path required="true">${uploadFile.path}</path>
                            <name required="true">${uploadFile.name}</name>
                            <sort_no required="true">${uploadFile.sortNo}</sort_no>
                        </UploadFile>
                        </#list>
                    </UploadFile_List>
            </DzjzCatalog>
            </#list>
    </DzjzCatalog_List>

</CaseInfo>
