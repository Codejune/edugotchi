package kr.ac.ssu.edugochi;

import java.util.Date;

import io.realm.RealmObject;

public class MeasureTimeObject extends RealmObject {
    private Date date;
    private long timeout;
    private long exp;

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }
    public long getExp() { return exp; }
    public void setExp(long exp) { this.exp = exp; }
}
