package kr.ac.ssu.edugochi.object;

import io.realm.RealmObject;

public class MeasureTimeObject extends RealmObject {
    private String subject;
    private String date;
    private long timeout;
    private long exp;

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }
    public long getExp() { return exp; }
    public void setExp(long exp) { this.exp = exp; }
}
