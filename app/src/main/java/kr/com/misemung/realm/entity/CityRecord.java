package kr.com.misemung.realm.entity;

import android.support.annotation.NonNull;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import kr.com.misemung.vo.AirInfo;
import kr.com.misemung.vo.CityInfo;

@RealmClass
public class CityRecord implements RealmModel {

    @PrimaryKey
    public String umdName;
    public String tmX;
    public String tmY;


    public CityRecord() {}

    public CityRecord(@NonNull CityInfo cityInfo) {
        this.tmX = cityInfo.getTmX();
        this.tmY = cityInfo.getTmY();
    }

}
