package com.pfms.biz.service.impl;

import com.pfms.biz.service.IProSettingService;
import com.pfms.biz.service.ISequenceService;
import com.pfms.dal.mybatis.dao.CustPfmsUsageLevelTwoMapper;
import com.pfms.dal.mybatis.dao.PfmsUsageLevelOneMapper;
import com.pfms.dal.mybatis.dao.PfmsUsageLevelTwoMapper;
import com.pfms.dal.mybatis.dao.RealStatisticsMapper;
import com.pfms.dal.mybatis.model.*;
import com.pfms.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Fred on 2016/1/3.
 */
@Scope("singleton")
@Service
public class ProSettingServiceImp implements IProSettingService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PfmsUsageLevelOneMapper pfmsUsageLevelOneMapper;

    @Autowired
    PfmsUsageLevelTwoMapper pfmsUsageLevelTwoMapper;

    @Autowired
    CustPfmsUsageLevelTwoMapper custPfmsUsageLevelTwoMapper;

    @Autowired
    ISequenceService sequenceService;

    @Autowired
    RealStatisticsMapper realStatisticsMapper;

    @Override
    public void insertProOne(PfmsUsageLevelOne pmfsUsageOne) {
        pfmsUsageLevelOneMapper.insert(pmfsUsageOne);
    }

    @Override
        public void updateProOne(PfmsUsageLevelOne pfmsUsageLevelOne) {
        pfmsUsageLevelOneMapper.updateByPrimaryKeySelective(pfmsUsageLevelOne);
    }

    @Override
    public void deleteProOne(PfmsUsageLevelOne pfmsUsageLevelOne) {
        pfmsUsageLevelOneMapper.deleteByPrimaryKey(pfmsUsageLevelOne.getId());
    }

    @Override
    public void insertProTwo(PfmsUsageLevelTwo pfmsUsageLevelTwo) {
        pfmsUsageLevelTwoMapper.insert(pfmsUsageLevelTwo);
    }

    @Override
    public void updateProTwo(PfmsUsageLevelTwo pfmsUsageLevelTwo) {
        pfmsUsageLevelTwoMapper.updateByPrimaryKeySelective(pfmsUsageLevelTwo);
    }

    @Override
    public void deleteProTwo(PfmsUsageLevelTwo pfmsUsageLevelTwo) {
        pfmsUsageLevelTwoMapper.deleteByPrimaryKey(pfmsUsageLevelTwo.getId());
    }

    @Override
    public List<PfmsUsageLevelOne> getProOne(String id, String type, Integer creatorId) {

        PfmsUsageLevelOneExample pfmsUsageLevelOneExample = new PfmsUsageLevelOneExample();
        PfmsUsageLevelOneExample.Criteria criteria = pfmsUsageLevelOneExample.createCriteria();
        if (id != null) {
            criteria.andIdEqualTo(id);
        }
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }
        if (creatorId != null) {
            criteria.andCreatorIdEqualTo(creatorId);
        }
        pfmsUsageLevelOneExample.setOrderByClause("ID ASC");
        return pfmsUsageLevelOneMapper.selectByExample(pfmsUsageLevelOneExample);
    }

    @Override
    public List<PfmsUsageLevelTwo> getProTwo(String id, String type, String parentId, Integer creatorId) {

        PfmsUsageLevelTwoExample pfmsUsageLevelTwoExample = new PfmsUsageLevelTwoExample();
        PfmsUsageLevelTwoExample.Criteria criteria = pfmsUsageLevelTwoExample.createCriteria();
        if (id != null) {
            criteria.andIdEqualTo(id);
        }
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }
        if (parentId != null) {
            criteria.andFatherIdEqualTo(parentId);
        }
        if (creatorId != null) {
            criteria.andCreatorIdEqualTo(creatorId);
        }
        pfmsUsageLevelTwoExample.setOrderByClause("ID ASC");
        return pfmsUsageLevelTwoMapper.selectByExample(pfmsUsageLevelTwoExample);
    }


    @Override
    public PfmsUsageLevelOne insertProOneWithPara(String name, String type, int userId) {
        PfmsUsageLevelOne pfmsUsageLevelOne = new PfmsUsageLevelOne();
        pfmsUsageLevelOne.setId(sequenceService.getId(Constants.SEQ_PRO_ONE_ID));//一级科目ID
        pfmsUsageLevelOne.setType(type);//"1"为收入一级科目，"2"为支出一级科目
        pfmsUsageLevelOne.setName(name);//一级科目名称
        pfmsUsageLevelOne.setMonthbudget(BigDecimal.valueOf(0.00));//一级科目预算金额
        pfmsUsageLevelOne.setCreatorId(userId);//创建者/所属者ID
        pfmsUsageLevelOne.setCreateTime(new Date());//创建时间
        pfmsUsageLevelOne.setDescript("");//描述，默认为空
        insertProOne(pfmsUsageLevelOne);
        return pfmsUsageLevelOne;
    }

    @Override
    public PfmsUsageLevelTwo insertProTwoWithPara(String name, String type, int userId, String fatherId, String budget) {
        PfmsUsageLevelTwo pfmsUsageLevelTwo = new PfmsUsageLevelTwo();
        pfmsUsageLevelTwo.setId(sequenceService.getId(Constants.SEQ_PRO_TWO_ID));//二级科目ID
        pfmsUsageLevelTwo.setType(type);//"1"为收入二级科目，"2"为支出二级科目
        pfmsUsageLevelTwo.setName(name);//二级科目名称
        pfmsUsageLevelTwo.setFatherId(fatherId);//二级科目父ID
        pfmsUsageLevelTwo.setMonthbudget(new BigDecimal(budget));//二级科目预算金额
        pfmsUsageLevelTwo.setCreatorId(userId);//创建者/所属者ID
        pfmsUsageLevelTwo.setCreateTime(new Date());//创建时间
        pfmsUsageLevelTwo.setDescript("");//描述，默认为空
        insertProTwo(pfmsUsageLevelTwo);
        return pfmsUsageLevelTwo;
    }

    @Override
    public RealStatistics getOrInsertMonthBudget(String id, String month, BigDecimal money) {
        RealStatisticsExample realStatisticsExample = new RealStatisticsExample();
        realStatisticsExample.createCriteria().andIdEqualTo(id).andMonthEqualTo(month);
        List<RealStatistics> realStatisticses = realStatisticsMapper.selectByExample(realStatisticsExample);
        if(realStatisticses.size() < 1){
            RealStatistics realStatistics = new RealStatistics();
            realStatistics.setId(id);
            realStatistics.setMonth(month);
            realStatistics.setBudget(money);
            realStatistics.setRealamount(new BigDecimal(0.00));
            realStatisticsMapper.insert(realStatistics);
            return realStatistics;
        }
        else{
            return realStatisticses.get(0);
        }
    }

    @Override
    public RealStatistics updateMonthBudget(String id, String month, BigDecimal money) {
        RealStatisticsExample realStatisticsExample = new RealStatisticsExample();
        realStatisticsExample.createCriteria().andIdEqualTo(id).andMonthEqualTo(month);
        List<RealStatistics> realStatisticses = realStatisticsMapper.selectByExample(realStatisticsExample);
        if(realStatisticses.size() < 1){
            RealStatistics realStatistics = new RealStatistics();
            realStatistics.setId(id);
            realStatistics.setMonth(month);
            realStatistics.setBudget(money);
            realStatistics.setRealamount(new BigDecimal(0.00));
            realStatisticsMapper.insert(realStatistics);
            return realStatistics;
        }
        else{
            RealStatistics realStatistics = realStatisticses.get(0);
            realStatistics.setBudget(money);
            realStatisticsMapper.updateByPrimaryKeySelective(realStatistics);
            return realStatistics;
        }
    }

    @Override
    public BigDecimal getParentIdBudget(String fatherId) {
        BigDecimal sumNum = null;
        sumNum = custPfmsUsageLevelTwoMapper.getAddResultByFatherId(fatherId);
        return sumNum;
    }
}
