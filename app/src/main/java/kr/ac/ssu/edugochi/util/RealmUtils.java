package kr.ac.ssu.edugochi.util;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmUtils {
    public static Realm getRealm(Context context, String filename) {
        return Realm.getInstance(getDatabaseConfiguration(filename));
    }

    private static RealmConfiguration getDatabaseConfiguration(String filename) {
        return new RealmConfiguration.Builder()
                .assetFile(filename)
                .name(filename)
                .build();
    }
}
