package kr.ac.ssu.edugochi.object;

import io.realm.RealmObject;

public class ExpTable extends RealmObject {
    private long lv;
    private long exp;
    private long interval;

    public long getLv() {
        return lv;
    }

    public void setLv(long lv) {
        this.lv = lv;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }


}
