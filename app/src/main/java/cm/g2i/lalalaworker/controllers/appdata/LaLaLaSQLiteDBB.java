package cm.g2i.lalalaworker.controllers.appdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cm.g2i.lalalaworker.controllers.settings.SettingUnit;
import cm.g2i.lalalaworker.models.Comment;
import cm.g2i.lalalaworker.models.HistoryUnit;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.models.Strikes;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;

import java.util.ArrayList;

/**
 * Created by Sim'S on 28/07/2017.
 */

public class LaLaLaSQLiteDBB {
    /**
     * TABLES AND COLUMN NAMES
     */
    private static String USER_DB_NAME = "users";
    private static String WORKER_DB_NAME = "workers";
    private static String HISTORY_DB_NAME = "history";
    private static String WORKS_WORKERS_DB_NAME = "works_workers";
    private static String COMMENTS_DB_NAME = "comments";
    private static String SETTINGS_DB_NAME = "settings";
    private static String STRIKES_DB_NAME = "strikes";
    private static String NEWS_DB_NAME = "news";


    private static String USER_DB__ID = "id";
    private static String USER_DB__NAME = "name";

    private static String WORKER_DB__ID = "id";
    private static String WORKER_DB__NAME = "name";
    private static String WORKER_DB__NATIONALITY = "nationality";
    private static String WORKER_DB__PHOTO = "photo";
    private static String WORKER_DB__PHONENUMBER = "phonenumber";
    private static String WORKER_DB__STREET = "street";
    private static String WORKER_DB__TOWN = "town";
    private static String WORKER_DB__PASSWD = "password";
    private static String WORKER_DB__NOTE = "note";
    private static String WORKER_DB__NBR_SOLLICITATIONS = "nbrsollicitations";
    private static String WORKER_DB__HAS_ACCOUNT = "hasaccountindevice";
    private static String[] WORKER_COLUMNS = {WORKER_DB__ID, WORKER_DB__NAME, WORKER_DB__NATIONALITY, WORKER_DB__PHOTO,
        WORKER_DB__PHONENUMBER, WORKER_DB__STREET, WORKER_DB__TOWN, WORKER_DB__PASSWD, WORKER_DB__NOTE, WORKER_DB__NBR_SOLLICITATIONS, WORKER_DB__HAS_ACCOUNT};

    private static String HISTORY_DB__WORKER_ID = "workerid";
    private static String HISTORY_DB__TYPE = "type";
    private static String HISTORY_DB__DATE = "date";

    private static String WORKS_WORKERS_DB__WORK = "_work";
    private static String WORKS_WORKERS_DB__WORKER = "_worker";

    private static String COMMENTS_DB__ID = "ComID";
    private static String COMMENTS_DB__WORKER = "_worker";
    private static String COMMENTS_DB__USER = "_user";
    private static String COMMENTS_DB__DATE = "_date";
    private static String COMMENTS_DB__COMMENT = "_comment";
    private static String[] COMMENTS_COLUMNS = {COMMENTS_DB__ID, COMMENTS_DB__WORKER, COMMENTS_DB__USER, COMMENTS_DB__DATE, COMMENTS_DB__COMMENT};

    private static String SETTINGS_DB__SETTING_NAME = "name";
    private static String SETTINGS_DB__SETTING_VALUE = "value";
    private static String[] SETTINGS_COLUMNS = {SETTINGS_DB__SETTING_NAME, SETTINGS_DB__SETTING_VALUE};

    private static String STRIKES_DB__ID = "ID";
    private static String STRIKES_DB__TYPE = "type";
    private static String STRIKES_DB__MESSAGE = "message";
    private static String STRIKES_DB__GRAVITY = "gravity";
    private static String[] STRIKES_COLUMNS = {STRIKES_DB__ID, STRIKES_DB__TYPE, STRIKES_DB__MESSAGE, STRIKES_DB__GRAVITY};

