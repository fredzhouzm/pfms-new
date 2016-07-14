/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.controller;

import com.alibaba.fastjson.JSON;
import com.pfms.biz.model.ProOneBO;
import com.pfms.biz.service.IProSettingService;
import com.pfms.dal.mybatis.model.PfmsUsageLevelOne;
import com.pfms.dal.mybatis.model.PfmsUsageLevelTwo;
import com.pfms.dal.mybatis.model.RealStatistics;
import com.pfms.util.Constants;
import com.pfms.util.PersonalUtil;
import com.pfms.webapp.jsonResponse.*;
import com.pfms.webapp.model.Authentication;
import com.pfms.webapp.model.LevelOneProject;
import com.pfms.webapp.model.LevelTwoProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:ProSettingController.java, v0.1 2016/7/13 10:33 fred.zhou Exp $
 */
@Controller
@RequestMapping(value = "/setting")
public class ProSettingController {

    protected Logger logger = LoggerFactory.getLogger(ProSettingController.class);

    @Autowired
    public IProSettingService proSettingService;

    @RequestMapping(value = "/proSet")
    public ModelAndView proSetting(HttpServletRequest request) {

        //获取当前用户
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();
        logger.info("查询用户["+ authentication.getLoginName() +"]的科目设置信息");

        ModelAndView modelAndView = new ModelAndView();

        //获取收入选项的科目设置
        HashMap<String, LevelOneProject> incomeNode = getProList(Constants.PRO_TYPE_INCOME, userId);
        modelAndView.addObject("incomeNode", incomeNode);

        //获取支出选项的科目设置
        HashMap<String, LevelOneProject> expendNode = getProList(Constants.PRO_TYPE_EXPEND, userId);
        modelAndView.addObject("expendNode", expendNode);

        //返回ModelAndView
        modelAndView.setViewName("projectSettings");
        return modelAndView;
    }

    //新增一级科目
    @RequestMapping(value = "/addProOne.json", method = RequestMethod.POST)
    @ResponseBody
    public String addProOne(@RequestBody Map map, HttpServletRequest request) {
        //获取用户信息
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        //解析并判断JSON
        String proOneNameAdd = (String) map.get("proOneNameAdd");
        String type = (String) map.get("proOneType");
        logger.info("开始新增一级科目,科目名称为：["+proOneNameAdd+"]，新增科目的类型为：["+type+"]");
        AddProOneResponse response = new AddProOneResponse();
        if (StringUtils.isEmpty(proOneNameAdd.trim()) || StringUtils.isEmpty(type.trim())) {
            logger.info("新增一级科目失败，因为数据检查失败");
            response.setOpstatus(Constants.RESPONSE_FAILURE);
        } else {
            String today = PersonalUtil.getDateStr(Constants.DATETIME_FORMAT_MONTH);
            ProOneBO proOneBO = proSettingService.insertProOneAndBudget(proOneNameAdd, type, authentication.getId(), today);
            logger.info("新增一级科目成功");
            response.setOpstatus(Constants.RESPONSE_SUCCESS);
            response.setOptype(proOneBO.getType());
            response.setOpid(proOneBO.getId());
            response.setOpname(proOneBO.getName());
            response.setOprealMmount(PersonalUtil.bigDecimalToString(proOneBO.getRealAmount()));
            response.setOpbudgetMmount(PersonalUtil.bigDecimalToString(proOneBO.getBudget()));
        }
        return JSON.toJSONString(response);
    }

