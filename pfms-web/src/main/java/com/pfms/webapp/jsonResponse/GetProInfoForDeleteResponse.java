/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:GetProInfoForDeleteResponse.java, v0.1 2016/7/14 16:09 fred.zhou Exp $
 */
public class GetProInfoForDeleteResponse {

    //状态
    private String opstatus = "";

    //二级科目名称
    private String optname = "";

    //二级科目月预算
    private String opbudget = "";

    public String getOpstatus() {
        return opstatus;
    }

    public void setOpstatus(String opstatus) {
        this.opstatus = opstatus;
    }

    public String getOptname() {
        return optname;
    }

    public void setOptname(String optname) {
        this.optname = optname;
    }

    public String getOpbudget() {
        return opbudget;
    }

    public void setOpbudget(String opbudget) {
        this.opbudget = opbudget;
    }
}