    private static String NEWS_DB__TYPE = "type";
    private static String NEWS_DB__MESSAGE = "message";
    private static String NEWS_DB__ISNEW = "isnew";
    private static String[] NEWS_COLUMNS = {NEWS_DB__TYPE, NEWS_DB__MESSAGE, NEWS_DB__ISNEW};
    /*

     */

    private SQLiteDatabase database;
    private SQLiteDatabaseHandler handler;

    public LaLaLaSQLiteDBB(Context context){
        handler = new SQLiteDatabaseHandler(context);
    }

    public void open(){
        database = handler.getWritableDatabase();
    }

    public void close(){
        database.close();
    }

    public SQLiteDatabase getDatabase(){
        return database;
    }

    /**
     * USER
     * @param user
     * @return
     */
    public long insertUser(User user){
        ContentValues values = new ContentValues();
        values.put(USER_DB__ID, user.getID());
        values.put(USER_DB__NAME, user.getName());
        return database.insert(USER_DB_NAME, null, values);
    }

    public int updateUser(User user){
        ContentValues values = new ContentValues();
        values.put(USER_DB__ID, user.getID());
        values.put(USER_DB__NAME, user.getName());
        return database.update(USER_DB_NAME, values, USER_DB__ID + " = "+user.getID(), null);
    }

    public int deleteUser(User user){
        return database.delete(USER_DB_NAME, USER_DB__ID + " = "+user.getID(), null);
    }

    public int deleteUsers(){
        return database.delete(USER_DB_NAME, null, null);
    }

    public User getUser(){
        Cursor cursor = database.query(USER_DB_NAME, new String[]{USER_DB__ID, USER_DB__NAME}, null, null, null, null, null, null);
        return cursorToUser(cursor);
    }

    public User getUserByName(String name){
        Cursor cursor = database.query(USER_DB_NAME, new String[]{USER_DB__ID, USER_DB__NAME}, USER_DB__NAME + " LIKE \"" + name + "\"",
                null, null, null, null, null);
        return cursorToUser(cursor);
    }

    public User cursorToUser(Cursor cursor){
        if(cursor.getCount()==0) return null;
        cursor.moveToFirst();
        User user = new User();
        user.setID(cursor.getInt(0));
        user.setName(cursor.getString(1));
        cursor.close();
        return user;
    }
    /*********************************************************************************************/



    /*************************************************************************************************/
    //Worker
    public long insertWorker(Worker worker, boolean hasAccountInDevice){
        ContentValues values = new ContentValues();
        values.put(WORKER_DB__ID, worker.getID());
        values.put(WORKER_DB__NAME, worker.getName());
        values.put(WORKER_DB__NATIONALITY, worker.getNationality());
        values.put(WORKER_DB__PHOTO, worker.getlocalPhoto());
        values.put(WORKER_DB__PHONENUMBER, worker.getPhoneNumber());
        values.put(WORKER_DB__TOWN, worker.getVille());
        values.put(WORKER_DB__STREET, worker.getQuartier());
        values.put(WORKER_DB__NOTE, worker.getNote());
        values.put(WORKER_DB__NBR_SOLLICITATIONS, worker.getNbr_sollicitations());
        values.put(WORKER_DB__HAS_ACCOUNT, hasAccountInDevice?1:0);
        values.put(WORKER_DB__PASSWD, hasAccountInDevice?worker.getPasswd():"lalalaw");
        return database.insert(WORKER_DB_NAME, null, values);
    }

