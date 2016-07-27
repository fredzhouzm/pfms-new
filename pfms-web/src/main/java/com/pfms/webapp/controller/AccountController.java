/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.controller;

import com.pfms.biz.service.IAccountService;
import com.pfms.biz.service.ISequenceService;
import com.pfms.dal.mybatis.model.FormVm;
import com.pfms.dal.mybatis.model.PfmsForm;
import com.pfms.util.Constants;
import com.pfms.util.PersonalUtil;
import com.pfms.webapp.jsonResponse.*;
import com.pfms.webapp.model.Authentication;
import com.pfms.webapp.model.FormToShow;
import org.joda.time.DateTime;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.pfms.util.PersonalUtil.getDateStr;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:AccountController.java, v0.1 2016/7/13 10:32 fred.zhou Exp $
 */
@Controller
@RequestMapping(value = "/account")
public class AccountController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public IAccountService accountService;

    @Autowired
    public ISequenceService sequenceService;

    @RequestMapping(value = "/show")
    public ModelAndView accountInit(HttpServletRequest request) {
        logger.info("记账页面初始化,获取相应的页面参数");
        ModelAndView modelAndView = new ModelAndView();
        Authentication authentication = (Authentication) request.getSession().getAttribute(Constants.SESSION);
        int userId = authentication.getId();

        DateTime time = new DateTime();
        int currentYear = time.getYear();
        int currentMonth = time.getMonthOfYear();
        HashMap<String, String> incomeProOneList = accountService.getProOne(Constants.PRO_TYPE_INCOME, userId);
        HashMap<String, String> expendProOneList = accountService.getProOne(Constants.PRO_TYPE_EXPEND, userId);
        BigDecimal totalAmountIn = accountService.getTotalAmountByMonth(currentYear, currentMonth, Constants.PRO_TYPE_INCOME, userId);
        BigDecimal totalAmountOut = accountService.getTotalAmountByMonth(currentYear, currentMonth, Constants.PRO_TYPE_EXPEND, userId);
        List<FormVm> formVmList = accountService.getFormVmByMonth(currentYear, currentMonth, userId);

        LinkedList<FormToShow> formToShowLinkedList = new LinkedList<>();
        for (FormVm formVm : formVmList) {
            FormToShow formToShow = formVmToFromShow(formVm);
            formToShowLinkedList.add(formToShow);
        }

        modelAndView.addObject("incomeProOneList", incomeProOneList);
        modelAndView.addObject("expendProOneList", expendProOneList);
        modelAndView.addObject("currentYear", String.valueOf(currentYear));
        modelAndView.addObject("currentMonth", PersonalUtil.intToString(currentMonth, 2));
        modelAndView.addObject("totalAmountIn", PersonalUtil.bigDecimalToString(totalAmountIn));
        modelAndView.addObject("totalAmountOut", PersonalUtil.bigDecimalToString(totalAmountOut));
        modelAndView.addObject("formList", formToShowLinkedList);
        modelAndView.setViewName("account");
        return modelAndView;
    }

    @RequestMapping(value = "/getProTwoList.json")
    @ResponseBody
    public GetProTwoListResponse getProTwoList(@RequestBody Map map) {
        //解析并判断JSON
        String proOneId = (String) map.get("proOneId");
        GetProTwoListResponse response = new GetProTwoListResponse();
        logger.info("Ajax异步获取一级科目[" + proOneId + "]对应的二级科目分类!");
        if (StringUtils.isEmpty(proOneId)) {
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("Ajax异步获取二级科目分类失败，因为数据检查失败");
        } else {
            try {
                HashMap<String, String> proTwolist = accountService.getProTwoByProOne(proOneId);
                response.setOpstatus(Constants.RESPONSE_SUCCESS);
                response.setOpmap(proTwolist);
                logger.info("Ajax异步获取二级科目分类成功");
            } catch (Exception e) {
                response.setOpstatus(Constants.RESPONSE_FAILURE);
                logger.info("Ajax异步获取二级科目分类失败，因为数据库操作失败");
            }
        }
        return response;
    }

    @RequestMapping(value = "/insertForm.json", method = RequestMethod.POST)
    @ResponseBody
    public InsertFormResponse insertForm(@RequestBody Map map, HttpServletRequest request) {

        InsertFormResponse response = new InsertFormResponse();
        Authentication authentication = (Authentication) request.getSession().getAttribute(Constants.SESSION);
        int userId = authentication.getId();

        //解析并判断JSON
        String type = (String) map.get("type");
        String amount = (String) map.get("amount");
        String proOneId = (String) map.get("proOneId");
        String proTwoId = (String) map.get("proTwoId");
        String date = (String) map.get("date");
        String peroid = (String) map.get("peroid");
        String remark = (String) map.get("remark");
        logger.info("Ajax异步提交单据，type:[" + type + "],amount:[" + amount + "],proOneId:[" + proOneId + "],proTwoId:[" + proTwoId + "],date:[" + date + "],peroid:[" + peroid + "],remark:[" + remark + "]");

        try {
            PfmsForm pfmsForm = transToForm(type, amount, proOneId, proTwoId, date, peroid, remark, userId);
            accountService.insertForm(pfmsForm);
            FormVm formVm = accountService.getFormVmListByCondition(null, null, null, null, null, pfmsForm.getId()).get(0);

            int selectedYear = Integer.parseInt(date.substring(0, 4));
            int seletedMonth = Integer.parseInt(date.substring(5, 7));
            FormToShow formToShow = formVmToFromShow(formVm);
            BigDecimal totalAmountIn = accountService.getTotalAmountByMonth(selectedYear, seletedMonth, Constants.PRO_TYPE_INCOME, userId);
            BigDecimal totalAmountOut = accountService.getTotalAmountByMonth(selectedYear, seletedMonth, Constants.PRO_TYPE_EXPEND, userId);

            logger.info("Ajax异步提交单据成功");
            response.setOpstatus(Constants.RESPONSE_SUCCESS);
            response.setOpid(formToShow.getId());
            response.setOptype(formToShow.getType());
            response.setOpamount(formToShow.getAmount());
            response.setOpdate(formToShow.getValueDateStr());
            response.setOpperiod(formToShow.getPeroidStr());
            response.setOppro(formToShow.getProOneStr() + "-" + formToShow.getProTwoStr());
            response.setOpremark(formToShow.getRemark());
            response.setOpselectedMonth(String.valueOf(selectedYear) + PersonalUtil.intToString(seletedMonth, 2));
            response.setOptotalAmountIn(PersonalUtil.bigDecimalToString(totalAmountIn));
            response.setOptotalAmountOut(PersonalUtil.bigDecimalToString(totalAmountOut));
        } catch (Exception e) {
            logger.info("Ajax异步提交单据失败");
            response.setOpstatus(Constants.RESPONSE_FAILURE);
        }
        return response;

    }

    @RequestMapping(value = "/switchMonth.json", method = RequestMethod.POST)
    @ResponseBody
    public SwithMonthResponse swithMonth(@RequestBody Map map, HttpServletRequest request) {

        SwithMonthResponse response = new SwithMonthResponse();
        Authentication authentication = (Authentication) request.getSession().getAttribute(Constants.SESSION);
        int userId = authentication.getId();

        //解析并判断JSON
        String monthStr = (String) map.get("newMonth");
        logger.info("更改月份，目标月份为：[" + monthStr + "]");

        try {
            int year = Integer.parseInt(monthStr.substring(0, 4));
            int month = Integer.parseInt(monthStr.substring(4));
            BigDecimal totalAmountIn = accountService.getTotalAmountByMonth(year, month, Constants.PRO_TYPE_INCOME, userId);
            BigDecimal totalAmountOut = accountService.getTotalAmountByMonth(year, month, Constants.PRO_TYPE_EXPEND, userId);


            response.setOpcurrentYear(String.valueOf(year));
            response.setOpcurrentMonth(PersonalUtil.intToString(month, 2));
            response.setOptotalAmountIn(PersonalUtil.bigDecimalToString(totalAmountIn));
            response.setOptotalAmountOut(PersonalUtil.bigDecimalToString(totalAmountOut));

            LinkedList<FormToShow> formToShowLinkedList = new LinkedList<>();
            List<FormVm> formVmList = accountService.getFormVmByMonth(year, month, userId);
            if (formVmList != null && formVmList.size() > 0) {
                for (FormVm formVm : formVmList) {
                    FormToShow formToShow = formVmToFromShow(formVm);
                    formToShowLinkedList.add(formToShow);
                }
                response.setOpsearchResult("yes");
                logger.info("目标月份有单据存在");
            } else {
                response.setOpsearchResult("no");
                logger.info("目标月份不存在单据");
            }
            response.setOpformList(formToShowLinkedList);
            response.setOpstatus(Constants.RESPONSE_SUCCESS);
        } catch (Exception e) {
            logger.info("更改月份显示失败，因为数据库操作失败");
            response.setOpstatus(Constants.RESPONSE_FAILURE);
        }
        return response;
    }

    @RequestMapping(value = "/modifyInit.json", method = RequestMethod.POST)
    @ResponseBody
    public ModifyInitResponse modifyInit(@RequestBody Map map) {
        ModifyInitResponse response = new ModifyInitResponse();
        String id = map.get("id").toString();
        logger.info("修改单据初始化，单据ID编号为：" + id);

        try {
            PfmsForm pfmsForm = accountService.getFormById(id);
            String proOneId = pfmsForm.getUsageLevelOne();
            if (pfmsForm != null) {
                HashMap<String, String> proTwolist = accountService.getProTwoByProOne(proOneId);
                response.setOpstatus(Constants.RESPONSE_SUCCESS);
                response.setOpamount(PersonalUtil.bigDecimalToString(pfmsForm.getAmount()));
                response.setOpproone(pfmsForm.getUsageLevelOne());
                response.setProtwo(pfmsForm.getUsageLevelTwo());
                response.setOpdate(PersonalUtil.getDateStr(Constants.DATETIME_FORMAT_DATE, pfmsForm.getValueDate()));
                response.setOpperoid(pfmsForm.getTimeNo());
                response.setOpremark(pfmsForm.getRemark());
                response.setOpprotwolist(proTwolist);
                logger.info("修改单据的查询成功");
            } else {
                response.setOpstatus(Constants.RESPONSE_FAILURE);
                logger.info("修改单据的查询失败");
            }
        } catch (Exception e) {
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("修改单据的查询失败");
        }
        return response;
    }

    @RequestMapping(value = "/modifyForm.json", method = RequestMethod.POST)
    @ResponseBody
    public ModifyFormResponse modifyForm(@RequestBody Map map, HttpServletRequest request) throws ParseException {
        ModifyFormResponse response = new ModifyFormResponse();
        //解析并判断JSON
        String id = map.get("id").toString();
        String type = map.get("type").toString();
        String amount = map.get("amount").toString();
        String proOneId = map.get("proOneId").toString();
        String proTwoId = map.get("proTwoId").toString();
        String date = map.get("date").toString();
        String peroid = map.get("peroid").toString();
        String remark = map.get("remark").toString();
        String month = map.get("month").toString();
        logger.info("修改单据提交开始，id:[" + id + "],type:[" + type + "],amount:[" + amount + "],proOneId:[" + proOneId + "],proTwoId:[" + proTwoId + "],date:[" + date + "],peroid:[" + peroid + "],remark:[" + remark + "],month:[" + month + "]");
        Authentication authentication = (Authentication) request.getSession().getAttribute(Constants.SESSION);
        int userId = authentication.getId();
        try {
            PfmsForm pfmsForm = accountService.getFormById(id);
            if (pfmsForm == null) {
                response.setOpstatus(Constants.RESPONSE_FAILURE);
                logger.info("修改单据失败，原因是对应单据在数据库中不存在");
            } else {
                pfmsForm.setValueDate(PersonalUtil.getStrToDate(date, Constants.DATETIME_FORMAT_DATE));
                pfmsForm.setTimeNo(peroid);
                pfmsForm.setAmount(new BigDecimal(amount));
                pfmsForm.setUsageLevelOne(proOneId);
                pfmsForm.setUsageLevelTwo(proTwoId);
                pfmsForm.setModifierId(userId);
                pfmsForm.setModifyTime(new Date());
                pfmsForm.setRemark(remark);
                accountService.updateForm(pfmsForm);
                FormVm formVm = accountService.getFormVmListByCondition(null, null, null, null, null, pfmsForm.getId()).get(0);
                FormToShow formToShow = formVmToFromShow(formVm);

                int selectedYear = Integer.parseInt(month.substring(0, 4));
                int seletedMonth = Integer.parseInt(month.substring(4, 6));
                BigDecimal totalAmountIn = accountService.getTotalAmountByMonth(selectedYear, seletedMonth, Constants.PRO_TYPE_INCOME, userId);
                BigDecimal totalAmountOut = accountService.getTotalAmountByMonth(selectedYear, seletedMonth, Constants.PRO_TYPE_EXPEND, userId);
                String orderMonth = date.substring(0, 4).concat(date.substring(5, 7));

                logger.info("修改单据成功");
                response.setOpstatus(Constants.RESPONSE_SUCCESS);
                response.setOpid(formToShow.getId());
                response.setOptype(formToShow.getType());
                response.setOpamount(formToShow.getAmount());
                response.setOpdate(formToShow.getValueDateStr());
                response.setOpperiod(formToShow.getPeroidStr());
                response.setOppro(formToShow.getProOneStr() + "-" + formToShow.getProTwoStr());
                response.setOpremark(formToShow.getRemark());
                response.setOpselectedMonth(orderMonth);
                response.setOptotalAmountIn(PersonalUtil.bigDecimalToString(totalAmountIn));
                response.setOptotalAmountOut(PersonalUtil.bigDecimalToString(totalAmountOut));
            }
        } catch (Exception e) {
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("修改单据失败");
        }
        return response;
    }

    @RequestMapping(value = "/deleteForm.json", method = RequestMethod.POST)
    @ResponseBody
    public DeleteFormResponse deleteForm(@RequestBody Map map, HttpServletRequest request) {
        DeleteFormResponse response = new DeleteFormResponse();
        //解析并判断JSON
        String id = map.get("id").toString();
        String type = map.get("type").toString();
        String month = map.get("month").toString();
        logger.info("删除单据开始，id:[" + id + "],type:[" + type + "],month:[" + month + "]");
        Authentication authentication = (Authentication) request.getSession().getAttribute(Constants.SESSION);
        int userId = authentication.getId();

        try {
            accountService.deleteForm(id);
            int selectedYear = Integer.parseInt(month.substring(0, 4));
            int seletedMonth = Integer.parseInt(month.substring(4, 6));
            BigDecimal totalAmountIn = accountService.getTotalAmountByMonth(selectedYear, seletedMonth, Constants.PRO_TYPE_INCOME, userId);
            BigDecimal totalAmountOut = accountService.getTotalAmountByMonth(selectedYear, seletedMonth, Constants.PRO_TYPE_EXPEND, userId);
            int count = accountService.getMonthOrderCount(selectedYear, seletedMonth, userId);

            logger.info("删除单据成功");
            response.setOpstatus(Constants.RESPONSE_SUCCESS);
            response.setOpid(id);
            response.setOptype(type);
            response.setCount(count);
            response.setOptotalAmountIn(PersonalUtil.bigDecimalToString(totalAmountIn));
            response.setOptotalAmountOut(PersonalUtil.bigDecimalToString(totalAmountOut));
        } catch (Exception e) {
            response.setOpstatus(Constants.RESPONSE_FAILURE);
            logger.info("删除单据失败");
        }
        return response;
    }

    public PfmsForm transToForm(String type, String amount, String proOneId, String proTwoId, String date, String peroid, String remark, int userId) throws ParseException {

        String id = sequenceService.getId(Constants.SEQ_FORMID);
        PfmsForm pfmsForm = new PfmsForm();
        pfmsForm.setId(id);
        pfmsForm.setType(type);
        pfmsForm.setAmount(new BigDecimal(amount));
        pfmsForm.setUsageLevelOne(proOneId);
        pfmsForm.setUsageLevelTwo(proTwoId);
        pfmsForm.setValueDate(PersonalUtil.getStrToDate(date, Constants.DATETIME_FORMAT_DATE));
        pfmsForm.setTimeNo(peroid);
        pfmsForm.setRemark(remark);
        pfmsForm.setCreatorId(userId);
        pfmsForm.setCreateTime(new Date());
        return pfmsForm;
    }

    public FormToShow formVmToFromShow(FormVm formVm) {
        FormToShow formToShow = new FormToShow();
        formToShow.setId(formVm.getId());
        formToShow.setType(formVm.getType());
        formToShow.setAmount(PersonalUtil.bigDecimalToString(formVm.getAmount()));
        formToShow.setValueDate(formVm.getValueDate());
        formToShow.setValueDateStr(PersonalUtil.getDateStr(Constants.DATETIME_FORMAT_DATE, formVm.getValueDate()));
        formToShow.setPeroid(formVm.getTimeNo());
        formToShow.setPeroidStr(getStrOfPeriod(formVm.getTimeNo()));
        formToShow.setProOne(formVm.getUsageLevelOne());
        formToShow.setProOneStr(formVm.getUsageLevelOneName());
        formToShow.setProTwo(formVm.getUsageLevelTwo());
        formToShow.setProTwoStr(formVm.getUsageLevelTwoName());
        formToShow.setRemark(formVm.getRemark());
        return formToShow;
    }

    public String getStrOfPeriod(String period) {
        String periodStr;
        switch (period) {
            case "01":
                periodStr = "早上";
                break;
            case "02":
                periodStr = "上午";
                break;
            case "03":
                periodStr = "中午";
                break;
            case "04":
                periodStr = "下午";
                break;
            case "05":
                periodStr = "傍晚";
                break;
            case "06":
                periodStr = "晚上";
                break;
            case "07":
                periodStr = "午夜";
                break;
            default:
                periodStr = "";
                break;
        }
        return periodStr;
    }

    public static void main(String[] args) {
        DateTime time = new DateTime();
        int year = time.getYear();
        int month = time.getMonthOfYear();
        System.out.println("year:" + year);
        System.out.println("month:" + month);
    }
}
