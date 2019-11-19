package kr.ac.ssu.edugochi.object;

import io.realm.RealmObject;

public class CharacterObject extends RealmObject {
    private String name;
    private int lv;
    private long exp;

    public void setName(String name) {  this.name = name; }
    public String getName() { return this.name; }
    public void setLv(int lv) { this.lv = lv; }
    public int getLv() { return this.lv; }
    public void setExp(long exp) { this.exp = exp; }
    public long getExp() { return this.exp; };

}