    public int updateWorker(Worker worker, boolean hasAccountInDevice){
        ContentValues values = new ContentValues();
        values.put(WORKER_DB__ID, worker.getID());
        values.put(WORKER_DB__NAME, worker.getName());
        values.put(WORKER_DB__NATIONALITY, worker.getNationality());
        values.put(WORKER_DB__PHOTO, worker.getlocalPhoto());
        values.put(WORKER_DB__PHONENUMBER, worker.getPhoneNumber());
        values.put(WORKER_DB__TOWN, worker.getVille());
        values.put(WORKER_DB__STREET, worker.getQuartier());
        values.put(WORKER_DB__NOTE, worker.getNote());
        values.put(WORKER_DB__NBR_SOLLICITATIONS, worker.getNbr_sollicitations());
        values.put(WORKER_DB__HAS_ACCOUNT, hasAccountInDevice?1:0);
        values.put(WORKER_DB__PASSWD, hasAccountInDevice?worker.getPasswd():"lalalaw");
        return  database.update(WORKER_DB_NAME, values, WORKER_DB__ID + " = "+worker.getID(), null);
    }

    public int removeWorker(){
        return database.delete(WORKER_DB_NAME, WORKER_DB__HAS_ACCOUNT + " = 1", null);
    }

    public int removeWorker(int workerID){
        return database.delete(WORKER_DB_NAME, WORKER_DB__ID + " = "+workerID, null);
    }

    public int removeOtherWorkers(){
        return database.delete(WORKER_DB_NAME, WORKER_DB__HAS_ACCOUNT + " = 0", null);
    }

    public Worker[] getWorkers(){
        Cursor cursor = database.query(WORKER_DB_NAME, WORKER_COLUMNS, null, null, null, null, null);
        return cursorToWorkers(cursor);
    }

    public Worker[] othersWorkers(){
        Cursor cursor = database.query(WORKER_DB_NAME, WORKER_COLUMNS, WORKER_DB__HAS_ACCOUNT + " = 0", null, null, null, null);
        return cursorToWorkers(cursor);
    }

    public Worker getWorker(){
        Cursor cursor = database.query(WORKER_DB_NAME, WORKER_COLUMNS, WORKER_DB__HAS_ACCOUNT + " = 1", null, null, null, null);
        Worker[] workers = cursorToWorkers(cursor);
        return workers==null?null:workers[0];
    }

    public Worker getWorkerById(int workerID){
        Cursor cursor = database.query(WORKER_DB_NAME, WORKER_COLUMNS, WORKER_DB__ID + " = " + workerID, null, null, null, null);
        Worker[] workers = cursorToWorkers(cursor);
        return workers==null?null:workers[0];
    }

    public Worker[] cursorToWorkers(Cursor c){
        if (c.getCount()==0) return null;
        Worker[] result = new Worker[c.getCount()];
        c.moveToFirst();
        for (int i=0; i<c.getCount(); i++){
            Worker worker = new Worker();
            worker.setID(c.getInt(0));
            worker.setName(c.getString(1));
            worker.setNationality(c.getString(2));
            worker.setLocalPhoto(c.getString(3));
            worker.setPhoneNumber(c.getString(4));
            worker.setQuartier(c.getString(5));
            worker.setVille(c.getString(6));
            worker.setPasswd(c.getString(7));
            worker.setNote(c.getDouble(8));
            worker.setNbr_sollicitations(c.getInt(9));
            result[i] = worker;
            c.moveToNext();
        }
        c.close();
        return result;
    }
    /*******************************************************************************************************************/


    /********************************************************************************************************************/
    //WorkerWorks
    public long insertWorkerWork(WorkerWork workerWork){
        ContentValues values = new ContentValues();
        values.put(WORKS_WORKERS_DB__WORK, workerWork.getWork());
        values.put(WORKS_WORKERS_DB__WORKER, workerWork.getWorkerID());
        return database.insert(WORKS_WORKERS_DB_NAME, null, values);
    }

    public int deleteWorkerWork(int workerID){
        return database.delete(WORKS_WORKERS_DB_NAME, WORKS_WORKERS_DB__WORKER + " = " + workerID, null);
    }

    public void insertWorkerWorks(ArrayList<WorkerWork> workerWorks){
        for (WorkerWork workerWork: workerWorks){
            insertWorkerWork(workerWork);
        }
    }

