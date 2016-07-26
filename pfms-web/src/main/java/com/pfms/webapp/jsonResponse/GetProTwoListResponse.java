/**
 * 汇付天下有限公司 Copyright (c) 2006-2016 ChinaPnR,Inc.All Rights Reserved.
 */
package com.pfms.webapp.jsonResponse;

import java.util.HashMap;

/**
 * <h1>Class Description</h1>
 *
 * @author fred.zhou
 * @version $ Id:GetProTwoListResponse.java, v0.1 2016/7/26 16:32 fred.zhou Exp $
 */
public class GetProTwoListResponse {

    //状态
    private String opstatus;

    //二级科目Map对象
    private HashMap<String, String> opmap;

    public String getOpstatus() {
        return opstatus;
    }

    public void setOpstatus(String opstatus) {
        this.opstatus = opstatus;
    }

    public HashMap<String, String> getOpmap() {
        return opmap;
    }

    public void setOpmap(HashMap<String, String> opmap) {
        this.opmap = opmap;
    }
}