    //新增二级科目
    @RequestMapping(value = "/addProTwo.json", method = RequestMethod.POST)
    @ResponseBody
    public String addProTwo(@RequestBody Map map, HttpServletRequest request) {
        //获取用户信息
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");

        //解析并判断JSON
        String proTwoNameAdd = (String) map.get("proTwoNameAdd");
        String type = (String) map.get("proTwoType");
        String proOneId = (String) map.get("proOneId");
        String budget = (String) map.get("proTwoBudgetAdd");
        logger.info("开始新增二级科目，科目名称为["+ proTwoNameAdd +"]，新增科目类型为["+ type +"]，新增科目父级科目ID为["+ proOneId +"]，设置的每月预算为["+ budget +"]");
        AddProTwoResponse response = new AddProTwoResponse();
        if (StringUtils.isEmpty(proTwoNameAdd) || StringUtils.isEmpty(type) || StringUtils.isEmpty(proOneId)) {
            logger.info("新增二级科目失败，因为数据检查失败");
            response.setOpstatus(Constants.RESPONSE_FAILURE);
        } else {
            //判断此一级科目在当前收入支出项下面是否存在
            List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(proOneId, type, authentication.getId());
            if (pfmsUsageLevelOnes.size() != 1) {
                logger.info("新增二级科目失败，因为父级科目不存在");
                response.setOpstatus(Constants.RESPONSE_FAILURE);
            }
            //向数据库中插入此条二级科目
            else {
                String today = PersonalUtil.getDateStr("yyyyMM");
                //插入二级科目
                PfmsUsageLevelTwo pfmsUsageLevelTwo = proSettingService.insertProTwoWithPara(proTwoNameAdd, type, authentication.getId(), proOneId, budget);
                //向预算表中插入当月相关的二级科目
                proSettingService.getOrInsertMonthBudget(pfmsUsageLevelTwo.getId(), today, pfmsUsageLevelTwo.getMonthbudget());
                //更新一级科目
                PfmsUsageLevelOne pfmsUsageLevelOne = pfmsUsageLevelOnes.get(0);
                BigDecimal bigDecimal = pfmsUsageLevelOne.getMonthbudget();
                BigDecimal newBigDecimal = bigDecimal.add(pfmsUsageLevelTwo.getMonthbudget());
                pfmsUsageLevelOne.setMonthbudget(newBigDecimal);
                proSettingService.updateProOne(pfmsUsageLevelOne);
                //在预算表中更新当月相关的一级科目
                RealStatistics realStatistics = proSettingService.updateMonthBudget(pfmsUsageLevelOne.getId(),today,newBigDecimal);
                logger.info("新增二级科目成功");
                response.setOpstatus(Constants.RESPONSE_SUCCESS);
                response.setOptype(pfmsUsageLevelTwo.getType());
                response.setOpparentid(pfmsUsageLevelTwo.getFatherId());
                response.setOpid(pfmsUsageLevelTwo.getId());
                response.setOpname(pfmsUsageLevelTwo.getName());
                response.setOpparentbudgetamount(PersonalUtil.bigDecimalToString(newBigDecimal));
                response.setOpparentrealamount(PersonalUtil.bigDecimalToString(realStatistics.getRealamount()));
                response.setOpbudgetamount(PersonalUtil.bigDecimalToString(pfmsUsageLevelTwo.getMonthbudget()));
                response.setOprealamount("0.00");
            }
        }
        return JSON.toJSONString(response);
    }

    //修改一级科目
    @RequestMapping(value = "/proOneModify.json", method = RequestMethod.POST)
    @ResponseBody
    public String modifyProOne(@RequestBody Map map) {
        //解析并判断JSON
        String proId = (String) map.get("proId");
        String proNameModify = (String) map.get("proNameModify");
        logger.info("开始修改一级科目，修改的科目ID为["+ proId +"]，科目名称为["+ proNameModify +"]");
        ModifyProOneResponse response = new ModifyProOneResponse();
        if (StringUtils.isEmpty(proId) || StringUtils.isEmpty(proNameModify)) {
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("修改一级科目失败，因为数据检查失败");
        } else {
            List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(proId, null, null);
            if (pfmsUsageLevelOnes.size() == 1) {
                PfmsUsageLevelOne pfmsUsageLevelOne = pfmsUsageLevelOnes.get(0);
                pfmsUsageLevelOne.setName(proNameModify);
                proSettingService.updateProOne(pfmsUsageLevelOne);
                response.setOpstatus(Constants.RESPONSE_SUCCESS);
                response.setOptype(pfmsUsageLevelOne.getType());
                response.setOpid(proId);
                response.setOpname(proNameModify);
                logger.info("修改一级科目成功");
            } else {
                response.setOpstatus(Constants.RESPONSE_FAILURE);
                logger.info("修改一级科目失败，因为数据库中查找不到指定的科目内容");
            }
        }
        return JSON.toJSONString(response);
    }