    public void updateWorkerWorks(ArrayList<WorkerWork> workerWorks){
        for (WorkerWork workerWork: workerWorks){
            Cursor cursor = database.query(WORKS_WORKERS_DB_NAME, new String[]{WORKS_WORKERS_DB__WORK, WORKS_WORKERS_DB__WORKER},
                    WORKS_WORKERS_DB__WORK + " = '" + workerWork.getWork() + "' AND " + WORKS_WORKERS_DB__WORKER + " = " + workerWork.getWorkerID(), null,
                    null, null, null);
            if (cursor.getCount()==0) insertWorkerWork(workerWork);
        }
    }

    public ArrayList<WorkerWork> getWorkerWorks(int workerID){
        Cursor cursor = database.query(WORKS_WORKERS_DB_NAME, new String[]{WORKS_WORKERS_DB__WORK, WORKS_WORKERS_DB__WORKER},
                WORKS_WORKERS_DB__WORKER + " = " + workerID, null, null, null, null);
        return cursorToWorkerWorks(cursor);
    }

    public ArrayList<WorkerWork> cursorToWorkerWorks(Cursor cursor){
        if (cursor.getCount()==0) return null;
        ArrayList<WorkerWork> workerWorks = new ArrayList<>();
        for (int i = 0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            WorkerWork workerWork = new WorkerWork();
            workerWork.setWork(cursor.getString(0));
            workerWork.setWorkerID(cursor.getInt(1));
            workerWorks.add(workerWork);
        }
        return workerWorks;
    }
    /************************************************************************************************/


    /****************************************************************************************************/
    //Comments
    public long insertComment(Comment comment){
        ContentValues values = new ContentValues();
        values.put(COMMENTS_DB__ID, comment.getID());
        values.put(COMMENTS_DB__WORKER, comment.getWorker().getID());
        values.put(COMMENTS_DB__USER, comment.getUser().getName());
        values.put(COMMENTS_DB__DATE, comment.getDate());
        values.put(COMMENTS_DB__COMMENT, comment.getComment());
        return database.insert(COMMENTS_DB_NAME, null, values);
    }

    public int deleteComments(int workerID){
        return database.delete(COMMENTS_DB_NAME, COMMENTS_DB__WORKER + " = " + workerID, null);
    }

    public int updateComment(Comment comment){
        ContentValues values = new ContentValues();
        values.put(COMMENTS_DB__ID, comment.getID());
        values.put(COMMENTS_DB__WORKER, comment.getWorker().getID());
        values.put(COMMENTS_DB__USER, comment.getUser().getName());
        values.put(COMMENTS_DB__DATE, comment.getDate());
        values.put(COMMENTS_DB__COMMENT, comment.getComment());
        return database.update(COMMENTS_DB_NAME, values, COMMENTS_DB__WORKER + " = " + comment.getWorker().getID(), null);
    }

    public ArrayList<Comment> getComments(int workerID){
        Cursor cursor = database.query(COMMENTS_DB_NAME, COMMENTS_COLUMNS, COMMENTS_DB__WORKER + " = " + workerID, null, null, null, null);
        return cursorToComments(cursor);
    }

