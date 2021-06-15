package kr.com.misemung.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by kjh on 2019-01-21.
 */
class GeoInfo : Serializable {
    @SerializedName("x")
    @Expose
    var x: String? = null

    @SerializedName("y")
    @Expose
    var y: String? = null

    @SerializedName("region_2depth_name")
    @Expose
    var region_2depth_name: String? = null

    @SerializedName("region_3depth_name")
    @Expose
    var region_3depth_name: String? = null
}