package kr.com.misemung.vo

import java.io.Serializable

/**
 * Created by kjh on 2019-01-21.
 */
class AirInfo : Serializable {
    var date: String? = null
    var so2value: String? = null
    var covalue: String? = null
    var no2value: String? = null
    var o3value: String? = null
    var pm10value: String? = null
    var pm25value: String? = null
    var khaivalue: String? = null
    var khaigrade: String? = null
    var so2grade: String? = null
    var no2grade: String? = null
    var cograde: String? = null
    var o3grade: String? = null
    var pm10grade1h: String? = null
    var pm25grade1h: String? = null
    var totalCount: String? = null
}