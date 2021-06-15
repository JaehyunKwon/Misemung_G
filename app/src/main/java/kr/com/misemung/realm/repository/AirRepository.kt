package kr.com.misemung.realm.repository

import io.realm.Realm
import io.realm.RealmResults
import kr.com.misemung.realm.entity.AirRecord
import kr.com.misemung.vo.AirInfo

class AirRepository {
    object Air {
        operator fun set(stationName: String?, airInfo: AirInfo) {
            val realm = Realm.getDefaultInstance()
            try {
                realm.beginTransaction()
                var nextID = realm.where(AirRecord::class.java).max("id")
                nextID = if (nextID == null) {
                    2
                } else {
                    nextID.toInt() + 1
                }
                val record = AirRecord(airInfo)
                record.stationName = stationName
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

        fun setCurrent(id: Int, stationName: String?, airInfo: AirInfo) {
            val realm = Realm.getDefaultInstance()
            try {
                realm.beginTransaction()
                val record = AirRecord(airInfo)
                record.stationName = stationName
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

        val id: Number
            get() {
                val realm = Realm.getDefaultInstance()
                var nextID = realm.where(AirRecord::class.java).max("id")
                nextID = if (nextID == null) {
                    2
                } else {
                    nextID.toInt() + 1
                }
                return nextID
            }

        fun selectByAllList(): RealmResults<AirRecord> {
            return Realm.getDefaultInstance().where(AirRecord::class.java)
                .findAll()
        }

        fun selectByGPSData(id: Int): AirRecord? {
            return Realm.getDefaultInstance().where(AirRecord::class.java)
                .equalTo("id", id)
                .findFirst()
        }

        fun selectByDustData(id: Int, stationName: String?): AirRecord? {
            return Realm.getDefaultInstance().where(AirRecord::class.java)
                .equalTo("id", id)
                .equalTo("stationName", stationName)
                .findFirst()
        }

        fun updateDustData(id: Int, airInfo: AirInfo, stationName: String?) {
            val realm = Realm.getDefaultInstance()
            try {
                realm.beginTransaction()
                val record = AirRecord(airInfo)
                record.id = id
                record.stationName = stationName
                realm.insertOrUpdate(record)
                realm.commitTransaction()
            } catch (e: Exception) {
                e.printStackTrace()
                realm.cancelTransaction()
            } finally {
                realm.close()
            }
        }

        fun deleteDustData(id: Int) {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction { realm1: Realm ->
                val result = realm1.where(
                    AirRecord::class.java
                ).equalTo("id", id).findAll()
                result.deleteAllFromRealm()
            }
        }
    }
}