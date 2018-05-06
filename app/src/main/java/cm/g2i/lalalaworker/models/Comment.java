package cm.g2i.lalalaworker.models;

/**
 * Created by Sim'S on 21/07/2017.
 */

public class Comment {
    private int ID;
    private Worker worker;
    private User user;
    private String date;
    private String comment;

    public Comment() {
    }

    public Comment(Worker worker, User user, String date, String comment) {
        this.worker = worker;
        this.user = user;
        this.date = date;
        this.comment = comment;
    }

    public Comment(int ID, Worker worker, User user, String date, String comment) {
        this.ID = ID;
        this.worker = worker;
        this.user = user;
        this.date = date;
        this.comment = comment;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
