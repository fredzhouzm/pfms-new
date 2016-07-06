package com.pfms.dal.mybatis.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fred on 16/2/28.
 */
public interface CustomMapper {

    BigDecimal selTotAmtByMon(@Param("type") String type, @Param("firstDate") Date firstDate, @Param("lastDate") Date lastDate, @Param("creator_id") int creatorId);
}