/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.controller;

import com.pfms.biz.service.IProSettingService;
import com.pfms.dal.mybatis.model.PfmsUsageLevelOne;
import com.pfms.dal.mybatis.model.PfmsUsageLevelTwo;
import com.pfms.dal.mybatis.model.RealStatistics;
import com.pfms.util.Constants;
import com.pfms.util.PersonalUtil;
import com.pfms.webapp.model.Authentication;
import com.pfms.webapp.model.LevelOneProject;
import com.pfms.webapp.model.LevelTwoProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    private PersonalUtil personalUtil = new PersonalUtil();

    @RequestMapping(value = "/proSet")
    public ModelAndView proSetting(HttpServletRequest request) {

        //获取当前用户
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();
        logger.info("查询用户"+ userId +"科目设置信息");

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
    public Map addProOne(@RequestBody Map map, HttpServletRequest request) {
        logger.info("来时新增一级科目！");

        //获取用户信息
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");

        //解析并判断JSON
        String proOneNameAdd = (String) map.get("proOneNameAdd");
        proOneNameAdd = proOneNameAdd.trim();
        String type = (String) map.get("proOneType");
        type = type.trim();
        Map jsonMap = new HashMap();
        if (personalUtil.isBlankOrNull(proOneNameAdd) || personalUtil.isBlankOrNull(type)) {
            jsonMap.put("opstatus", "fail");
            jsonMap.put("optype", "");
            jsonMap.put("opid", "");
            jsonMap.put("opname", "");
            jsonMap.put("oprealMmount", "");
            jsonMap.put("opbudgetMmount", "");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            String today = sdf.format(new Date());
            PfmsUsageLevelOne pfmsUsageLevelOne = proSettingService.insertProOneWithPara(proOneNameAdd, type, authentication.getId());
            RealStatistics realStatisticses = proSettingService.getOrInsertMonthBudget(pfmsUsageLevelOne.getId(),today,pfmsUsageLevelOne.getMonthbudget());
            jsonMap.put("opstatus", "success");
            jsonMap.put("optype", pfmsUsageLevelOne.getType());
            jsonMap.put("opid", pfmsUsageLevelOne.getId());
            jsonMap.put("opname", pfmsUsageLevelOne.getName());
            jsonMap.put("oprealMmount", personalUtil.bigDecimalToString(realStatisticses.getRealamount()));
            jsonMap.put("opbudgetMmount", personalUtil.bigDecimalToString(realStatisticses.getBudget()));
        }
        return jsonMap;
    }

    //新增二级科目
    @RequestMapping(value = "/addProTwo.json", method = RequestMethod.POST)
    @ResponseBody
    public Map addProTwo(@RequestBody Map map, HttpServletRequest request) {
        logger.info("开始新增二级科目！");

        //获取用户信息
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");

        //解析并判断JSON
        String proTwoNameAdd = (String) map.get("proTwoNameAdd");
        String type = (String) map.get("proTwoType");
        String proOneId = (String) map.get("proOneId");
        String budget = (String) map.get("proTwoBudgetAdd");
        Map jsonMap = new HashMap();
        if (personalUtil.isBlankOrNull(proTwoNameAdd) || personalUtil.isBlankOrNull(type) || personalUtil.isBlankOrNull(proOneId)) {
            jsonMap.put("opstatus", "fail");
            jsonMap.put("optype", "");
            jsonMap.put("opparentid", "");
            jsonMap.put("opid", "");
            jsonMap.put("opname", "");
            jsonMap.put("opparentbudgetamount", "");
            jsonMap.put("opparentrealamount", "");
            jsonMap.put("opbudgetamount", "");
            jsonMap.put("oprealamount", "");
        } else {
            //判断此一级科目在当前收入支出项下面是否存在
            List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(proOneId, type, authentication.getId());
            if (pfmsUsageLevelOnes.size() != 1) {
                jsonMap.put("opstatus", "fail");
                jsonMap.put("optype", "");
                jsonMap.put("opparentid", "");
                jsonMap.put("opid", "");
                jsonMap.put("opname", "");
                jsonMap.put("opparentbudgetamount", "");
                jsonMap.put("opparentrealamount", "");
                jsonMap.put("opbudgetamount", "");
                jsonMap.put("oprealamount", "");
            }
            //向数据库中插入此条二级科目
            else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                String today = sdf.format(new Date());
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
                jsonMap.put("opstatus", "success");
                jsonMap.put("optype", pfmsUsageLevelTwo.getType());
                jsonMap.put("opparentid", pfmsUsageLevelTwo.getFatherId());
                jsonMap.put("opid", pfmsUsageLevelTwo.getId());
                jsonMap.put("opname", pfmsUsageLevelTwo.getName());
                jsonMap.put("opparentbudgetamount", personalUtil.bigDecimalToString(newBigDecimal));
                jsonMap.put("opparentrealamount", personalUtil.bigDecimalToString(realStatistics.getRealamount()));
                jsonMap.put("opbudgetamount", personalUtil.bigDecimalToString(pfmsUsageLevelTwo.getMonthbudget()));
                jsonMap.put("oprealamount", "0.00");
            }
        }
        return jsonMap;
    }

    //修改一级科目名称
    @RequestMapping(value = "/proOneModify.json", method = RequestMethod.POST)
    @ResponseBody
    public Map modifyProOne(@RequestBody Map map) {
        logger.info("开始修改科目名称！");

        Map jsonMap = new HashMap();
        String type = "";
        boolean resultType;

        //解析并判断JSON
        String proId = (String) map.get("proId");
        String proNameModify = (String) map.get("proNameModify");
        if (personalUtil.isBlankOrNull(proId) || personalUtil.isBlankOrNull(proNameModify)) {
            resultType = false;
        } else {
            List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(proId, null, null);
            if (pfmsUsageLevelOnes.size() == 1) {
                PfmsUsageLevelOne pfmsUsageLevelOne = pfmsUsageLevelOnes.get(0);
                type = pfmsUsageLevelOne.getType();
                pfmsUsageLevelOne.setName(proNameModify);
                proSettingService.updateProOne(pfmsUsageLevelOne);
                resultType = true;
            } else {
                resultType = false;
            }
                /*List<PfmsUsageLevelTwo> pfmsUsageLevelTwos = proSettingService.getProTwo(proId, null, null, null);
                if (pfmsUsageLevelTwos.size() == 1) {
                    PfmsUsageLevelTwo pfmsUsageLevelTwo = pfmsUsageLevelTwos.get(0);
                    type = pfmsUsageLevelTwo.getType();
                    pfmsUsageLevelTwo.setName(proNameModify);
                    proSettingService.updateProTwo(pfmsUsageLevelTwo);
                    resultType = true;
                } else {
                    resultType = false;
                }*/
        }
        if (resultType) {
            jsonMap.put("opstatus", "success");
            jsonMap.put("optype", type);
            jsonMap.put("opid", proId);
            jsonMap.put("opname", proNameModify);
        } else {
            jsonMap.put("opstatus", "fail");
            jsonMap.put("optype", "");
            jsonMap.put("opid", "");
            jsonMap.put("opname", "");
        }
        return jsonMap;
    }

    //修改二级科目
    @RequestMapping(value = "/proTwoModify.json", method = RequestMethod.POST)
    @ResponseBody
    public Map modifyProTwo(@RequestBody Map map) {
        logger.info("开始修改科目名称！");

        Map jsonMap = new HashMap();
        String type = "";
        boolean resultType = true;
        String parentId = "";
        BigDecimal parentbudgetamount = null;
        BigDecimal parentrealamount = null;
        BigDecimal budgetamount = null;
        BigDecimal realamount = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String today = sdf.format(new Date());

        //解析并判断JSON
        String proId = (String) map.get("proTwoId");
        String proTwoNameModify = (String) map.get("proTwoNameModify");
        String proTwoBudgetModify = (String) map.get("proTwoBudgetModify");
        if (personalUtil.isBlankOrNull(proId) || personalUtil.isBlankOrNull(proTwoNameModify)|| personalUtil.isBlankOrNull(proTwoBudgetModify)) {
            resultType = false;
        }
        if(resultType){
            List<PfmsUsageLevelTwo> pfmsUsageLevelTwos = proSettingService.getProTwo(proId, null, null, null);
            if (pfmsUsageLevelTwos.size() == 1) {
                PfmsUsageLevelTwo pfmsUsageLevelTwo = pfmsUsageLevelTwos.get(0);
                parentId = pfmsUsageLevelTwo.getFatherId();
                type = pfmsUsageLevelTwo.getType();
                pfmsUsageLevelTwo.setName(proTwoNameModify);
                pfmsUsageLevelTwo.setMonthbudget(new BigDecimal(proTwoBudgetModify));
                proSettingService.updateProTwo(pfmsUsageLevelTwo);
                BigDecimal sumNum = proSettingService.getParentIdBudget(parentId);
                List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(parentId, null, null);
                PfmsUsageLevelOne pfmsUsageLevelOne = pfmsUsageLevelOnes.get(0);
                pfmsUsageLevelOne.setMonthbudget(sumNum);
                proSettingService.updateProOne(pfmsUsageLevelOne);
                RealStatistics realStatisticsForProTwo = proSettingService.updateMonthBudget(proId,today,new BigDecimal(proTwoBudgetModify));
                RealStatistics realStatisticsForProOne = proSettingService.updateMonthBudget(parentId,today,sumNum);
                parentbudgetamount = realStatisticsForProOne.getBudget();
                parentrealamount = realStatisticsForProOne.getRealamount();
                budgetamount = realStatisticsForProTwo.getBudget();
                realamount = realStatisticsForProTwo.getRealamount();
                resultType = true;
            } else {
                resultType = false;
            }
        }
        if (resultType) {
            jsonMap.put("opstatus", "success");
            jsonMap.put("optype", type);
            jsonMap.put("opparentid", parentId);
            jsonMap.put("opid", proId);
            jsonMap.put("opname", proTwoNameModify);
            jsonMap.put("opparentbudgetamount", personalUtil.bigDecimalToString(parentbudgetamount));
            jsonMap.put("opparentrealamount", personalUtil.bigDecimalToString(parentrealamount));
            jsonMap.put("opbudgetamount", personalUtil.bigDecimalToString(budgetamount));
            jsonMap.put("oprealamount", personalUtil.bigDecimalToString(realamount));

        } else {
            jsonMap.put("opstatus", "fail");
            jsonMap.put("optype", "");
            jsonMap.put("opparentid", "");
            jsonMap.put("opid", "");
            jsonMap.put("opname", "");
            jsonMap.put("opparentbudgetamount", "");
            jsonMap.put("opparentrealamount", "");
            jsonMap.put("opbudgetamount", "");
            jsonMap.put("oprealamount", "");
        }
        return jsonMap;
    }

    //删除科目
    @RequestMapping(value = "/proDelete.json", method = RequestMethod.POST)
    @ResponseBody
    public Map deletePro(@RequestBody Map map, HttpServletRequest request) {
        logger.info("开始删除科目操作！");

        //获取用户信息
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();
        Map jsonMap = new HashMap();
        String type = "";
        boolean resultType;

        //解析并判断JSON
        String proId = (String) map.get("proId");
        String level = (String) map.get("level");
        if (personalUtil.isBlankOrNull(proId) || personalUtil.isBlankOrNull(level)) {
            resultType = false;
        } else {
            if ("1".equals(level)) {
                List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(proId, null, userId);
                if (pfmsUsageLevelOnes.size() == 1) {
                    type = pfmsUsageLevelOnes.get(0).getType();
                    proSettingService.deleteProOne(pfmsUsageLevelOnes.get(0));
                    resultType = true;
                } else {
                    resultType = false;
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
                    type = pfmsUsageLevelTwo.getType();
                    proSettingService.deleteProTwo(pfmsUsageLevelTwos.get(0));
                    resultType = true;
                } else {
                    resultType = false;
                }
            }
        }
        if (resultType) {
            jsonMap.put("opstatus", "success");
            jsonMap.put("optype", type);
            jsonMap.put("oplvl", level);
            jsonMap.put("opid", proId);

        } else {
            jsonMap.put("opstatus", "fail");
            jsonMap.put("optype", "");
            jsonMap.put("oplvl", "");
            jsonMap.put("opid", "");
        }
        return jsonMap;
    }

    @RequestMapping(value = "/getProOneName.json",method = RequestMethod.POST)
    @ResponseBody
    public Map getProOneName(@RequestBody Map map,HttpServletRequest request){
        Map<String, String> resultMap = new HashMap<String, String>();
        String id = map.get("id").toString();
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();
        List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(id,null,userId);
        if(pfmsUsageLevelOnes.size() != 1){
            resultMap.put("opstatus","failure");
            resultMap.put("optname","");
        }else{
            resultMap.put("opstatus","success");
            resultMap.put("optname",pfmsUsageLevelOnes.get(0).getName());
        }
        return resultMap;
    }

    @RequestMapping(value = "/getProTwoInfo.json",method = RequestMethod.POST)
    @ResponseBody
    public Map getProTwoInfo(@RequestBody Map map,HttpServletRequest request){
        Map<String, String> resultMap = new HashMap<String, String>();
        String id = map.get("id").toString();
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();
        List<PfmsUsageLevelTwo> pfmsUsageLevelTwos = proSettingService.getProTwo(id,null,null,null);
        if(pfmsUsageLevelTwos.size() != 1){
            resultMap.put("opstatus","failure");
            resultMap.put("optname","");
            resultMap.put("opbudget","");
        }else{
            resultMap.put("opstatus","success");
            resultMap.put("optname",pfmsUsageLevelTwos.get(0).getName());
            resultMap.put("opbudget",personalUtil.bigDecimalToString(pfmsUsageLevelTwos.get(0).getMonthbudget()));
        }
        return resultMap;
    }

    @RequestMapping(value = "/getProInfoForDelete.json",method = RequestMethod.POST)
    @ResponseBody
    public Map getProInfoForDelete(@RequestBody Map map,HttpServletRequest request){
        Map<String, String> resultMap = new HashMap<String, String>();
        String id = map.get("id").toString();
        String level = map.get("level").toString();
        Authentication authentication = (Authentication) request.getSession().getAttribute("session_authentication");
        int userId = authentication.getId();
        if("1".equals(level)){
            List<PfmsUsageLevelOne> pfmsUsageLevelOnes = proSettingService.getProOne(id,null,userId);
            if(pfmsUsageLevelOnes.size() != 1){
                resultMap.put("opstatus","failure");
                resultMap.put("optname","");
                resultMap.put("opbudget","");
            }else{
                resultMap.put("opstatus","success");
                resultMap.put("optname",pfmsUsageLevelOnes.get(0).getName());
                resultMap.put("opbudget",personalUtil.bigDecimalToString(pfmsUsageLevelOnes.get(0).getMonthbudget()));
            }
        }else{
            List<PfmsUsageLevelTwo> pfmsUsageLevelTwos = proSettingService.getProTwo(id,null,null,userId);
            if(pfmsUsageLevelTwos.size() != 1){
                resultMap.put("opstatus","failure");
                resultMap.put("optname","");
                resultMap.put("opbudget","");
            }else{
                resultMap.put("opstatus","success");
                resultMap.put("optname",pfmsUsageLevelTwos.get(0).getName());
                resultMap.put("opbudget",personalUtil.bigDecimalToString(pfmsUsageLevelTwos.get(0).getMonthbudget()));
            }
        }
        return resultMap;
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
                levelTwoProject.setRealAmountByMonth(personalUtil.bigDecimalToString(realStatistics.getRealamount()));
                levelTwoProject.setProjectMonthBudget(personalUtil.bigDecimalToString(realStatistics.getBudget()));
                levelOneProject.getLevelTwoProjectList().add(levelTwoProject);
            }
            RealStatistics realStatisticsForProOne = proSettingService.getOrInsertMonthBudget(id,today,pfmsUsageLevelOne.getMonthbudget());
            levelOneProject.setRealAmountByMonth(personalUtil.bigDecimalToString(realStatisticsForProOne.getRealamount()));
            levelOneProject.setProjectMonthBudget(personalUtil.bigDecimalToString(realStatisticsForProOne.getBudget()));
            map.put(pfmsUsageLevelOne.getId(), levelOneProject);
        }
        return map;
    }
}
