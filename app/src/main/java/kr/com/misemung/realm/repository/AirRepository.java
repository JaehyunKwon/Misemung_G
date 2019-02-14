package kr.com.misemung.realm.repository;



import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import kr.com.misemung.realm.entity.AirRecord;
import kr.com.misemung.vo.AirInfo;

public class AirRepository {

    public static class Air {
        public static void set(String stationName, AirInfo airInfo) {
            Realm realm = Realm.getDefaultInstance();

            try {
                realm.beginTransaction();

                AirRecord record = new AirRecord(airInfo);
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

        public static RealmResults<AirRecord> selectByList(String stationName) {

            return Realm.getDefaultInstance().where(AirRecord.class)
                    .equalTo("stationName", stationName)
                    .findAll();
        }

    }
}
