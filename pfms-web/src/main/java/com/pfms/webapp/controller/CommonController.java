/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.controller;

import com.pfms.biz.service.ICommonService;
import com.pfms.dal.mybatis.model.PfmsUser;
import com.pfms.util.Constants;
import com.pfms.webapp.model.Authentication;
import com.pfms.webapp.model.LoginUser;
import com.pfms.webapp.model.RegisterUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashSet;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:CommonController.java, v0.1 2016/7/13 10:32 fred.zhou Exp $
 */
@Controller
public class CommonController {

    protected Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ICommonService commonService;

    @RequestMapping(value = "/")
    public ModelAndView start() {
        logger.info("新连接访问系统，准备进行登录操作");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        mav.addObject("user", new LoginUser());
        return mav;
    }

    @RequestMapping(value = "/register")
    public ModelAndView registerOp() {
        logger.info("新连接访问系统，准备进行用户注册操作");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("register");
        mav.addObject("user", new RegisterUser());
        return mav;
    }

    @RequestMapping(value = "/login")
    public ModelAndView loginOp(@Valid @ModelAttribute("user") LoginUser user, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("用户 [" + user.getLoginName() + "] 开始登录系统");
        ModelAndView mav = new ModelAndView();
        if (bindingResult.hasErrors()) {
            logger.info("登录数据校验失败，返回登录页面");
            mav.setViewName("login");
            mav.addObject("user", user);
            return mav;
        }
        if (!commonService.checkUserNameIsExist(user.getLoginName())) {
            logger.info("用户不存在，返回登录页面");
            bindingResult.rejectValue("loginName", "error.notExist.login");
            mav.setViewName("login");
            mav.addObject("user", user);
            return mav;
        }
        PfmsUser pfmsUser = commonService.getPfmsUser(user.getLoginName(), Constants.BY_LOGIN_NAME);
        Boolean isCorrectPwd = commonService.isCorrectPwd(user.getPassword(), pfmsUser.getPassword());
        if (isCorrectPwd) {
            logger.info("密码检查通过");
            Authentication authentication = new Authentication();
            authentication.setLoginName(pfmsUser.getLoginName());
            authentication.setNickName(pfmsUser.getName());
            authentication.setPassword(pfmsUser.getPassword());
            authentication.setId(pfmsUser.getId());
            authentication.setValid(false);
            authentication.setMyActions(new HashSet<String>());
            request.getSession(true).setAttribute("session_authentication", authentication);
            logger.info("用户登录成功，重定向到欢迎页面");
            return new ModelAndView("redirect:/welcome");
        } else {
            logger.info("密码检查不通过，返回登录页面");
            bindingResult.rejectValue("password", "error.incorrect.password");
            mav.setViewName("login");
            mav.addObject("user", user);
            return mav;
        }
    }

    @RequestMapping(value = "/registerCheck")
    public ModelAndView registerChk(@Valid @ModelAttribute("user") RegisterUser user, BindingResult bindingResult, HttpSession httpSession) throws ParseException {
        logger.info("开始进行用户注册");
        ModelAndView mav = new ModelAndView();
        if (bindingResult.hasErrors()) {
            logger.info("注册数据校验失败，返回注册页面");
            mav.setViewName("register");
            mav.addObject("user", user);
            return mav;
        }
        if (commonService.checkUserNameIsExist(user.getLoginName())) {
            logger.info("待注册用户已经存在，返回注册页面");
            bindingResult.rejectValue("loginName", "error.exist.loginName");
            mav.setViewName("register");
            mav.addObject("user", user);
            return mav;
        } else {
            PfmsUser pfmsUser = new PfmsUser();
            pfmsUser.setLoginName(user.getLoginName());
            pfmsUser.setPassword(user.getPassword());
            pfmsUser.setName(user.getNickName());
            pfmsUser.setGender(user.getGender());
            pfmsUser.setBirthDate(user.getBirthdate());
            if (user.getEmail() != null && user.getEmail().length() > 0) {
                pfmsUser.setMailAddress(user.getEmail());
            }
            pfmsUser.setTimestamp(new Date(System.currentTimeMillis()));
            //设置注册用户为首次登录
            pfmsUser.setFirstLogin(Constants.USER_FIRST_LOGIN_Y);
            //设置注册用户的用户种类为“1”普通用户
            pfmsUser.setUserType(Constants.USER_TYPE_NORMAL);
            commonService.insertPfmsUser(pfmsUser);
            pfmsUser = commonService.getPfmsUser(pfmsUser.getLoginName(), Constants.BY_LOGIN_NAME);
            Authentication authentication = new Authentication();
            authentication.setLoginName(pfmsUser.getLoginName());
            authentication.setNickName(pfmsUser.getName());
            authentication.setPassword(pfmsUser.getPassword());
            authentication.setId(pfmsUser.getId());
            authentication.setValid(false);
            authentication.setMyActions(new HashSet<String>());
            httpSession.setAttribute("session_authentication", authentication);
            logger.info("用户注册成功，重定向到欢迎页面");
            return new ModelAndView("redirect:/welcome");
        }
    }

    @RequestMapping(value = "/welcome")
    public String redirectToWelcome(ModelMap map) {
        return "welcome";
    }
}
