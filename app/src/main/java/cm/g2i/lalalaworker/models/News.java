package cm.g2i.lalalaworker.models;

import android.content.Context;

import cm.g2i.lalalaworker.R;

import java.io.Serializable;

/**
 * Created by Sim'S on 09/09/2017.
 */

public class News implements Serializable{
    public static final int TYPE_STRIKE = 117;
    public static final int TYPE_NOTE = 118;
    public static final int TYPE_COMMENT = 119;
    public static final int TYPE_STRIKE_LEVEL = 120;
    public static final int TYPE_ACCOUNT_EXPIRATION = 121;

    private int ID;
    private int type;
    private String message;
    private boolean isNew;
    private int workerID;

    public News(){}

    public News(int ID, int type, String message, int workerID) {
        this.ID = ID;
        this.type = type;
        this.message = message;
        this.isNew = true;
        this.workerID = workerID;
    }

    public News(int ID, int type, String message, boolean isNew, int workerID) {
        this.ID = ID;
        this.type = type;
        this.message = message;
        this.isNew = isNew;
        this.workerID = workerID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getWorkerID() {
        return workerID;
    }

    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;

        News news = (News) o;

        if (getID() != news.getID()) return false;
        if (getType() != news.getType()) return false;
        if (getWorkerID() != news.getWorkerID()) return false;
        return getMessage() != null ? getMessage().equals(news.getMessage()) : news.getMessage() == null;

    }

    @Override
    public int hashCode() {
        int result = getID();
        result = 31 * result + getType();
        result = 31 * result + (getMessage() != null ? getMessage().hashCode() : 0);
        result = 31 * result + (isNew() ? 1 : 0);
        result = 31 * result + getWorkerID();
        return result;
    }

    @Override
    public String toString() {
        return "News{" +
                "ID=" + ID +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", isNew=" + isNew +
                ", workerID=" + workerID +
                '}';
    }

    public static News buildNewForInsertFromUsr(Worker worker, User user, int type, String add, Context context){
        News news = new News();
        news.setType(type);
        news.setWorkerID(worker.getID());
        if (type == TYPE_COMMENT){
            news.setMessage(user.getName() + " " + context.getString(R.string.news_message_for_comment));
        }
        else if (type == TYPE_NOTE){
            news.setMessage(user.getName() + " " + context.getString(R.string.news_message_for_note) + " " + add);
        }
        else if (type == TYPE_STRIKE){
            news.setMessage(user.getName() + " " + context.getString(R.string.news_message_for_signal) + " " + add);
        }
        return news;
    }

    public static News buildNewForInsertFromApp(int workerID, int type, String add1, String add2, Context context){
        News news = new News();
        news.setType(type);
        news.setWorkerID(workerID);
        if (type == TYPE_STRIKE_LEVEL){
            news.setMessage(context.getString(R.string.news_message_for_strike_lvl).replace("??1", add1).replace("??2", add2));
        }
        else if (type == TYPE_ACCOUNT_EXPIRATION){
            news.setMessage(context.getString(R.string.news_message_for_account_expiration).replace("??", add1));
        }
        return news;
    }

}