    //修改二级科目
    @RequestMapping(value = "/proTwoModify.json", method = RequestMethod.POST)
    @ResponseBody
    public String modifyProTwo(@RequestBody Map map) {
        //解析并判断JSON
        String proId = (String) map.get("proTwoId");
        String proTwoNameModify = (String) map.get("proTwoNameModify");
        String proTwoBudgetModify = (String) map.get("proTwoBudgetModify");
        logger.info("开始修改二级科目，修改的科目ID为["+ proId +"]，科目名称为["+ proTwoNameModify +"]，修改科目的月预算为["+ proTwoBudgetModify +"]");
        ModifyProTwoResponse response = new ModifyProTwoResponse();
        if (StringUtils.isEmpty(proId) || StringUtils.isEmpty(proTwoNameModify)|| StringUtils.isEmpty(proTwoBudgetModify)) {
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("修改二级科目失败，因为数据检查失败");
        }
        else{
            List<PfmsUsageLevelTwo> pfmsUsageLevelTwos = proSettingService.getProTwo(proId, null, null, null);
            if (pfmsUsageLevelTwos.size() == 1) {
                PfmsUsageLevelTwo pfmsUsageLevelTwo = pfmsUsageLevelTwos.get(0);
                String parentId = pfmsUsageLevelTwo.getFatherId();
                pfmsUsageLevelTwo.setName(proTwoNameModify);
                pfmsUsageLevelTwo.setMonthbudget(new BigDecimal(proTwoBudgetModify));
                proSettingService.updateProTwo(pfmsUsageLevelTwo);

                BigDecimal sumNum = proSettingService.getParentIdBudget(parentId);
                List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(parentId, null, null);
                PfmsUsageLevelOne pfmsUsageLevelOne = pfmsUsageLevelOnes.get(0);
                pfmsUsageLevelOne.setMonthbudget(sumNum);
                proSettingService.updateProOne(pfmsUsageLevelOne);

                String today = PersonalUtil.getDateStr("yyyyMM");
                RealStatistics realStatisticsForProTwo = proSettingService.updateMonthBudget(proId,today,new BigDecimal(proTwoBudgetModify));
                RealStatistics realStatisticsForProOne = proSettingService.updateMonthBudget(parentId,today,sumNum);

                response.setOpstatus(Constants.RESPONSE_SUCCESS);
                response.setOptype(pfmsUsageLevelTwo.getType());
                response.setOpparentid(parentId);
                response.setOpid(proId);
                response.setOpname(proTwoNameModify);
                response.setOpparentbudgetamount(PersonalUtil.bigDecimalToString(realStatisticsForProOne.getBudget()));
                response.setOpparentrealamount(PersonalUtil.bigDecimalToString(realStatisticsForProOne.getRealamount()));
                response.setOpbudgetamount(PersonalUtil.bigDecimalToString(realStatisticsForProTwo.getBudget()));
                response.setOprealamount(PersonalUtil.bigDecimalToString(realStatisticsForProTwo.getRealamount()));
                logger.info("修改二级科目成功");
            } else {
                response.setOpstatus(Constants.RESPONSE_FAILURE);
                logger.info("修改二级科目失败，因为数据库中查找不到指定的科目内容");
            }
        }
        return JSON.toJSONString(response);
    }

