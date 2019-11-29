package kr.ac.ssu.edugochi.util;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class DBMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        //RealmSchema schema = realm.getSchema();
        if(oldVersion == 0) {
            // DB 버전 0 -> 1
            /*
            RealmObjectSchema measureScheme = schema.get("MeasureObject");
            measureScheme.addField("필드이름", 타입, 필드속성).transform(new RealmObjectSchema.Function() {
                @Override
                public void apply(DynamicRealmObject obj) {
                    obj.set("필드이름", );
                }
            })
             */
            oldVersion++;
        }
        if(oldVersion < newVersion) {
            throw new IllegalStateException(String.format("DBMigration(): fail to v%d to %d", oldVersion, newVersion));
        }
    }
}
