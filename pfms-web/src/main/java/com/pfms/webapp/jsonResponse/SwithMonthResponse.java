/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

import com.pfms.webapp.model.FormToShow;

import java.util.LinkedList;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:SwithMonthResponse.java, v0.1 2016/7/26 16:59 fred.zhou Exp $
 */
public class SwithMonthResponse {

    //状态
    private String opstatus;
    //切换的年份
    private String opcurrentYear;
    //切换的月份
    private String opcurrentMonth;
    //收入总金额
    private String optotalAmountIn;
    //支出总金额
    private String optotalAmountOut;
    //是否有记录存在
    private String opsearchResult;
    //记录详细信息
    private LinkedList<FormToShow> opformList;

    public String getOpstatus() {
        return opstatus;
    }

    public void setOpstatus(String opstatus) {
        this.opstatus = opstatus;
    }

    public String getOpcurrentYear() {
        return opcurrentYear;
    }

    public void setOpcurrentYear(String opcurrentYear) {
        this.opcurrentYear = opcurrentYear;
    }

    public String getOpcurrentMonth() {
        return opcurrentMonth;
    }

    public void setOpcurrentMonth(String opcurrentMonth) {
        this.opcurrentMonth = opcurrentMonth;
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

    public String getOpsearchResult() {
        return opsearchResult;
    }

    public void setOpsearchResult(String opsearchResult) {
        this.opsearchResult = opsearchResult;
    }

    public LinkedList<FormToShow> getOpformList() {
        return opformList;
    }

    public void setOpformList(LinkedList<FormToShow> opformList) {
        this.opformList = opformList;
    }
}
