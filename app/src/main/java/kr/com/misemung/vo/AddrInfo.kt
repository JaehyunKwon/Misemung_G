package kr.com.misemung.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by kjh on 2019-01-21.
 */
class AddrInfo : Serializable {
    @SerializedName("address_name")
    @Expose
    var address_name: String? = null

    @SerializedName("address_type")
    @Expose
    var address_type: String? = null

    @SerializedName("road_address")
    @Expose
    var road_address: String? = null

    @SerializedName("x")
    @Expose
    var x: String? = null

    @SerializedName("y")
    @Expose
    var y: String? = null
}