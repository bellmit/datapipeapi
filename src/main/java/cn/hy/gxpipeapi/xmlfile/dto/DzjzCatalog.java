package cn.hy.gxpipeapi.xmlfile.dto;

import lombok.Data;

import java.util.List;

@Data
public class DzjzCatalog {
    private String name;
    private String code;
    private String path;
    private String ayOid;
    private String ayName;
    private String level ="1";
    private String isRequired = "1";
    private String jgCode = "0400";
    private String sortNo = "1";
    private String dataType = "pdf,jpg,png";
    private List<UploadFile> uploadFileList;
}
