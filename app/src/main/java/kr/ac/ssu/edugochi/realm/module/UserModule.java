package kr.ac.ssu.edugochi.realm.module;

import io.realm.annotations.RealmModule;
import kr.ac.ssu.edugochi.object.Character;
import kr.ac.ssu.edugochi.object.MeasureData;

@RealmModule(classes = {Character.class, MeasureData.class})
public class UserModule {
}
