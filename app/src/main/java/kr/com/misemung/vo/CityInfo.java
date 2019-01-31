package kr.com.misemung.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kjh on 2019-01-30.
 */

public class CityInfo implements Serializable {
    @SerializedName("sidoName")
    @Expose
    private String sidoName;
    @SerializedName("sggName")
    @Expose
    private String sggName;
    @SerializedName("umdName")
    @Expose
    private String umdName;
    @SerializedName("tmX")
    @Expose
    private String tmX;
    @SerializedName("tmY")
    @Expose
    private String tmY;

    public String getSidoName() {
        return sidoName;
    }

    public void setSidoName(String sidoName) {
        this.sidoName = sidoName;
    }

    public String getSggName() {
        return sggName;
    }

    public void setSggName(String sggName) {
        this.sggName = sggName;
    }

    public String getUmdName() {
        return umdName;
    }

    public void setUmdName(String umdName) {
        this.umdName = umdName;
    }

    public String getTmX() {
        return tmX;
    }

    public void setTmX(String tmX) {
        this.tmX = tmX;
    }

    public String getTmY() {
        return tmY;
    }

    public void setTmY(String tmY) {
        this.tmY = tmY;
    }
}
