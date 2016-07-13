/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Date;


/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:RegisterUser.java, v0.1 2016/7/13 10:38 fred.zhou Exp $
 */
public class RegisterUser {

    @NotEmpty(message = "{error.required.loginName}")
    private String loginName;

    @NotEmpty(message = "{error.required.password}")
    @Pattern(regexp = "[a-zA-Z0-9_@!]{8,32}", message = "{error.pattern.password}")
    private String password;

    @NotEmpty(message = "{error.required.nickName}")
    private String nickName;

    @NotEmpty(message = "{error.required.gender}")
    @Pattern(regexp = "1|2", message = "{error.pattern.gender}")
    private String gender;

    @NotNull(message = "{error.required.birthdate}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "{error.past.birthdate}")
    private Date birthdate;

    @Email(message = "{error.pattern.email}")
    private String email;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
