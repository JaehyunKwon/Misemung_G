package kr.com.misemung.realm.entity;

import android.support.annotation.NonNull;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import kr.com.misemung.vo.AirInfo;

@RealmClass
public class AirRecord implements RealmModel {

    @PrimaryKey
    public int id;

    public String stationName;
    public String date = "";
    public String so2value = "";
    public String covalue = "";
    public String no2value = "";
    public String o3value = "";
    public String pm10value = "";
    public String pm25value = "";
    public String khaivalue = "";

    public String khaigrade = "";
    public String so2grade = "";
    public String no2grade = "";
    public String cograde = "";
    public String o3grade = "";
    public String pm10Grade1h = "";
    public String pm25Grade1h = "";
    public String totalCount = "";

    public AirRecord() {}

    public AirRecord(@NonNull AirInfo airInfo) {
        this.date = airInfo.getDate();
        this.so2value = airInfo.getSo2value();
        this.covalue = airInfo.getCovalue();
        this.no2value = airInfo.getNo2value();
        this.o3value = airInfo.getO3value();
        this.pm10value = airInfo.getPm10value();
        this.pm25value = airInfo.getPm25value();
        this.khaivalue = airInfo.getKhaivalue();

        this.khaigrade = airInfo.getKhaigrade();
        this.so2grade = airInfo.getSo2grade();
        this.no2grade = airInfo.getNo2grade();
        this.cograde = airInfo.getCograde();
        this.o3grade = airInfo.getO3grade();
        this.pm10Grade1h = airInfo.getPm10grade1h();
        this.pm25Grade1h = airInfo.getPm25grade1h();
        this.totalCount = airInfo.getTotalCount();
    }

}
