package kr.ac.ssu.edugochi.util;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmUtils {
    public static Realm getExpTable(Context context) {
        Realm.init(context);
        return Realm.getInstance(getDatabaseConfiguration());
    }

    private static RealmConfiguration getDatabaseConfiguration() {
        return new RealmConfiguration.Builder()
                .assetFile("EXPTABLE.realm")
                .readOnly()
                .build();
    }
}