    //删除科目
    @RequestMapping(value = "/proDelete.json", method = RequestMethod.POST)
    @ResponseBody
    public String deletePro(@RequestBody Map map, HttpServletRequest request) {
        //获取用户信息
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();

        //解析并判断JSON
        String proId = (String) map.get("proId");
        String level = (String) map.get("level");
        DeleteProResponse response = new DeleteProResponse();
        logger.info("开始删除科目操作，删除科目ID为["+ proId +"]，级别为["+ level +"]");
        if (StringUtils.isEmpty(proId) || StringUtils.isEmpty(level)) {
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("删除科目失败，因为数据检查失败");
        } else {
            if ("1".equals(level)) {
                List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(proId, null, userId);
                if (pfmsUsageLevelOnes.size() == 1) {
                    proSettingService.deleteProOne(pfmsUsageLevelOnes.get(0));

                    response.setOpstatus(Constants.RESPONSE_SUCCESS);
                    response.setOptype(pfmsUsageLevelOnes.get(0).getType());
                    response.setOplvl(level);
                    response.setOpid(proId);
                    logger.info("删除一级科目成功");
                } else {
                    response.setOpstatus(Constants.RESPONSE_FAILURE);
                    logger.info("删除一级科目失败，因为数据库中查找不到指定的科目内容");
                }
            } else {
                List<PfmsUsageLevelTwo> pfmsUsageLevelTwos = proSettingService.getProTwo(proId, null, null, userId);
                if (pfmsUsageLevelTwos.size() == 1) {
                    PfmsUsageLevelTwo pfmsUsageLevelTwo = pfmsUsageLevelTwos.get(0);
                    String parentId = pfmsUsageLevelTwo.getFatherId();
                    BigDecimal budgetForProTwo = pfmsUsageLevelTwo.getMonthbudget();
                    PfmsUsageLevelOne pfmsUsageLevelOne = proSettingService.getProOne(parentId,null,userId).get(0);
                    BigDecimal budgetForFather = pfmsUsageLevelOne.getMonthbudget();
                    BigDecimal newBudgetForFather = budgetForFather.subtract(budgetForProTwo);
                    pfmsUsageLevelOne.setMonthbudget(newBudgetForFather);
                    proSettingService.updateProOne(pfmsUsageLevelOne);
                    proSettingService.deleteProTwo(pfmsUsageLevelTwos.get(0));

                    response.setOpstatus(Constants.RESPONSE_SUCCESS);
                    response.setOptype(pfmsUsageLevelTwo.getType());
                    response.setOplvl(level);
                    response.setOpid(proId);
                    logger.info("删除二级科目成功");
                } else {
                    response.setOpstatus(Constants.RESPONSE_FAILURE);
                    logger.info("删除二级科目失败，因为数据库中查找不到指定的科目内容");
                }
            }
        }
        return JSON.toJSONString(response);
    }

    //获取一级科目名称
    @RequestMapping(value = "/getProOneName.json",method = RequestMethod.POST)
    @ResponseBody
    public String getProOneName(@RequestBody Map map,HttpServletRequest request){

        //解析并判断JSON
        String id = map.get("id").toString();
        logger.info("开始获取一级科目名称操作，一级科目ID为["+ id +"]");
        GetProOneNameResponse response = new GetProOneNameResponse();
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();
        List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(id,null,userId);
        if(pfmsUsageLevelOnes.size() != 1){
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("获取一级科目名称操作失败，因为数据库中查找不到指定的科目内容");
        }else{
            response.setOpstatus(Constants.RESPONSE_SUCCESS);
            response.setOptname(pfmsUsageLevelOnes.get(0).getName());
            logger.info("一级科目名称为["+response.getOptname()+"]");
            logger.info("获取一级科目名称操作成功");
        }
        return JSON.toJSONString(response);
    }

    //获取二级科目信息
    @RequestMapping(value = "/getProTwoInfo.json",method = RequestMethod.POST)
    @ResponseBody
    public String getProTwoInfo(@RequestBody Map map){

        //解析并判断JSON
        String id = map.get("id").toString();
        logger.info("开始获取二级科目名称操作，二级科目ID为["+ id +"]");
        GetProTwoInfoResponse response = new GetProTwoInfoResponse();
        List<PfmsUsageLevelTwo> pfmsUsageLevelTwos = proSettingService.getProTwo(id,null,null,null);
        if(pfmsUsageLevelTwos.size() != 1){
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("获取二级科目名称操作失败，因为数据库中查找不到指定的科目内容");
        }else{
            response.setOpstatus(Constants.RESPONSE_SUCCESS);
            response.setOptname(pfmsUsageLevelTwos.get(0).getName());
            response.setOpbudget(PersonalUtil.bigDecimalToString(pfmsUsageLevelTwos.get(0).getMonthbudget()));
            logger.info("二级科目名称为["+ response.getOptname() +"]，其月预算为["+ response.getOpbudget() +"]");
            logger.info("获取二级科目名称操作成功");
        }
        return JSON.toJSONString(response);
    }

