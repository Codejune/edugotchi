package kr.ac.ssu.edugochi;

import android.app.Application;

import io.realm.Realm;

public class Edugochi extends Application {
    private static final String TAG = Edugochi.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
