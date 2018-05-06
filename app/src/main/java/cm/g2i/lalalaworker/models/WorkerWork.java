package cm.g2i.lalalaworker.models;

import java.io.Serializable;

/**
 * Created by Sim'S on 06/07/2017.
 */

public class WorkerWork implements Serializable{
    private int workerID;
    private String work;

    public WorkerWork(){

    }

    public WorkerWork(int workerID, String work) {
        this.workerID = workerID;
        this.work = work;
    }

    public int getWorkerID() {
        return workerID;
    }

    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    @Override
    public String toString() {
        return "WorkerWork{" +
                "workerID=" + workerID +
                ", work='" + work + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkerWork)) return false;

        WorkerWork that = (WorkerWork) o;

        if (getWorkerID() != that.getWorkerID()) return false;
        return getWork() != null ? getWork().equals(that.getWork()) : that.getWork() == null;

    }

    @Override
    public int hashCode() {
        int result = getWorkerID();
        result = 31 * result + (getWork() != null ? getWork().hashCode() : 0);
        return result;
    }
}
