package kr.com.misemung.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by kjh on 2019-01-30.
 */
class CityInfo : Serializable {
    @SerializedName("sidoName")
    @Expose
    var sidoName: String? = null

    @SerializedName("sggName")
    @Expose
    var sggName: String? = null

    @SerializedName("umdName")
    @Expose
    var umdName: String? = null

    @SerializedName("tmX")
    @Expose
    var tmX: String? = null

    @SerializedName("tmY")
    @Expose
    var tmY: String? = null
}