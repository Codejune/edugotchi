package kr.ac.ssu.edugochi.adapter;

public class RankListItem {
    private String subject;
    private long time;
    private long exp;

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void plusTime(long time) {
        this.time += time;
    }

    public void plusExp(long exp) {
        this.exp += exp;
    }

    public String getSubject() {
        return subject;
    }

    public long getTime() {
        return time;
    }

    public long getExp() {
        return exp;
    }


    public RankListItem() {
        this.subject = null;
        this.time = 0;
        this.exp = 0;
    }
}
