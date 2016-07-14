/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:AddProTwoResponse.java, v0.1 2016/7/14 15:15 fred.zhou Exp $
 */
public class AddProTwoResponse {

    //状态
    private String opstatus = "";

    //二级科目类型
    private String optype = "";

    //二级科目父科目ID
    private String opparentid = "";

    //二级科目ID
    private String opid = "";

    //二级科目名称
    private String opname = "";

    //二级科目父科目月预算
    private String opparentbudgetamount = "";

    //二级科目父科目月实际收支
    private String opparentrealamount = "";

    //二级科目月预算
    private String opbudgetamount = "";

    //二级科目月实际收支
    private String oprealamount = "";

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

    public String getOpparentid() {
        return opparentid;
    }

    public void setOpparentid(String opparentid) {
        this.opparentid = opparentid;
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

    public String getOpparentbudgetamount() {
        return opparentbudgetamount;
    }

    public void setOpparentbudgetamount(String opparentbudgetamount) {
        this.opparentbudgetamount = opparentbudgetamount;
    }

    public String getOpparentrealamount() {
        return opparentrealamount;
    }

    public void setOpparentrealamount(String opparentrealamount) {
        this.opparentrealamount = opparentrealamount;
    }

    public String getOpbudgetamount() {
        return opbudgetamount;
    }

    public void setOpbudgetamount(String opbudgetamount) {
        this.opbudgetamount = opbudgetamount;
    }

    public String getOprealamount() {
        return oprealamount;
    }

    public void setOprealamount(String oprealamount) {
        this.oprealamount = oprealamount;
    }
}
