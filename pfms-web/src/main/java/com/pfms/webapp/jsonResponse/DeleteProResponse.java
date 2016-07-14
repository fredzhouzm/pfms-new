/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:DeleteProResponse.java, v0.1 2016/7/14 15:52 fred.zhou Exp $
 */
public class DeleteProResponse {

    //状态
    private String opstatus = "";

    //科目类型
    private String optype = "";

    //科目级别
    private String oplvl = "";

    //科目ID
    private String opid = "";

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

    public String getOplvl() {
        return oplvl;
    }

    public void setOplvl(String oplvl) {
        this.oplvl = oplvl;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }
}
