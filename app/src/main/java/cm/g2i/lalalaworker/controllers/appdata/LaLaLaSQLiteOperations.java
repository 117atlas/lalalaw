package cm.g2i.lalalaworker.controllers.appdata;

import android.content.Context;
import android.util.Log;

import cm.g2i.lalalaworker.controllers.settings.SettingUnit;
import cm.g2i.lalalaworker.models.Comment;
import cm.g2i.lalalaworker.models.History;
import cm.g2i.lalalaworker.models.HistoryUnit;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.models.Strikes;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;

import java.util.ArrayList;

/**
 * Created by Sim'S on 29/07/2017.
 */

public class LaLaLaSQLiteOperations {

    private static LaLaLaSQLiteDBB dbb;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long insertUser(User user, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            long res = dbb.insertUser(user);
            return res;
        } finally {
            dbb.close();
        }
    }

    public static int updateUser(User user, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.updateUser(user);
            return res;
        } finally {
            dbb.close();
        }
    }

    public static User getUser(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            User user = dbb.getUser();
            return user;
        } finally {
            dbb.close();
        }
    }

    public static int deleteAllUsers(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.deleteUsers();
            return res;
        } finally {
            dbb.close();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long insertWorker(Worker worker, boolean hasAccountInDevice, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            long res = 0;
            Worker w = dbb.getWorkerById(worker.getID());
            if (w == null){
                res = dbb.insertWorker(worker, hasAccountInDevice);
            }
            else{
                Log.i("INSERT WRKS UPDATE", "Update");
                res = dbb.updateWorker(worker, hasAccountInDevice);
            }
            return res;
        } finally {
            dbb.close();
        }
    }

    public static int updateWorker(Worker worker, boolean hasAccountInDevice, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.updateWorker(worker, hasAccountInDevice);
            return res;
        } finally {
            dbb.close();
        }
    }

    public static Worker getWorkerByID(int workerID, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            Worker worker = dbb.getWorkerById(workerID);
            return worker;
        } finally {
            dbb.close();
        }
    }

    public static int removeOthersWorkers(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.removeOtherWorkers();
            return res;
        } finally {
            dbb.close();
        }
    }

    public static Worker[] getOthersWorkers(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            Worker[] workers = dbb.othersWorkers();
            return workers;
        } finally {
            dbb.close();
        }
    }

    public static Worker getWorker(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            Worker worker = dbb.getWorker();
            return worker;
        } finally {
            dbb.close();
        }
    }

    public static int removeWorker(Context context){
        dbb = new LaLaLaSQLiteDBB(context);
        dbb.open();
        int res = dbb.removeWorker();
        dbb.close();
        return res;
    }

    public static int removeWorker(int workerID, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.removeWorker(workerID);
            return res;
        }finally {
            dbb.close();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void insertWorkerWorks(ArrayList<WorkerWork> workerWorks, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            dbb.insertWorkerWorks(workerWorks);
        } finally {
            dbb.close();
        }
    }

    public static int deleteWorkerWorks(int workerID, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.deleteWorkerWork(workerID);
            return res;
        } finally {
            dbb.close();
        }

    }

    public static void updateWorkerWorks(ArrayList<WorkerWork> workerWorks, int workerID, Context context){
        try{
            deleteWorkerWorks(workerID, context);
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            dbb.updateWorkerWorks(workerWorks);
        } finally {
            dbb.close();
        }
    }

    public static ArrayList<WorkerWork> getWorkerWorks(int workerID, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            ArrayList<WorkerWork> workerWorks = dbb.getWorkerWorks(workerID);
            return workerWorks;
        } finally {
            dbb.close();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void insertComments(ArrayList<Comment> comments, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            if (comments!=null){
                for (Comment comment: comments){
                    dbb.insertComment(comment);
                }
            }
        } finally {
            dbb.close();
        }
    }

    public static int deleteComments(int workerID, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.deleteComments(workerID);
            return res;
        } finally {
            dbb.close();
        }
    }

    public static void updateComments(ArrayList<Comment> comments, int workerID, Context context){
        deleteComments(workerID, context);
        insertComments(comments, context);
    }

    public static ArrayList<Comment> getComments(int workerID, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            ArrayList<Comment> comments = dbb.getComments(workerID);
            return comments;
        } finally {
            dbb.close();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void insertWorkerInfos(Worker worker, ArrayList<WorkerWork> workerWorks, ArrayList<Comment> comments, Context context){
        insertWorker(worker, false, context);
        insertWorkerWorks(workerWorks, context);
        insertComments(comments, context);
    }

    public static void updateWorkerInfos(Worker worker, ArrayList<WorkerWork> workerWorks, ArrayList<Comment> comments, Context context){
        insertWorker(worker, false, context);
        deleteWorkerWorks(worker.getID(), context);
        insertWorkerWorks(workerWorks, context);
        deleteComments(worker.getID(), context);
        insertComments(comments, context);
    }

    public static void deleteWorkerInfos(int workerID, Context context){
        removeWorker(workerID, context);
        deleteWorkerWorks(workerID, context);
        deleteComments(workerID, context);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long insertHistory(HistoryUnit unit, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            long res = dbb.insertHistory(unit);
            return res;
        } finally {
            dbb.close();
        }
    }

    public static HistoryUnit[] getHistories(Context context, Worker worker){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            HistoryUnit[] res = dbb.getHistoriesForWorker(worker.getID());
            return res;
        } finally {
            dbb.close();
        }
    }

    public static ArrayList<History> historiesFullList(Context context){
        Worker[] workers = getOthersWorkers(context);
        if (workers!=null){
            ArrayList<History> res = new ArrayList<>();
            for (int i=0; i<workers.length; i++){
                HistoryUnit[] units = getHistories(context, workers[i]);
                res.add(new History(units));
            }
            return res;
        }
        return null;
    }

    public static int removeHistoriesForWorker(int workerID, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.removeHistoryForWorker(workerID);
            return res;
        } finally {
            dbb.close();
        }
    }

    public static void fulldeleteHistory(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            dbb.removeAllHistory();
        } finally {
            dbb.close();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long updateSettingUnit(SettingUnit settingUnit, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            long res = dbb.updateSettingUnit(settingUnit);
            return res;
        } finally {
            dbb.close();
        }
    }

    public static long initializeSettingUnit(SettingUnit settingUnit, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            long res = dbb.initializeSettingUnit(settingUnit);
            return res;
        } finally {
            dbb.close();
        }
    }

    public static SettingUnit getSettingUnitByName(String name, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            SettingUnit settingUnit = dbb.getSettingUnitByName(name);
            return settingUnit;
        } finally {
            dbb.close();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void insertStrikes(ArrayList<Strikes> strikes, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            dbb.insertStrikes(strikes);
        } finally {
            dbb.close();
        }
    }

    public static int deleteStrikes(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.deleteStrikes();
            return res;
        } finally {
            dbb.close();
        }
    }

    public static ArrayList<Strikes> getStrikes(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            ArrayList<Strikes> strikes = dbb.getStrikes();
            return strikes;
        } finally {
            dbb.close();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void insertNews(ArrayList<News> newses, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            dbb.insertNews(newses);
        } finally {
            dbb.close();
        }
    }

    public static int deleteNews(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            int res = dbb.deleteNews();
            return res;
        } finally {
            dbb.close();
        }
    }

    public static void deleteNews(ArrayList<News> newses, Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            dbb.deleteNews(newses);
        } finally {
            dbb.close();
        }
    }

    public static void updateNews(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            dbb.updateNews();
        } finally {
            dbb.close();
        }
    }

    public static ArrayList<News> getNews(Context context){
        try{
            dbb = new LaLaLaSQLiteDBB(context);
            dbb.open();
            ArrayList<News> newses = dbb.getNews();
            return newses;
        } finally {
            dbb.close();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

}
