package cm.g2i.lalalaworker.models;

import java.io.Serializable;

/**
 * Created by Sim'S on 25/07/2017.
 */

public class HistoryUnit implements Serializable {
    public static int TYPE_CALL = 1;
    public static int TYPE_SMS = 2;

    private User user;
    private Worker worker;
    private int type;
    private String date;

    public HistoryUnit() {
    }

    public HistoryUnit(int type, String date) {
        user = new User(1, "Usr1", "Pass1");
        worker = new Worker(1, "Ademo", "Terrien", "Photo1", "656565656", "Tarterets", "91");
        this.type = type;
        this.date = date;
    }

    public HistoryUnit(User user, Worker worker, int type, String date) {
        this.user = user;
        this.worker = worker;
        this.type = type;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryUnit)) return false;

        HistoryUnit unit = (HistoryUnit) o;

        if (getType() != unit.getType()) return false;
        if (getUser() != null ? !getUser().equals(unit.getUser()) : unit.getUser() != null)
            return false;
        if (getWorker() != null ? !getWorker().equals(unit.getWorker()) : unit.getWorker() != null)
            return false;
        return getDate() != null ? getDate().equals(unit.getDate()) : unit.getDate() == null;

    }

    public boolean semiEquals(HistoryUnit that){
        if (getUser() != null ? !getUser().equals(that.getUser()) : that.getUser() != null)
            return false;
        if (getWorker() != null ? !getWorker().equals(that.getWorker()) : that.getWorker() != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getUser() != null ? getUser().hashCode() : 0;
        result = 31 * result + (getWorker() != null ? getWorker().hashCode() : 0);
        result = 31 * result + getType();
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HistoryUnit{" +
                "user=" + user.toString() +
                ", worker=" + worker.toString() +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
