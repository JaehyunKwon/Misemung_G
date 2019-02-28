package kr.com.misemung.realm.repository;



import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import kr.com.misemung.realm.entity.AirRecord;
import kr.com.misemung.vo.AirInfo;

public class AirRepository {

    public static class Air {
        public static void set(int id, String stationName, AirInfo airInfo) {
            Realm realm = Realm.getDefaultInstance();

            try {
                realm.beginTransaction();

                int nextID = id;
                Log.d("AirRepository", "nextID ==> " + nextID);

                AirRecord record = new AirRecord(airInfo);
                record.stationName = stationName;
                record.id = nextID;

                realm.insertOrUpdate(record);
                realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                realm.cancelTransaction();
            } finally {
                realm.close();
            }
        }

        public static RealmResults<AirRecord> selectByAllList() {

            return Realm.getDefaultInstance().where(AirRecord.class)
                    .findAll();
        }

        public static RealmResults<AirRecord> selectByList(String stationName) {

            return Realm.getDefaultInstance().where(AirRecord.class)
                    .equalTo("stationName", stationName)
                    .findAll();
        }

        public static AirRecord selectByDustData(int id, String stationName) {

            return Realm.getDefaultInstance().where(AirRecord.class)
                    .equalTo("id", id)
                    .equalTo("stationName", stationName)
                    .findFirst();
        }

        public static void updateDustData(int id, AirInfo airInfo, String stationName) {
            Realm realm = Realm.getDefaultInstance();

            try {
                realm.beginTransaction();

                AirRecord record = new AirRecord(airInfo);
                record.id = id;
                record.stationName = stationName;

                realm.insertOrUpdate(record);
                realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                realm.cancelTransaction();
            } finally {
                realm.close();
            }
        }

        public static void deleteDustData(int id) {
            Realm realm = Realm.getDefaultInstance();

            realm.executeTransaction(realm1 -> {
                RealmResults<AirRecord> result
                        = realm1.where(AirRecord.class).equalTo("id", id).findAll();
                result.deleteAllFromRealm();
            });
        }

    }
}
