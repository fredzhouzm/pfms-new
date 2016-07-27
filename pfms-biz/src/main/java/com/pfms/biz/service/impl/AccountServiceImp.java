package com.pfms.biz.service.impl;

import com.pfms.biz.service.IAccountService;
import com.pfms.biz.service.ISequenceService;
import com.pfms.dal.mybatis.dao.*;
import com.pfms.dal.mybatis.model.*;
import com.pfms.util.Constants;
import com.pfms.util.PersonalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by fred on 2016/01/31.
 */
@Service
public class AccountServiceImp implements IAccountService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PfmsFormMapper pfmsFormMapper;
    @Autowired
    PfmsUsageLevelOneMapper pfmsUsageLevelOneMapper;
    @Autowired
    PfmsUsageLevelTwoMapper pfmsUsageLevelTwoMapper;
    @Autowired
    CustomMapper customMapper;
    @Autowired
    FormVmMapper formVmMapper;
    @Autowired
    ISequenceService sequenceService;

    public HashMap getProOne(String type, int userId) {
        logger.info("获取用户ID为[" + userId + "],TYPE为[" + type + "]的一级科目分类!");

        LinkedHashMap<String, String> proOneTmp = new LinkedHashMap<String, String>();
        PfmsUsageLevelOneExample pfmsUsageLevelOneExample = new PfmsUsageLevelOneExample();

        pfmsUsageLevelOneExample.createCriteria().andTypeEqualTo(type).andCreatorIdEqualTo(userId);
        List<PfmsUsageLevelOne> pfmsUsageLevelOneList = pfmsUsageLevelOneMapper.selectByExample(pfmsUsageLevelOneExample);
        if (pfmsUsageLevelOneList.size() > 0) {
            for (PfmsUsageLevelOne pfmsUsageLevelOne : pfmsUsageLevelOneList) {
                proOneTmp.put(pfmsUsageLevelOne.getId(), pfmsUsageLevelOne.getName());
            }
        }
        return proOneTmp;
    }

    public HashMap getProTwoByProOne(String proOneId) {
        logger.info("根据一级科目["+ proOneId +"]获取其二级科目列表");

        LinkedHashMap<String, String> proTwoTmp = new LinkedHashMap<String, String>();
        PfmsUsageLevelTwoExample pfmsUsageLevelTwoExample = new PfmsUsageLevelTwoExample();

        pfmsUsageLevelTwoExample.createCriteria().andFatherIdEqualTo(proOneId);
        List<PfmsUsageLevelTwo> pfmsUsageLevelTwoList = pfmsUsageLevelTwoMapper.selectByExample(pfmsUsageLevelTwoExample);
        if (pfmsUsageLevelTwoList.size() > 0) {
            for (PfmsUsageLevelTwo pfmsUsageLevelTwo : pfmsUsageLevelTwoList) {
                proTwoTmp.put(pfmsUsageLevelTwo.getId(), pfmsUsageLevelTwo.getName());
            }
        }
        return proTwoTmp;
    }

    @Transactional
    public void insertForm(PfmsForm pfmsForm) {
        logger.info("开始向数据库中插入新的单据记录，ID为["+ pfmsForm.getId() +"]");
        pfmsFormMapper.insert(pfmsForm);
    }

    public List<PfmsForm> getFormListByMonth(String month, int userId) {
        return null;
    }

    public PfmsForm getFormById(String formId) {
        logger.info("根据单据ID["+ formId +"]获取单据详细信息");
        PfmsFormExample pfmsFormExample = new PfmsFormExample();
        pfmsFormExample.createCriteria().andIdEqualTo(formId);
        List<PfmsForm> pfmsForms = pfmsFormMapper.selectByExample(pfmsFormExample);
        if (pfmsForms.size() != 1) {
            logger.info("数据库查询得到的单据列表大小不为1，返回NULL");
            return null;
        } else {
            return pfmsForms.get(0);
        }
    }

    @Transactional
    public void updateForm(PfmsForm pfmsForm) {
        logger.info("更新记账单据，ID为["+ pfmsForm.getId() +"]");
        pfmsFormMapper.updateByPrimaryKeySelective(pfmsForm);
    }

    @Transactional
    public void deleteForm(String id) {
        logger.info("删除记账单据，ID为["+ id +"]");
        pfmsFormMapper.deleteByPrimaryKey(id);
    }

    //获取指定月份的相应金额总和 type-1为收入,type-2为支出
    public BigDecimal getTotalAmountByMonth(int year, int month, String type, int creatorId) {
        Date firstDate = PersonalUtil.getDateOfMonth(year, month, Constants.DAY_OF_MONTH_BEGIN);
        Date lastDate = PersonalUtil.getDateOfMonth(year, month, Constants.DAY_OF_MONTH_END);
        logger.info("获取创建者为["+ creatorId +"],valueDateTime从["+PersonalUtil.getDateStr("yyyy-MM-dd HH:mm:ss",firstDate)+"]到["+PersonalUtil.getDateStr("yyyy-MM-dd HH:mm:ss",lastDate)+"]的所有type为["+type+"]的单据金额之和");
        return customMapper.selTotAmtByMon(type, firstDate, lastDate, creatorId);
    }

    //获取指定月份记录的收支单据流水
    public List<FormVm> getFormVmByMonth(int year, int month, int creatorId) {
        logger.info("获取创建者为["+ creatorId +"],["+ year + "/"+ PersonalUtil.intToString(month,2) +"]月份的所有收支记录");
        Date firstDate = PersonalUtil.getDateOfMonth(year, month, Constants.DAY_OF_MONTH_BEGIN);
        Date lastDate = PersonalUtil.getDateOfMonth(year, month, Constants.DAY_OF_MONTH_END);
        FormVmExample formVmExample = new FormVmExample();
        formVmExample.createCriteria().andValueDateBetween(firstDate, lastDate).andCreatorIdEqualTo(creatorId);
        formVmExample.setOrderByClause("VALUE_DATE DESC, TIME_NO DESC");
        return formVmMapper.selectByExample(formVmExample);
    }

    public List<FormVm> getFormVmListByCondition(String type, Date beginDate, Date endDate, String proOneId, String proTwoId, String id) {
        logger.info("根据条件获取收支记录视图列表");
        FormVmExample formVmExample = new FormVmExample();
        FormVmExample.Criteria criteria = formVmExample.createCriteria();
        if (type != null && ("1".equals(type) || "2".equals(type))) {
            criteria.andTypeEqualTo(type);
        }
        if (beginDate != null && endDate != null && beginDate.before(endDate) && endDate.before(new Date())) {
            criteria.andValueDateBetween(beginDate, endDate);
        }
        if (proOneId != null && !"".equals(proOneId)) {
            criteria.andUsageLevelOneEqualTo(proOneId);
        }
        if (proTwoId != null && !"".equals(proTwoId)) {
            criteria.andUsageLevelTwoEqualTo(proTwoId);
        }
        if (id != null && !"".equals(id)) {
            criteria.andIdEqualTo(id);
        }
        List<FormVm> formVmList = formVmMapper.selectByExample(formVmExample);
        return formVmList;
    }

    public int getMonthOrderCount(int selectedYear, int seletedMonth, int userId) {
        logger.info("获取创建者为["+ userId +"],["+ selectedYear + "/"+ PersonalUtil.intToString(seletedMonth,2) +"]月份的所有收支记录的数量");
        Date beginTime = PersonalUtil.getDateOfMonth(selectedYear,seletedMonth, Constants.DAY_OF_MONTH_BEGIN);
        Date endTime = PersonalUtil.getDateOfMonth(selectedYear,seletedMonth,Constants.DAY_OF_MONTH_END);
        PfmsFormExample pfmsFormExample = new PfmsFormExample();
        pfmsFormExample.createCriteria().andValueDateBetween(beginTime, endTime).andCreatorIdEqualTo(userId);
        return pfmsFormMapper.countByExample(pfmsFormExample);
    }
}
