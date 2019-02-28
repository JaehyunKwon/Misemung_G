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

                Number nextID = (realm.where(AirRecord.class).max("id"));
                if (nextID == null) {
                    nextID = 1;
                } else {
                    nextID = nextID.intValue() + 1;
                }

                CityRecord record = new CityRecord(cityInfo);
                record.umdName = umdName;
                record.id = (int) nextID;

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

        public static CityRecord selectByCityData(int id) {

            return Realm.getDefaultInstance().where(CityRecord.class)
                    .equalTo("id", id)
                    .findFirst();
        }

        public static void deleteCityData(int id) {
            Realm realm = Realm.getDefaultInstance();

            realm.executeTransaction(realm1 -> {
                RealmResults<CityRecord> result
                        = realm1.where(CityRecord.class).equalTo("id", id).findAll();
                result.deleteAllFromRealm();
            });
        }

    }
}
