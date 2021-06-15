package kr.com.misemung.realm.repository

import io.realm.Realm
import kr.com.misemung.realm.entity.AirRecord
import kr.com.misemung.realm.entity.CityRecord
import kr.com.misemung.vo.CityInfo

class CityRepository {
    object City {
        operator fun set(umdName: String?, cityInfo: CityInfo) {
            val realm = Realm.getDefaultInstance()
            try {
                realm.beginTransaction()
                var nextID = realm.where(AirRecord::class.java).max("id")
                nextID = if (nextID == null) {
                    2
                } else {
                    nextID.toInt() + 1
                }
                val record = CityRecord(cityInfo)
                record.umdName = umdName
                record.id = nextID
                realm.insertOrUpdate(record)
                realm.commitTransaction()
            } catch (e: Exception) {
                e.printStackTrace()
                realm.cancelTransaction()
            } finally {
                realm.close()
            }
        }

        fun setCurrentCity(id: Int, umdName: String?, cityInfo: CityInfo) {
            val realm = Realm.getDefaultInstance()
            try {
                realm.beginTransaction()
                val record = CityRecord(cityInfo)
                record.umdName = umdName
                record.id = id
                realm.insertOrUpdate(record)
                realm.commitTransaction()
            } catch (e: Exception) {
                e.printStackTrace()
                realm.cancelTransaction()
            } finally {
                realm.close()
            }
        }

        fun selectByCityData(id: Int): CityRecord? {
            return Realm.getDefaultInstance().where(CityRecord::class.java)
                .equalTo("id", id)
                .findFirst()
        }

        fun deleteCityData(id: Int) {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction { realm1: Realm ->
                val result = realm1.where(
                    CityRecord::class.java
                ).equalTo("id", id).findAll()
                result.deleteAllFromRealm()
            }
        }
    }
}