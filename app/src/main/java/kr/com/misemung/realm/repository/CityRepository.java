package kr.com.misemung.realm.repository;



import io.realm.Realm;
import io.realm.RealmResults;
import kr.com.misemung.realm.entity.AirRecord;
import kr.com.misemung.realm.entity.CityRecord;
import kr.com.misemung.vo.AirInfo;
import kr.com.misemung.vo.CityInfo;

public class CityRepository {

    public static class City {
        public static void set(String umdName, CityInfo cityInfo) {
            Realm realm = Realm.getDefaultInstance();

            try {
                realm.beginTransaction();

                CityRecord record = new CityRecord(cityInfo);
                record.umdName = umdName;

                realm.insertOrUpdate(record);
                realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                realm.cancelTransaction();
            } finally {
                realm.close();
            }
        }

        public static RealmResults<CityRecord> selectByCityList() {

            return Realm.getDefaultInstance().where(CityRecord.class)
                    .findAll();
        }

    }
}
