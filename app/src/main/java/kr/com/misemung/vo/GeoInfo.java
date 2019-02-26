package kr.com.misemung.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kjh on 2019-01-21.
 */

public class GeoInfo implements Serializable {
    @SerializedName("x")
    @Expose
    private String x;
    @SerializedName("y")
    @Expose
    private String y;
    @SerializedName("region_2depth_name")
    @Expose
    private String region_2depth_name;
    @SerializedName("region_3depth_name")
    @Expose
    private String region_3depth_name;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getRegion_2depth_name() {
        return region_2depth_name;
    }

    public void setRegion_2depth_name(String region_2depth_name) {
        this.region_2depth_name = region_2depth_name;
    }

    public String getRegion_3depth_name() {
        return region_3depth_name;
    }

    public void setRegion_3depth_name(String region_3depth_name) {
        this.region_3depth_name = region_3depth_name;
    }
}
