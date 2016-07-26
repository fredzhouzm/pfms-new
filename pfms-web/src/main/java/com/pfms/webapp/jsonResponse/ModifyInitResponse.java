/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

import java.util.HashMap;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:ModifyInitResponse.java, v0.1 2016/7/26 17:08 fred.zhou Exp $
 */
public class ModifyInitResponse {

    //状态
    private String opstatus;
    //金额
    private String opamount;
    //一级科目
    private String opproone;
    //二级科目
    private String protwo;
    //日期
    private String opdate;
    //时段
    private String opperoid;
    //备注
    private String opremark;
    //二级科目列表
    private HashMap<String, String> opprotwolist;

    public String getOpstatus() {
        return opstatus;
    }

    public void setOpstatus(String opstatus) {
        this.opstatus = opstatus;
    }

    public String getOpamount() {
        return opamount;
    }

    public void setOpamount(String opamount) {
        this.opamount = opamount;
    }

    public String getOpproone() {
        return opproone;
    }

    public void setOpproone(String opproone) {
        this.opproone = opproone;
    }

    public String getProtwo() {
        return protwo;
    }

    public void setProtwo(String protwo) {
        this.protwo = protwo;
    }

    public String getOpdate() {
        return opdate;
    }

    public void setOpdate(String opdate) {
        this.opdate = opdate;
    }

    public String getOpperoid() {
        return opperoid;
    }

    public void setOpperoid(String opperoid) {
        this.opperoid = opperoid;
    }

    public String getOpremark() {
        return opremark;
    }

    public void setOpremark(String opremark) {
        this.opremark = opremark;
    }

    public HashMap<String, String> getOpprotwolist() {
        return opprotwolist;
    }

    public void setOpprotwolist(HashMap<String, String> opprotwolist) {
        this.opprotwolist = opprotwolist;
    }
}
