/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.biz.model;

import com.pfms.dal.mybatis.model.PfmsUsageLevelOne;

import java.math.BigDecimal;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:ProOneBO.java, v0.1 2016/7/14 17:02 fred.zhou Exp $
 */
public class ProOneBO  extends PfmsUsageLevelOne {

    //一级科目月预算
    private BigDecimal budget;

    //一级科目实际月收支
    private BigDecimal realAmount;

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }
}
