package kr.ac.ssu.edugochi.object;

public class SubjectListItem {
    private String subject;
    private long timeout;

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public SubjectListItem(String subject, long timeout) {
        this.subject = subject;
        this.timeout = timeout;
    }
}
