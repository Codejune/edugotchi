package kr.ac.ssu.edugochi;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import kr.ac.ssu.edugochi.fragment.MainFragment;
import kr.ac.ssu.edugochi.realm.module.ExpModule;
import kr.ac.ssu.edugochi.realm.module.UserModule;

public class Edugochi extends Application {
    private static final String TAG = Edugochi.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
