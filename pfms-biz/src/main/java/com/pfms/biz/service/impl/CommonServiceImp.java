package com.pfms.biz.service.impl;

import com.pfms.biz.service.ICommonService;
import com.pfms.dal.mybatis.dao.PfmsUserMapper;
import com.pfms.dal.mybatis.model.PfmsUser;
import com.pfms.dal.mybatis.model.PfmsUserExample;
import com.pfms.util.Constants;
import com.pfms.util.SecurOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Fred on 2015/12/7.
 */
@Service
public class CommonServiceImp implements ICommonService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    PfmsUserMapper pfmsUserMapper;

    @Override
    public boolean birthdateCheck(String birthdate) {
        logger.info("开始方法：检查出生日期[" + birthdate + "]");
        Date birthdateDB = null;
        try {
            birthdateDB = sdf.parse(birthdate);
        } catch (ParseException e) {
            return false;
        }
        return birthdateDB.after(new Date());
    }

    @Override
    public boolean checkUserNameIsExist(String userName) {
        logger.info("开始方法：检查用户登录名[" + userName + "]是否已经存在");
        PfmsUserExample pue = new PfmsUserExample();
        pue.createCriteria().andLoginNameEqualTo(userName);
        List<PfmsUser> pu = pfmsUserMapper.selectByExample(pue);
        return pu.size() > 0 ? true : false;
    }

    @Override
    @Transactional
    public void insertPfmsUser(PfmsUser pfmsUser) {
        logger.info("开始方法：向数据表插入用户相关数据[" + pfmsUser.getLoginName() + "," + pfmsUser.getName() + "," + pfmsUser.getGender() + "," + pfmsUser.getUserType() + "," + pfmsUser.getBirthDate() + "," + pfmsUser.getMailAddress() + "]");
        pfmsUser.setPassword(SecurOp.sha1(pfmsUser.getPassword()));
        pfmsUserMapper.insert(pfmsUser);
    }

    @Override
    public PfmsUser getPfmsUser(Object object, String types) {
        logger.info("开始方法：根据单个条件[" + types + "/" + object.toString() + "]获取用户信息");
        PfmsUserExample pue = new PfmsUserExample();
        if (types == Constants.BY_ID) {
            pue.createCriteria().andIdEqualTo((Integer) object);
        } else if (types == Constants.BY_LOGIN_NAME) {
            pue.createCriteria().andLoginNameEqualTo((String) object);
        }
        PfmsUser pfmsUser = pfmsUserMapper.selectByExample(pue).get(0);
        return pfmsUser;
    }

    @Override
    public boolean isCorrectPwd(String inputPwd, String dbPwd) {
        logger.info("开始方法：判断用户登录的密码是否正确");
        String secPwd = SecurOp.sha1(inputPwd);
        logger.info("加密后的密码：" + secPwd);
        logger.info("数据库取出的密码：" + dbPwd);
        if (secPwd != null && !"".equals(secPwd) && secPwd.equals(dbPwd)) {
            return true;
        } else {
            return false;
        }
    }
}
