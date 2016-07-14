/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:ModifyProOneResponse.java, v0.1 2016/7/14 15:31 fred.zhou Exp $
 */
public class ModifyProOneResponse {

    //状态
    private String opstatus = "";

    //一级科目类型
    private String optype = "";

    //一级科目ID
    private String opid = "";

    //一级科目名称
    private String opname = "";

    public String getOpstatus() {
        return opstatus;
    }

    public void setOpstatus(String opstatus) {
        this.opstatus = opstatus;
    }

    public String getOptype() {
        return optype;
    }

    public void setOptype(String optype) {
        this.optype = optype;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getOpname() {
        return opname;
    }

    public void setOpname(String opname) {
        this.opname = opname;
    }
}
