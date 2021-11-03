package cn.hy.gxpipeapi.xmlfile.dto;

import lombok.Data;

import java.util.List;

@Data
public class DzjzCatalogMain {

    private List<DzjzCatalog> DzjzCatalog;

    @Data
    public static class DzjzCatalog {
        private String name;
        private String code;
        private String path;
        private String ay_oid;
        private String ay_name;
        private String level ="1";
        private String is_required = "1";
        private String jg_code = "0400";
        private String sort_no = "1";
        private String data_type = "pdf,jpg,png";
        private List<UploadFile> uploadFileList;
    }

}
