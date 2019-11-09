package kr.ac.ssu.edugochi;

import java.util.Date;

import io.realm.RealmObject;

public class MeasureTimeObject extends RealmObject {
    public Date date;
    public long timeout;
}
