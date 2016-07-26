/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:DeleteFormResponse.java, v0.1 2016/7/26 17:09 fred.zhou Exp $
 */
public class DeleteFormResponse {

    //状态
    private String opstatus;
    //单据ID
    private String opid;
    //单据类型
    private String optype;
    //页面选中月份单据个数
    private int count;
    //页面选中月份总收入金额
    private String optotalAmountIn;
    //页面选中月份总支出金额
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