    //获取将被删除的科目信息
    @RequestMapping(value = "/getProInfoForDelete.json",method = RequestMethod.POST)
    @ResponseBody
    public String getProInfoForDelete(@RequestBody Map map,HttpServletRequest request){

        //解析并判断JSON
        String id = map.get("id").toString();
        String level = map.get("level").toString();
        logger.info("开始获取将被删除的科目信息，科目ID为["+ id +"]，科目级别为["+ level +"]");
        GetProInfoForDeleteResponse response = new GetProInfoForDeleteResponse();
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();
        if("1".equals(level)){
            List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(id,null,userId);
            if(pfmsUsageLevelOnes.size() != 1){
                response.setOpstatus(Constants.RESPONSE_FAILURE);
                logger.info("获取将被删除的一级科目信息失败，因为数据库中查找不到指定的科目内容");
            }else{
                response.setOpstatus(Constants.RESPONSE_SUCCESS);
                response.setOptname(pfmsUsageLevelOnes.get(0).getName());
                response.setOpbudget(PersonalUtil.bigDecimalToString(pfmsUsageLevelOnes.get(0).getMonthbudget()));
                logger.info("一级科目名称为["+ response.getOptname() +"]，其月预算为["+ response.getOpbudget() +"]");
                logger.info("获取将被删除的一级科目信息成功");
            }
        }else{
            List<PfmsUsageLevelTwo> pfmsUsageLevelTwos = proSettingService.getProTwo(id,null,null,userId);
            if(pfmsUsageLevelTwos.size() != 1){
                response.setOpstatus(Constants.RESPONSE_FAILURE);
                logger.info("获取将被删除的二级科目信息失败，因为数据库中查找不到指定的科目内容");
            }else{
                response.setOpstatus(Constants.RESPONSE_SUCCESS);
                response.setOptname(pfmsUsageLevelTwos.get(0).getName());
                response.setOpbudget(PersonalUtil.bigDecimalToString(pfmsUsageLevelTwos.get(0).getMonthbudget()));
                logger.info("二级科目名称为["+ response.getOptname() +"]，其月预算为["+ response.getOpbudget() +"]");
                logger.info("获取将被删除的二级科目信息成功");
            }
        }
        return JSON.toJSONString(response);
    }

    public HashMap<String, LevelOneProject> getProList(String type, int userId) {
        List<PfmsUsageLevelOne> pfmsUsageLevelOnes;
        List<PfmsUsageLevelTwo> pfmsUsageLevelTwos;
        LinkedHashMap<String, LevelOneProject> map = new LinkedHashMap<String, LevelOneProject>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String today = sdf.format(new Date());
        pfmsUsageLevelOnes = proSettingService.getProOne(null, type, userId);
        for (PfmsUsageLevelOne pfmsUsageLevelOne : pfmsUsageLevelOnes) {
            String id = pfmsUsageLevelOne.getId();
            pfmsUsageLevelTwos = proSettingService.getProTwo(null, type, id, userId);
            LevelOneProject levelOneProject = new LevelOneProject();
            levelOneProject.setProjectID(pfmsUsageLevelOne.getId());
            levelOneProject.setProjectType(pfmsUsageLevelOne.getType());
            levelOneProject.setProjectName(pfmsUsageLevelOne.getName());
            levelOneProject.setProjectChildren(pfmsUsageLevelTwos.size());
            for (PfmsUsageLevelTwo pfmsUsageLevelTwo : pfmsUsageLevelTwos) {
                LevelTwoProject levelTwoProject = new LevelTwoProject();
                RealStatistics realStatistics = proSettingService.getOrInsertMonthBudget(id,today,pfmsUsageLevelTwo.getMonthbudget());
                levelTwoProject.setProjectID(pfmsUsageLevelTwo.getId());
                levelTwoProject.setProjectType(pfmsUsageLevelTwo.getType());
                levelTwoProject.setProjectName(pfmsUsageLevelTwo.getName());
                levelTwoProject.setProjectFatherID(pfmsUsageLevelTwo.getFatherId());
                levelTwoProject.setRealAmountByMonth(PersonalUtil.bigDecimalToString(realStatistics.getRealamount()));
                levelTwoProject.setProjectMonthBudget(PersonalUtil.bigDecimalToString(realStatistics.getBudget()));
                levelOneProject.getLevelTwoProjectList().add(levelTwoProject);
            }
            RealStatistics realStatisticsForProOne = proSettingService.getOrInsertMonthBudget(id,today,pfmsUsageLevelOne.getMonthbudget());
            levelOneProject.setRealAmountByMonth(PersonalUtil.bigDecimalToString(realStatisticsForProOne.getRealamount()));
            levelOneProject.setProjectMonthBudget(PersonalUtil.bigDecimalToString(realStatisticsForProOne.getBudget()));
            map.put(pfmsUsageLevelOne.getId(), levelOneProject);
        }
        return map;
    }
}
