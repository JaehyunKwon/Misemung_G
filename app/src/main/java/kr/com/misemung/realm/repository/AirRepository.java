package kr.com.misemung.realm.repository;




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

                Number nextID = (realm.where(AirRecord.class).max("id"));
                if (nextID == null) {
                    nextID = 2;
                } else {
                    nextID = nextID.intValue() + 1;
                }

                AirRecord record = new AirRecord(airInfo);
                record.stationName = stationName;
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

        public static void setCurrent(int id, String stationName, AirInfo airInfo) {
            Realm realm = Realm.getDefaultInstance();

            try {
                realm.beginTransaction();

                AirRecord record = new AirRecord(airInfo);
                record.stationName = stationName;
                record.id = id;

                realm.insertOrUpdate(record);
                realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                realm.cancelTransaction();
            } finally {
                realm.close();
            }
        }

        public static Number getId() {
            Realm realm = Realm.getDefaultInstance();

            Number nextID = (realm.where(AirRecord.class).max("id"));
            if (nextID == null) {
                nextID = 2;
            } else {
                nextID = nextID.intValue() + 1;
            }
            return nextID;
        }

        public static RealmResults<AirRecord> selectByAllList() {

            return Realm.getDefaultInstance().where(AirRecord.class)
                    .notEqualTo("id", 1)
                    .findAll();
        }

        public static AirRecord selectByGPSData(int id) {

            return Realm.getDefaultInstance().where(AirRecord.class)
                    .equalTo("id", id)
                    .findFirst();
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
