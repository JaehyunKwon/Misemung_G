package kr.com.misemung.realm.entity

import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import kr.com.misemung.vo.CityInfo

open class CityRecord : RealmObject {
    @PrimaryKey
    var id = 0
    var umdName: String? = null
    var tmX: String? = null
    var tmY: String? = null

    constructor() {}
    constructor(cityInfo: CityInfo) {
        tmX = cityInfo.tmX
        tmY = cityInfo.tmY
    }
}