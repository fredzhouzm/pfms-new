package com.pfms.biz.service.impl;

import com.pfms.biz.model.ProOneBO;
import com.pfms.biz.model.ProTwoBO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Fred on 2016/1/3.
 */
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
    @Transactional
    public void updateProOne(PfmsUsageLevelOne pfmsUsageLevelOne) {
        pfmsUsageLevelOneMapper.updateByPrimaryKeySelective(pfmsUsageLevelOne);
    }

    @Override
    @Transactional
    public void deleteProOne(PfmsUsageLevelOne pfmsUsageLevelOne) {
        pfmsUsageLevelOneMapper.deleteByPrimaryKey(pfmsUsageLevelOne.getId());
    }

    @Override
    public void insertProTwo(PfmsUsageLevelTwo pfmsUsageLevelTwo) {
        pfmsUsageLevelTwoMapper.insert(pfmsUsageLevelTwo);
    }

    @Override
    @Transactional
    public void updateProTwo(PfmsUsageLevelTwo pfmsUsageLevelTwo) {
        pfmsUsageLevelTwoMapper.updateByPrimaryKeySelective(pfmsUsageLevelTwo);
    }

    @Override
    @Transactional
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
    @Transactional
    public PfmsUsageLevelOne insertProOneWithPara(String name, String type, int userId) {
        PfmsUsageLevelOne pfmsUsageLevelOne = new PfmsUsageLevelOne();
        pfmsUsageLevelOne.setId(sequenceService.getId(Constants.SEQ_PRO_ONE_ID));//一级科目ID
        pfmsUsageLevelOne.setType(type);//"1"为收入一级科目，"2"为支出一级科目
        pfmsUsageLevelOne.setName(name);//一级科目名称
        pfmsUsageLevelOne.setMonthbudget(new BigDecimal(0.00));//一级科目预算金额
        pfmsUsageLevelOne.setCreatorId(userId);//创建者/所属者ID
        pfmsUsageLevelOne.setCreateTime(new Date());//创建时间
        pfmsUsageLevelOne.setDescript("");//描述，默认为空
        insertProOne(pfmsUsageLevelOne);
        return pfmsUsageLevelOne;
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
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

    @Override
    @Transactional
    public ProOneBO insertProOneAndBudget(String name, String type, int userId, String month) {

        ProOneBO proOneBO = new ProOneBO();

        try {
            PfmsUsageLevelOne pfmsUsageLevelOne = insertProOneWithPara(name, type, userId);
            RealStatistics realStatisticses = getOrInsertMonthBudget(pfmsUsageLevelOne.getId(), month, pfmsUsageLevelOne.getMonthbudget());
            BeanUtils.copyProperties(pfmsUsageLevelOne, proOneBO);
            proOneBO.setBudget(realStatisticses.getBudget());
            proOneBO.setRealAmount(realStatisticses.getRealamount());
            return proOneBO;
        }catch (Exception e){
            logger.info("ERROR:[新增一级科目失败，原因是数据库插入失败]");
            return  null;
        }
    }

    @Override
    @Transactional
    public ProTwoBO insertProTwoAndBudget(PfmsUsageLevelOne pfmsUsageLevelOne, String name, String type, int userId, String month, String proOneId, String budget) {

        ProTwoBO proTwoBO = new ProTwoBO();

        try {
            PfmsUsageLevelTwo pfmsUsageLevelTwo = insertProTwoWithPara(name, type, userId, proOneId, budget);
            //向预算表中插入当月相关的二级科目
            getOrInsertMonthBudget(pfmsUsageLevelTwo.getId(), month, pfmsUsageLevelTwo.getMonthbudget());
            //更新一级科目
            BigDecimal bigDecimal = pfmsUsageLevelOne.getMonthbudget();
            BigDecimal newBigDecimal = bigDecimal.add(pfmsUsageLevelTwo.getMonthbudget());
            pfmsUsageLevelOne.setMonthbudget(newBigDecimal);
            updateProOne(pfmsUsageLevelOne);
            //在预算表中更新当月相关的一级科目
            RealStatistics realStatistics = updateMonthBudget(pfmsUsageLevelOne.getId(), month, newBigDecimal);

            BeanUtils.copyProperties(pfmsUsageLevelTwo, proTwoBO);
            proTwoBO.setParentBudgetAmount(realStatistics.getBudget());
            proTwoBO.setParentRealAmount(realStatistics.getRealamount());
            proTwoBO.setRealAmount(new BigDecimal(0.00));

            return proTwoBO;
        }catch (Exception e){
            logger.info("ERROR:[新增二级科目失败，原因是数据库插入失败]");
            return null;
        }
    }

    @Override
    @Transactional
    public ProTwoBO modifyProTwoAndBudget(PfmsUsageLevelTwo pfmsUsageLevelTwo, String proId, String proTwoNameModify, String proTwoBudgetModify, String today) {

        ProTwoBO proTwoBO = new ProTwoBO();

        try{
            String parentId = pfmsUsageLevelTwo.getFatherId();
            pfmsUsageLevelTwo.setName(proTwoNameModify);
            pfmsUsageLevelTwo.setMonthbudget(new BigDecimal(proTwoBudgetModify));
            updateProTwo(pfmsUsageLevelTwo);

            BigDecimal sumNum = getParentIdBudget(parentId);
            List<PfmsUsageLevelOne> pfmsUsageLevelOnes = getProOne(parentId, null, null);
            PfmsUsageLevelOne pfmsUsageLevelOne = pfmsUsageLevelOnes.get(0);
            pfmsUsageLevelOne.setMonthbudget(sumNum);
            updateProOne(pfmsUsageLevelOne);

            RealStatistics realStatisticsForProTwo = updateMonthBudget(proId,today,new BigDecimal(proTwoBudgetModify));
            RealStatistics realStatisticsForProOne = updateMonthBudget(parentId,today,sumNum);

            BeanUtils.copyProperties(pfmsUsageLevelTwo, proTwoBO);
            proTwoBO.setParentBudgetAmount(sumNum);
            proTwoBO.setParentRealAmount(realStatisticsForProOne.getRealamount());
            proTwoBO.setRealAmount(realStatisticsForProTwo.getRealamount());

            return proTwoBO;
        }catch (Exception e){
            logger.info("ERROR:[修改二级科目失败，原因是数据库操作失败]");
            return null;
        }
    }

    @Override
    @Transactional
    public boolean deleteProTwoAndUptProOne(PfmsUsageLevelTwo pfmsUsageLevelTwo) {
        try {
            String parentId = pfmsUsageLevelTwo.getFatherId();
            BigDecimal budgetForProTwo = pfmsUsageLevelTwo.getMonthbudget();
            PfmsUsageLevelOne pfmsUsageLevelOne = getProOne(parentId, null, null).get(0);
            BigDecimal budgetForFather = pfmsUsageLevelOne.getMonthbudget();
            BigDecimal newBudgetForFather = budgetForFather.subtract(budgetForProTwo);
            pfmsUsageLevelOne.setMonthbudget(newBudgetForFather);
            updateProOne(pfmsUsageLevelOne);
            deleteProTwo(pfmsUsageLevelTwo);
            return true;
        }catch (Exception e){
            logger.info("ERROR:[删除二级科目失败，原因是数据库操作失败]");
            return false;
        }
    }
}
