package cm.g2i.lalalaworker.controllers.network;

import android.util.Log;
import android.util.Pair;

import cm.g2i.lalalaworker.BuildConfig;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Sim'S on 01/08/2017.
 */

public class WorkerWorksController {
    private static final String ADD = Settings.ADD;
    private static final String URL_WORKERLIST = "http://"+ADD+"/workerworkslist.php";
    private static final String URL_GETWORKS = "http://"+ADD+"/getworks.php";
    private static final String URL_ADDWORKFORWORKER = "http://"+ADD+"/addworkforworker.php";
    private static final String URL_REMOVEWORKONWORKER = "http://"+ADD+"/removeworkonworker.php";

    public static ArrayList<WorkerWork> workerWorksList(int workerID) throws IOException{
        InputStream is = null;
        ArrayList<WorkerWork> workerWorks = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_WORKERLIST+"?worker_id="+workerID);
            connection.connect();
            is = connection.getInputStream();
            workerWorks = Utils.parseToWorkerWorks(LaLaLaWNetwork.readInputStream(is));
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return workerWorks;
    }

    public static ArrayList<String> getWorks() throws IOException{
        InputStream is = null;
        ArrayList<String> works = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_GETWORKS);
            connection.connect();
            is = connection.getInputStream();
            works = Utils.parseToString(LaLaLaWNetwork.readInputStream(is));
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return works;
    }

    public static ArrayList<String> getWorks(int workerID) throws IOException{
        InputStream is = null;
        ArrayList<String> works = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_GETWORKS+"?worker_id="+workerID);
            connection.connect();
            is = connection.getInputStream();
            works = Utils.parseToString(LaLaLaWNetwork.readInputStream(is));
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return works;
    }

    public static String addWorkForWorker(WorkerWork workerWork) throws IOException{
        InputStream is = null;
        String feedback = null;
        HttpURLConnection connection = null;
        try{
            //connection = LaLaLaWNetwork.newConnection(URL_ADDWORKFORWORKER+"?worker_id="+workerWork.getWorkerID()+"&work="+workerWork.getWork());
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_ADDWORKFORWORKER, new Pair<>("worker_id", ""+workerWork.getWorkerID()),
                    new Pair<>("work", workerWork.getWork()));
            connection.connect();
            is = connection.getInputStream();
            feedback = LaLaLaWNetwork.readInputStream(is);
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return feedback;
    }

    public static String addWorksForWorker(ArrayList<String> works, Worker worker) throws IOException{
        String res = null;
        try{
            for (String s: works){
                WorkerWork workerWork = new WorkerWork(worker.getID(), s);
                res = addWorkForWorker(workerWork);
                Boolean.parseBoolean(res);
            }
            return res;
        }
        finally {

        }
    }

    public static String removeWorkOnWorker(WorkerWork workerWork) throws IOException{
        InputStream is = null;
        String feedback = null;
        HttpURLConnection connection = null;
        try{
            //connection = LaLaLaWNetwork.newConnection(URL_REMOVEWORKONWORKER+"?worker_id="+workerWork.getWorkerID()+"&work="+workerWork.getWork());
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_REMOVEWORKONWORKER, new Pair<>("worker_id", ""+workerWork.getWorkerID()),
                    new Pair<>("work", workerWork.getWork()));
            connection.connect();
            is = connection.getInputStream();
            feedback = LaLaLaWNetwork.readInputStream(is);
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return feedback;
    }

    private static class Utils{
        public static ArrayList<WorkerWork> parseToWorkerWorks(String json){
            try {
                ArrayList<WorkerWork> workerWorks = new ArrayList<>();
                JSONArray array = new JSONArray(json);
                for (int i=0; i<array.length(); i++){
                    WorkerWork workerWork = new WorkerWork();
                    workerWork.setWorkerID(array.optJSONObject(i).getInt("_worker"));
                    workerWork.setWork(array.optJSONObject(i).getString("_work"));
                    workerWorks.add(workerWork);
                }
                return workerWorks;
            } catch (Exception e){
                if(BuildConfig.DEBUG) {
                    Log.v("PARSE JSON WKRS", "[JSONException] e : " + e.getMessage());
                    Log.v("PARSE JSON WKRS", json);
                }
            }
            return null;
        }

        public static ArrayList<String> parseToString(String json){
            try{
                ArrayList<String> strings = new ArrayList<>();
                JSONArray array = new JSONArray(json);
                for (int i=0; i<array.length(); i++ ){
                    String s = array.optJSONObject(i).getString("work");
                    strings.add(s);
                }
                return strings;
            } catch (Exception e){
                if(BuildConfig.DEBUG) {
                    Log.v("PARSE JSON WKRS", "[JSONException] e : " + e.getMessage());
                    Log.v("PARSE JSON WKRS", json);
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

