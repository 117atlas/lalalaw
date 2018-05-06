package cm.g2i.lalalaworker.controllers.appdata;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sim'S on 28/07/2017.
 */

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static int DB_VERSION = 9;

    private static String DB_NAME = "lalalaworkers_intdb";

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
    private static String WORKER_DB__PHONENUMBER = "phonenumber";
    private static String WORKER_DB__PHOTO = "photo";
    private static String WORKER_DB__STREET = "street";
    private static String WORKER_DB__TOWN = "town";
    private static String WORKER_DB__PASSWD = "password";
    private static String WORKER_DB__NOTE = "note";
    private static String WORKER_DB__NBR_SOLLICITATIONS = "nbrsollicitations";
    private static String WORKER_DB__HAS_ACCOUNT = "hasaccountindevice";

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

    private static String SETTINGS_DB__SETTING_NAME = "name";
    private static String SETTINGS_DB__SETTING_VALUE = "value";

    private static String STRIKES_DB__ID = "ID";
    private static String STRIKES_DB__TYPE = "type";
    private static String STRIKES_DB__MESSAGE = "message";
    private static String STRIKES_DB__GRAVITY = "gravity";

    private static String NEWS_DB__TYPE = "type";
    private static String NEWS_DB__MESSAGE = "message";
    private static String NEWS_DB__ISNEW = "isnew";

    public SQLiteDatabaseHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public SQLiteDatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteDatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS "+USER_DB_NAME+" (" +
                USER_DB__ID + " INT PRIMARY KEY, " +
                USER_DB__NAME + " TEXT" +
                ")";
        final String CREATE_WORKER_TABLE = "CREATE TABLE IF NOT EXISTS " + WORKER_DB_NAME + " (" +
                WORKER_DB__ID + " INT PRIMARY KEY, " +
                WORKER_DB__NAME + " TEXT, " +
                WORKER_DB__NATIONALITY + " TEXT, " +
                WORKER_DB__PHONENUMBER + " TEXT, " +
                WORKER_DB__PHOTO + " TEXT, " +
                WORKER_DB__STREET + " TEXT, " +
                WORKER_DB__TOWN + " TEXT, " +
                WORKER_DB__PASSWD + " TEXT, " +
                WORKER_DB__NOTE + " REAL, " +
                WORKER_DB__NBR_SOLLICITATIONS + " INT, " +
                WORKER_DB__HAS_ACCOUNT + " INT" +
                ")";
        final String CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + HISTORY_DB_NAME + " (" +
                HISTORY_DB__WORKER_ID + " INT, " +
                HISTORY_DB__TYPE + " INT, " +
                HISTORY_DB__DATE + " TEXT" +
                ")";
        final String CREATE_WORKS_WORKERS_TABLE = "CREATE TABLE IF NOT EXISTS " + WORKS_WORKERS_DB_NAME + " (" +
                WORKS_WORKERS_DB__WORK + " TEXT, " +
                WORKS_WORKERS_DB__WORKER + " INT" +
                ")";
        final String CREATE_COMMENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + COMMENTS_DB_NAME + " (" +
                COMMENTS_DB__ID + " INT PRIMARY KEY, " +
                COMMENTS_DB__WORKER + " INT, " +
                COMMENTS_DB__USER + " TEXT, " +
                COMMENTS_DB__DATE + " TEXT, " +
                COMMENTS_DB__COMMENT + " TEXT" +
                ")";
        final String CREATE_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + SETTINGS_DB_NAME + " (" +
                SETTINGS_DB__SETTING_NAME + " TEXT PRIMARY KEY, " +
                SETTINGS_DB__SETTING_VALUE + " TEXT" +
                ")";
        final String CREATE_STRIKE_TABLE = "CREATE TABLE IF NOT EXISTS " + STRIKES_DB_NAME + " (" +
                STRIKES_DB__ID + " INT PRIMARY KEY, " +
                STRIKES_DB__TYPE + " INT, " +
                STRIKES_DB__MESSAGE + " TEXT, " +
                STRIKES_DB__GRAVITY + " INT" +
                ")";
        final String CREATE_NEWS_TABLE = "CREATE TABLE IF NOT EXISTS " + NEWS_DB_NAME + " (" +
                NEWS_DB__TYPE + " INT, " +
                NEWS_DB__MESSAGE + " TEXT, " +
                NEWS_DB__ISNEW + " INT" +
                ")";
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_WORKER_TABLE);
        sqLiteDatabase.execSQL(CREATE_HISTORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_WORKS_WORKERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMMENTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_SETTINGS_TABLE);
        sqLiteDatabase.execSQL(CREATE_STRIKE_TABLE);
        sqLiteDatabase.execSQL(CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+USER_DB_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+WORKER_DB_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+HISTORY_DB_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+WORKS_WORKERS_DB_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+COMMENTS_DB_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+SETTINGS_DB_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+NEWS_DB_NAME);
        onCreate(sqLiteDatabase);
        System.out.println("UPGRADE");
    }
}
