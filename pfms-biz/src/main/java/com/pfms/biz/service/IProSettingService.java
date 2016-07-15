package com.pfms.biz.service;

import com.pfms.biz.model.ProOneBO;
import com.pfms.biz.model.ProTwoBO;
import com.pfms.dal.mybatis.model.PfmsUsageLevelOne;
import com.pfms.dal.mybatis.model.PfmsUsageLevelTwo;
import com.pfms.dal.mybatis.model.RealStatistics;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fred on 2016/1/2.
 */
public interface IProSettingService {

    public void insertProOne(PfmsUsageLevelOne pfmsUsageLevelOne);

    public void updateProOne(PfmsUsageLevelOne pfmsUsageLevelOne);

    public void deleteProOne(PfmsUsageLevelOne pfmsUsageLevelOne);

    public void insertProTwo(PfmsUsageLevelTwo pfmsUsageLevelTwo);

    public void updateProTwo(PfmsUsageLevelTwo pfmsUsageLevelTwo);

    public void deleteProTwo(PfmsUsageLevelTwo pfmsUsageLevelTwo);

    public List<PfmsUsageLevelOne> getProOne(String id, String type, Integer creatorId);

    public List<PfmsUsageLevelTwo> getProTwo(String id, String type, String parentId, Integer creatorId);

    public PfmsUsageLevelOne insertProOneWithPara(String name, String type, int userId);

    public PfmsUsageLevelTwo insertProTwoWithPara(String name, String type, int userId, String fatherId, String budget);

    public RealStatistics getOrInsertMonthBudget(String id, String month, BigDecimal money);

    public RealStatistics updateMonthBudget(String id, String month, BigDecimal money);

    public BigDecimal getParentIdBudget(String fatherId);

    public ProOneBO insertProOneAndBudget(String name, String type, int userId, String month);

    public ProTwoBO insertProTwoAndBudget(PfmsUsageLevelOne pfmsUsageLevelOne, String name, String type, int userId, String month, String proOneId, String budget);

    public ProTwoBO modifyProTwoAndBudget(PfmsUsageLevelTwo pfmsUsageLevelTwo, String proId, String proTwoNameModify, String proTwoBudgetModify, String today);

    public boolean deleteProTwoAndUptProOne(PfmsUsageLevelTwo pfmsUsageLevelTwo);

}
