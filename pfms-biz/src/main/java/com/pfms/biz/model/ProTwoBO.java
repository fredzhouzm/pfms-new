/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.biz.model;

import com.pfms.dal.mybatis.model.PfmsUsageLevelTwo;

import java.math.BigDecimal;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:ProTwoBO.java, v0.1 2016/7/15 9:48 fred.zhou Exp $
 */
public class ProTwoBO extends PfmsUsageLevelTwo {

    //父科目月预算
    private BigDecimal parentBudgetAmount;

    //父科目月实际收支
    private BigDecimal parentRealAmount;

    //科目月实际收支
    private BigDecimal realAmount;

    public BigDecimal getParentBudgetAmount() {
        return parentBudgetAmount;
    }

    public void setParentBudgetAmount(BigDecimal parentBudgetAmount) {
        this.parentBudgetAmount = parentBudgetAmount;
    }

    public BigDecimal getParentRealAmount() {
        return parentRealAmount;
    }

    public void setParentRealAmount(BigDecimal parentRealAmount) {
        this.parentRealAmount = parentRealAmount;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }
}