    public ArrayList<Comment> cursorToComments(Cursor cursor){
        if (cursor.getCount() == 0) return null;
        ArrayList<Comment> comments = new ArrayList<>();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            Comment comment = new Comment();
            comment.setID(cursor.getInt(0));
            comment.setWorker(getWorkerById(cursor.getInt(1)));
            comment.setUser(new User(0, cursor.getString(2), null));
            comment.setDate(cursor.getString(3));
            comment.setComment(cursor.getString(4));
            comments.add(comment);
        }
        return comments;
    }
    /********************************************************************************************************/


    /*********************************************************************************************************/
    //History Unit
    public long insertHistory(HistoryUnit historyUnit){
        ContentValues values = new ContentValues();
        values.put(HISTORY_DB__WORKER_ID, historyUnit.getWorker().getID());
        values.put(HISTORY_DB__DATE, historyUnit.getDate());
        values.put(HISTORY_DB__TYPE, historyUnit.getType());
        return database.insert(HISTORY_DB_NAME, null, values);
    }

    public int updateHistory(HistoryUnit historyUnit){
        ContentValues values = new ContentValues();
        values.put(HISTORY_DB__WORKER_ID, historyUnit.getWorker().getID());
        values.put(HISTORY_DB__DATE, historyUnit.getDate());
        values.put(HISTORY_DB__TYPE, historyUnit.getType());
        return database.update(HISTORY_DB_NAME, values, HISTORY_DB__WORKER_ID + " = " + historyUnit.getWorker().getID(), null);
    }

    public int removeHistoryForWorker(int workerID){
        return database.delete(HISTORY_DB_NAME, HISTORY_DB__WORKER_ID + " = " + workerID, null);
    }

    public int fullDeleteHistory(){
        return database.delete(HISTORY_DB_NAME, null, null);
    }

    public int removeAllHistory(){
        return database.delete(HISTORY_DB_NAME, null, null);
    }

    public HistoryUnit[] getHistoriesForWorker(int workerID){
        Cursor cursor = database.query(HISTORY_DB_NAME, new String[]{HISTORY_DB__WORKER_ID, HISTORY_DB__TYPE, HISTORY_DB__DATE},
                HISTORY_DB__WORKER_ID + " = " + workerID, null, null, null, null, null);
        return cursorToHistories(cursor, workerID);
    }

    public HistoryUnit[] cursorToHistories(Cursor cursor, int workerID){
        if (cursor.getCount()==0) return null;
        HistoryUnit[] historyUnits = new HistoryUnit[cursor.getCount()];
        Worker worker = getWorkerById(workerID);
        User user = getUser();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            HistoryUnit historyUnit = new HistoryUnit();
            historyUnit.setUser(user);
            historyUnit.setWorker(worker);
            historyUnit.setType(cursor.getInt(1));
            historyUnit.setDate(cursor.getString(2));
            historyUnits[i] = historyUnit;
        }
        return historyUnits;
    }
    /**********************************************************************************************/


    /**********************************************************************************************/
    //Settings
    public long insertSettingUnit(SettingUnit settingUnit){
        ContentValues values = new ContentValues();
        values.put(SETTINGS_DB__SETTING_NAME, settingUnit.getName());
        values.put(SETTINGS_DB__SETTING_VALUE, settingUnit.getValue());
        return database.insert(SETTINGS_DB_NAME, null, values);
    }

    public long updateSettingUnit(SettingUnit settingUnit){
        if (getSettingUnitByName(settingUnit.getName())==null){
            return insertSettingUnit(settingUnit);
        }
        else{
            ContentValues values = new ContentValues();
            values.put(SETTINGS_DB__SETTING_NAME, settingUnit.getName());
            values.put(SETTINGS_DB__SETTING_VALUE, settingUnit.getValue());
            return database.update(SETTINGS_DB_NAME, values, SETTINGS_DB__SETTING_NAME + " = '" + settingUnit.getName() + "'", null);
        }
    }

    public long initializeSettingUnit(SettingUnit settingUnit){
        if (getSettingUnitByName(settingUnit.getName())==null){
            return insertSettingUnit(settingUnit);
        }
        return 0;
    }

    public SettingUnit getSettingUnitByName(String name){
        Cursor cursor = database.query(SETTINGS_DB_NAME, SETTINGS_COLUMNS, SETTINGS_DB__SETTING_NAME + " = '" + name + "'", null, null, null, null);
        return cursorToSettingUnit(cursor);
    }

    public SettingUnit cursorToSettingUnit(Cursor cursor){
        if (cursor.getCount()==0) return null;
        if (cursor.moveToFirst()){
            return new SettingUnit(cursor.getString(0), cursor.getString(1));
        }
        return null;
    }

    /**********************************************************************************************/


    /******************************************************************************************************/
    //STRIKES

    public long insertStrike(Strikes strikes){
        ContentValues values = new ContentValues();
        values.put(STRIKES_DB__ID, strikes.getID());
        values.put(STRIKES_DB__TYPE, strikes.getType());
        values.put(STRIKES_DB__MESSAGE, strikes.getMessage());
        values.put(STRIKES_DB__GRAVITY, strikes.getGravity());
        return database.insert(STRIKES_DB_NAME, null, values);
    }

    public void insertStrikes(ArrayList<Strikes> strikes){
        if (strikes!=null){
            for (Strikes strikes1: strikes){
                if (getStrikeById(strikes1.getID())==null) insertStrike(strikes1);
            }
        }
    }

    public int deleteStrikes(){
        return database.delete(STRIKES_DB_NAME, null, null);
    }

    public ArrayList<Strikes> getStrikes(){
        Cursor cursor = database.query(STRIKES_DB_NAME, STRIKES_COLUMNS, null, null, null, null, null);
        return cursorToStrikes(cursor);
    }

    public Strikes getStrikeById(int StrId){
        Cursor cursor = database.query(STRIKES_DB_NAME, STRIKES_COLUMNS, STRIKES_DB__ID + " = " + StrId, null, null, null, null);
        ArrayList<Strikes> res = cursorToStrikes(cursor);
        return (res==null||res.size()==0)?null:res.get(0);
    }

    public ArrayList<Strikes> cursorToStrikes(Cursor cursor){
        if (cursor.getCount()==0) return null;
        ArrayList<Strikes> strikes = new ArrayList<>();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            Worker worker = getWorker();
            Strikes s = new Strikes();
            s.setID(cursor.getInt(0));
            s.setType(cursor.getInt(1));
            s.setMessage(cursor.getString(2));
            s.setGravity(cursor.getInt(3));
            s.setWorkerID(worker.getID());
            strikes.add(s);
        }
        cursor.close();
        return strikes;
    }
    /******************************************************************************************************/



    /******************************************************************************************************/
    //NEWS
    public long insertNew(News news){
        ContentValues values = new ContentValues();
        values.put(NEWS_DB__TYPE, news.getType());
        values.put(NEWS_DB__MESSAGE, news.getMessage());
        values.put(NEWS_DB__ISNEW, news.isNew()?1:0);
        return database.insert(NEWS_DB_NAME, null, values);
    }

    public void insertNews(ArrayList<News> newses){
        if (newses!=null){
            for (News news: newses){
                insertNew(news);
            }
        }
    }

    public int deleteNews(){
        return database.delete(NEWS_DB_NAME, null, null);
    }

    public int deleteNew(News news){
        return database.delete(NEWS_DB_NAME, NEWS_DB__MESSAGE + " = '" + news.getMessage() + "'", null);
    }

    public void deleteNews(ArrayList<News> newses){
        if (newses!=null){
            for (News news: newses){
                deleteNew(news);
            }
        }
    }

    public int updateNews(){
        ContentValues values = new ContentValues();
        values.put(NEWS_DB__ISNEW, 0);
        return database.update(NEWS_DB_NAME, values, NEWS_DB__ISNEW + " = 1", null);
    }

    public ArrayList<News> getNews(){
        Cursor cursor = database.query(NEWS_DB_NAME, NEWS_COLUMNS, null, null, null, null, null);
        return cursorToNews(cursor);
    }

    public ArrayList<News> cursorToNews(Cursor cursor){
        if (cursor.getCount()==0) return null;
        ArrayList<News> newses = new ArrayList<>();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            Worker worker = getWorker();
            News news = new News();
            news.setType(cursor.getInt(0));
            news.setMessage(cursor.getString(1));
            news.setNew(cursor.getInt(2)==1?true:false);
            news.setWorkerID(worker.getID());
            newses.add(news);
        }
        cursor.close();
        return newses;
    }
    /******************************************************************************************************/
}
