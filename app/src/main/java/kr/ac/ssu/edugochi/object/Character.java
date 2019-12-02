package kr.ac.ssu.edugochi.object;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Character extends RealmObject {
    @Required
    private String name;
    private long lv;
    private long exp;
    private RealmList<String> subject;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public long getLv() { return lv; }

    public void setLv(long lv) { this.lv = lv; }

    public long getExp() { return exp; }

    public void setExp(long exp) { this.exp = exp; }

    public RealmList<String> getSubject() { return subject; }

    public void setSubject(RealmList<String> subject) { this.subject = subject; }


}
