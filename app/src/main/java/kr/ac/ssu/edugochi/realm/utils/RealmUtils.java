package kr.ac.ssu.edugochi.realm.utils;

import io.realm.RealmConfiguration;

public class RealmUtils {

    public static RealmConfiguration getDatabaseConfiguration(String filename) {
        return new RealmConfiguration.Builder()
                .assetFile(filename)
                .schemaVersion(0)
                .build();
    }
}
