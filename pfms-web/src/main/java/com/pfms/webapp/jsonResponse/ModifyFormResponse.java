/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:ModifyFormResponse.java, v0.1 2016/7/26 17:08 fred.zhou Exp $
 */
public class ModifyFormResponse {

    //状态
    private String opstatus;
    //单据ID
    private String opid;
    //单据类型
    private String optype;
    //单据金额
    private String opamount;
    //单据日期
    private String opdate;
    //单据时段
    private String opperiod;
    //单据科目
    private String oppro;
    //单据备注
    private String opremark;
    //页面选中月份
    private String opselectedMonth;
    //页面选中月份收入
    private String optotalAmountIn;
    //页面选中月份支出
    private String optotalAmountOut;

    public String getOpstatus() {
        return opstatus;
    }

    public void setOpstatus(String opstatus) {
        this.opstatus = opstatus;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getOptype() {
        return optype;
    }

    public void setOptype(String optype) {
        this.optype = optype;
    }

    public String getOpamount() {
        return opamount;
    }

    public void setOpamount(String opamount) {
        this.opamount = opamount;
    }

    public String getOpdate() {
        return opdate;
    }

    public void setOpdate(String opdate) {
        this.opdate = opdate;
    }

    public String getOpperiod() {
        return opperiod;
    }

    public void setOpperiod(String opperiod) {
        this.opperiod = opperiod;
    }

    public String getOppro() {
        return oppro;
    }

    public void setOppro(String oppro) {
        this.oppro = oppro;
    }

    public String getOpremark() {
        return opremark;
    }

    public void setOpremark(String opremark) {
        this.opremark = opremark;
    }

    public String getOpselectedMonth() {
        return opselectedMonth;
    }

    public void setOpselectedMonth(String opselectedMonth) {
        this.opselectedMonth = opselectedMonth;
    }

    public String getOptotalAmountIn() {
        return optotalAmountIn;
    }

    public void setOptotalAmountIn(String optotalAmountIn) {
        this.optotalAmountIn = optotalAmountIn;
    }

    public String getOptotalAmountOut() {
        return optotalAmountOut;
    }

    public void setOptotalAmountOut(String optotalAmountOut) {
        this.optotalAmountOut = optotalAmountOut;
    }
}
