package cm.g2i.lalalaworker.models;

import java.io.Serializable;

/**
 * Created by Sim'S on 09/09/2017.
 */

public class Strikes implements Serializable{
    public static final int TYPE_BAD_WORK = 117;
    public static final int TYPE_ACCOUNT_EXPIRED_ONE = 118;
    public static final int TYPE_ACCOUNT_EXPIRED_TWO = 119;

    private int ID;
    private int type;
    private String message;
    private int gravity;
    private int workerID;

    public Strikes(){}

    public Strikes(int ID, int type, String message, int gravity, int workerID) {
        this.ID = ID;
        this.type = type;
        this.message = message;
        this.gravity = gravity;
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

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
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
        if (!(o instanceof Strikes)) return false;

        Strikes strikes = (Strikes) o;

        if (getID() != strikes.getID()) return false;
        if (getType() != strikes.getType()) return false;
        if (getGravity() != strikes.getGravity()) return false;
        if (getWorkerID() != strikes.getWorkerID()) return false;
        return getMessage() != null ? getMessage().equals(strikes.getMessage()) : strikes.getMessage() == null;

    }

    @Override
    public int hashCode() {
        int result = getID();
        result = 31 * result + getType();
        result = 31 * result + (getMessage() != null ? getMessage().hashCode() : 0);
        result = 31 * result + getGravity();
        result = 31 * result + getWorkerID();
        return result;
    }

    @Override
    public String toString() {
        return "Strikes{" +
                "ID=" + ID +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", gravity=" + gravity +
                ", workerID=" + workerID +
                '}';
    }
}
