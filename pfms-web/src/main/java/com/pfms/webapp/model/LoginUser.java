/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:LoginUser.java, v0.1 2016/7/13 10:57 fred.zhou Exp $
 */
public class LoginUser {

    @NotEmpty(message = "{error.required.loginName}")
    private String loginName;

    @NotEmpty(message = "{error.required.password}")
    @Pattern(regexp = "[a-zA-Z0-9_@!]{8,32}", message = "{error.pattern.password}")
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
