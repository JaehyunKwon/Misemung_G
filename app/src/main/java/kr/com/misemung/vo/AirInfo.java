package kr.com.misemung.vo;

import java.io.Serializable;

/**
 * Created by soohoon.kang on 2017-04-03.
 */

public class AirInfo implements Serializable {

    private String date;
    private String so2value;
    private String covalue;
    private String no2value;
    private String o3value;
    private String pm10value;
    private String pm25value;
    private String khaivalue;

    private String khaigrade;
    private String so2grade;
    private String no2grade;
    private String cograde;
    private String o3grade;
    private String pm10Grade1h;
    private String pm25Grade1h;
    private String totalCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSo2value() {
        return so2value;
    }

    public void setSo2value(String so2value) {
        this.so2value = so2value;
    }

    public String getCovalue() {
        return covalue;
    }

    public void setCovalue(String covalue) {
        this.covalue = covalue;
    }

    public String getNo2value() {
        return no2value;
    }

    public void setNo2value(String no2value) {
        this.no2value = no2value;
    }

    public String getO3value() {
        return o3value;
    }

    public void setO3value(String o3value) {
        this.o3value = o3value;
    }

    public String getPm10value() {
        return pm10value;
    }

    public void setPm10value(String pm10value) {
        this.pm10value = pm10value;
    }

    public String getPm25value() {
        return pm25value;
    }

    public void setPm25value(String pm25value) {
        this.pm25value = pm25value;
    }

    public String getKhaivalue() {
        return khaivalue;
    }

    public void setKhaivalue(String khaivalue) {
        this.khaivalue = khaivalue;
    }

    public String getKhaigrade() {
        return khaigrade;
    }

    public void setKhaigrade(String khaigrade) {
        this.khaigrade = khaigrade;
    }

    public String getSo2grade() {
        return so2grade;
    }

    public void setSo2grade(String so2grade) {
        this.so2grade = so2grade;
    }

    public String getNo2grade() {
        return no2grade;
    }

    public void setNo2grade(String no2grade) {
        this.no2grade = no2grade;
    }

    public String getCograde() {
        return cograde;
    }

    public void setCograde(String cograde) {
        this.cograde = cograde;
    }

    public String getO3grade() {
        return o3grade;
    }

    public void setO3grade(String o3grade) {
        this.o3grade = o3grade;
    }

    public String getPm10grade1h() {
        return pm10Grade1h;
    }

    public void setPm10grade1h(String pm10Grade1h) {
        this.pm10Grade1h = pm10Grade1h;
    }

    public String getPm25grade1h() {
        return pm25Grade1h;
    }

    public void setPm25grade1h(String pm25Grade1h) {
        this.pm25Grade1h = pm25Grade1h;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }
}
