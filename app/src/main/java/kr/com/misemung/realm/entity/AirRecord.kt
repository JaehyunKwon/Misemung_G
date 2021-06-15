package kr.com.misemung.realm.entity

import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import kr.com.misemung.vo.AirInfo

open class AirRecord : RealmObject {
    @PrimaryKey
    var id = 0
    var stationName: String? = ""
    var date: String? = ""
    var so2value: String? = ""
    var covalue: String? = ""
    var no2value: String? = ""
    var o3value: String? = ""
    var pm10value: String? = ""
    var pm25value: String? = ""
    var khaivalue: String? = ""
    var khaigrade: String? = ""
    var so2grade: String? = ""
    var no2grade: String? = ""
    var cograde: String? = ""
    var o3grade: String? = ""
    var pm10Grade1h: String? = ""
    var pm25Grade1h: String? = ""
    var totalCount: String? = ""

    constructor()
    constructor(airInfo: AirInfo) {
        date = airInfo.date
        so2value = airInfo.so2value
        covalue = airInfo.covalue
        no2value = airInfo.no2value
        o3value = airInfo.o3value
        pm10value = airInfo.pm10value
        pm25value = airInfo.pm25value
        khaivalue = airInfo.khaivalue
        khaigrade = airInfo.khaigrade
        so2grade = airInfo.so2grade
        no2grade = airInfo.no2grade
        cograde = airInfo.cograde
        o3grade = airInfo.o3grade
        pm10Grade1h = airInfo.pm10grade1h
        pm25Grade1h = airInfo.pm25grade1h
        totalCount = airInfo.totalCount
    }
}