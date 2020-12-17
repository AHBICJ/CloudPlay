package xyz.ahbicj.snowflake;

public class ID {
    private long id;
    private long millis;
    private Status status;

    public ID(long id, long millis, Status status) {
        this.id = id;
        this.millis = millis;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID{" +
                "id=" + id +
                ", millis=" + millis +
                ", status=" + status +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
