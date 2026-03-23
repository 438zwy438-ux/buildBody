package com.cdp.zwy.buildbody.common.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class AgeCalculator {
    /**
     * 根据出生日期计算年龄
     * @param birthDate 出生日期
     * @return 年龄
     */
    public static int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }
        
        LocalDate birthLocalDate = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate currentDate = LocalDate.now();
        
        return Period.between(birthLocalDate, currentDate).getYears();
    }
}