/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:AddProOneResponse.java, v0.1 2016/7/14 14:37 fred.zhou Exp $
 */
public class AddProOneResponse {

    //状态
    private String opstatus = "";

    //一级科目类型
    private String optype = "";

    //一级科目ID
    private String opid = "";

    //一级科目名称
    private String opname = "";

    //一级科目月实际费用
    private String oprealMmount = "";

    //一级科目月预算
    private String opbudgetMmount = "";

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

    public String getOprealMmount() {
        return oprealMmount;
    }

    public void setOprealMmount(String oprealMmount) {
        this.oprealMmount = oprealMmount;
    }

    public String getOpbudgetMmount() {
        return opbudgetMmount;
    }

    public void setOpbudgetMmount(String opbudgetMmount) {
        this.opbudgetMmount = opbudgetMmount;
    }
}
