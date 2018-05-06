package cm.g2i.lalalaworker.controllers.network;

import android.accounts.AccountManager;
import android.util.Log;
import android.util.Pair;

import cm.g2i.lalalaworker.BuildConfig;
import cm.g2i.lalalaworker.controllers.services.FilesUtils;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.Strikes;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.fragment.AccountFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sim'S on 01/08/2017.
 */

public class WorkerController {
    private static final String ADD = Settings.ADD;
    private static final String URL_LISTE = "http://"+ADD+"/workerlist.php";
    private static final String URL_GETWORKER = "http://"+ADD+"/getworker.php";
    private static final String URL_NATIONS_TOWNS_STREETS = "http://"+ADD+"/nations_towns_streets.php";
    private static final String URL_ADDWORKER = "http://"+ADD+"/addworker.php";
    private static final String URL_EDITWORKER = "http://"+ADD+"/editworker.php";
    private static final String URL_NOTEWORKER = "http://"+ADD+"/noteworker.php";
    private static final String URL_INCSOLLIS = "http://"+ADD+"/incrementsollic.php";
    private static final String URL_STRIKEWORKER = "http://"+ADD+"/strikeworker.php";
    private static final String URL_SETWORKERPROFILEIMG = "http://"+ADD+"/setworkerprofileimg.php";
    private static final String URL_GETWORKERPROFILEIMG = "http://"+ADD+"/getworkerprofileimg.php";
    private static final String URL_UPLOADWORKERPROFILEIMG = "http://"+ADD+"/uploadworkerprofileimg.php";
    private static final String URL_GETSTRIKESFORWORKER = "http://"+ADD+"/getstrikesforworker.php";
    private static final String URL_GETNEWSESFORWORKER = "http://"+ADD+"/getnewsesforworker.php";
    private static final String URL_COMMENTNOTESIGNAL = "http://"+ADD+"/commentnotesignal.php";
    private static final String URL_GETWKRSACCSETTINGS = "http://"+ADD+"/getworkeraccountsettings.php";
    private static final String URL_ASKFOR = "http://"+ADD+"/reactivateorextendacc.php";

    public static ArrayList<Worker> workersList(Pair<String, String>... params) throws IOException{
        InputStream is = null;
        ArrayList<Worker> workers = null;
        HttpURLConnection connection = null;
        try{
            if (params==null || params.length == 0) connection = LaLaLaWNetwork.newConnection(URL_LISTE);
            else connection = LaLaLaWNetwork.newConnectionWithParams(URL_LISTE, params);
            connection.connect();
            is = connection.getInputStream();
            workers = Utils.parseToWorkersList(LaLaLaWNetwork.readInputStream(is));
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return workers;
    }

    public static Worker getWorkerByID(int workerID) throws IOException{
        InputStream is = null;
        Worker worker = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_GETWORKER+"?worker_id="+workerID);
            connection.connect();
            is = connection.getInputStream();
            worker = Utils.parseToWorkersList(LaLaLaWNetwork.readInputStream(is)).get(0);
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return worker;
    }

    public static ArrayList<Pair<Worker, ArrayList<WorkerWork>>> workersGrandList(Pair<String, String>... params) throws IOException{
        ArrayList<Worker> workers = workersList(params);
        if (workers!=null){
            ArrayList<Pair<Worker, ArrayList<WorkerWork>>> result = new ArrayList<>();
            for (Worker worker: workers){
                ArrayList<WorkerWork> workerWorks = WorkerWorksController.workerWorksList(worker.getID());
                Pair<Worker, ArrayList<WorkerWork>> pair = new Pair<>(worker, workerWorks);
                result.add(pair);
            }
            return result;
        }
        return null;
    }

    public static ArrayList<String> getArrayAdaptersListForAutoCompleteTextView(String whatAutoCompleteTextView) throws IOException{
        InputStream is = null;
        ArrayList<String> res = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_NATIONS_TOWNS_STREETS+"?what="+whatAutoCompleteTextView);
            connection.connect();
            is = connection.getInputStream();
            res = Utils.parseToString(LaLaLaWNetwork.readInputStream(is));
        } finally {
            if (connection!=null) LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static Worker addWorker(Worker worker, int usrID) throws IOException{
        InputStream is = null;
        Worker w = null;
        HttpURLConnection connection = null;
        try{
            ArrayList<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<String, String>("phone", worker.getPhoneNumber()));
            pairs.add(new Pair<String, String>("nationality", worker.getNationality()));
            pairs.add(new Pair<String, String>("town", worker.getVille()));
            pairs.add(new Pair<String, String>("street", worker.getQuartier()));
            pairs.add(new Pair<String, String>("passwd", worker.getPasswd()));
            pairs.add(new Pair<String, String>("usr_id", usrID+""));

            connection = LaLaLaWNetwork.newConnectionWithParams(URL_ADDWORKER, pairs.get(0), pairs.get(1), pairs.get(2), pairs.get(3),
                    pairs.get(4), pairs.get(5));
            connection.connect();
            is = connection.getInputStream();
            ArrayList<Worker> workers = Utils.parseToWorkersList(LaLaLaWNetwork.readInputStream(is));
            w = workers==null?null:workers.get(0);
        } finally {
            if (connection!=null) LaLaLaWNetwork.closeConnection(connection);
        }
        return w;
    }

    public static String editWorker(int workerID, int imod, String mod) throws IOException{
        InputStream is = null;
        String res = null;
        HttpURLConnection connection = null;
        try{
            //connection = LaLaLaWNetwork.newConnection(URL_EDITWORKER+"?worker_id="+workerID+"&imod="+imod+"&mod="+mod);
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_EDITWORKER, new Pair<>("worker_id", ""+workerID), new Pair<>("imod", ""+imod),
                    new Pair<>("mod", mod));
            connection.connect();
            is = connection.getInputStream();
            res = LaLaLaWNetwork.readInputStream(is);
        } finally {
            if (connection!=null) LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static String noteWorker(int workerID, double note) throws IOException{
        InputStream is = null;
        String res = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_NOTEWORKER+"?worker_id="+workerID+"&note="+note);
            connection.connect();
            is = connection.getInputStream();
            res = LaLaLaWNetwork.readInputStream(is);
        } finally {
            if (connection != null) LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static String incrementSollicitations(int workerID, int usrID, int type) throws IOException{
        InputStream is = null;
        String res = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_INCSOLLIS+"?worker_id="+workerID+"&usr_id="+usrID+"&type="+type);
            connection.connect();
            is = connection.getInputStream();
            res = LaLaLaWNetwork.readInputStream(is);
        } finally {
            if (connection != null) LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static String commentNoteSignal(int workerID, int usrID) throws IOException {
        InputStream is = null;
        String res = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_COMMENTNOTESIGNAL+"?worker_id="+workerID+"&usr_id="+usrID);
            System.out.println(URL_COMMENTNOTESIGNAL+"?worker_id="+workerID+"&usr_id="+usrID);
            connection.connect();
            is = connection.getInputStream();
            res = LaLaLaWNetwork.readInputStream(is);
        } finally {
            if (connection != null) LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static String signalWorker(int workerID, int usrID, String reason) throws IOException{
        InputStream is = null;
        String res = null;
        HttpURLConnection connection = null;
        try{
            //connection = LaLaLaWNetwork.newConnection(URL_STRIKEWORKER+"?_worker="+workerID+"&_user="+usrID+"&reason="+reason);
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_STRIKEWORKER, new Pair<>("worker_id", ""+workerID), new Pair<>("_user", ""+usrID),
                    new Pair<>("reason", reason));
            connection.connect();
            is = connection.getInputStream();
            res = LaLaLaWNetwork.readInputStream(is);
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static ArrayList<Strikes> getStrikes(int workerID) throws IOException{
        InputStream is = null;
        ArrayList<Strikes> strikes = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_GETSTRIKESFORWORKER, new Pair<>("worker_id", ""+workerID));
            connection.connect();
            is = connection.getInputStream();
            strikes = Utils.parseToStrikes(LaLaLaWNetwork.readInputStream(is));
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return strikes;
    }

    public static String[] workerAccountSettings(int workerID) throws IOException{
        InputStream is = null;
        String[] res = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_GETWKRSACCSETTINGS, new Pair<>("worker_id", ""+workerID));
            connection.connect();
            is = connection.getInputStream();
            res = Utils.parseWorkerAccountSettings(LaLaLaWNetwork.readInputStream(is));
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static boolean askForReacOrExtend(int workerID, String phoneNum, String what) throws IOException{
        InputStream is = null;
        boolean res = false;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_ASKFOR, new Pair<>("worker_id", ""+workerID), new Pair<>("phone", phoneNum),
                    new Pair<>("what", what));
            connection.connect();
            is = connection.getInputStream();
            res = Boolean.parseBoolean(LaLaLaWNetwork.readInputStream(is));
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static String setWorkerProfileImg(int workerID, String profile) throws IOException{
        InputStream is = null;
        String res = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_SETWORKERPROFILEIMG, new Pair<String, String>("worker_id", ""+workerID),
                    new Pair<String, String>("profile", profile));
            connection.connect();
            is = connection.getInputStream();
            res = LaLaLaWNetwork.readInputStream(is);
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static HashMap<String, String> uploadWorkerProfileImg(String imageFilePath, AccountFragment.UploadImageProfileAsyncTask task) throws IOException{
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceImage = new File(imageFilePath);
        if (!sourceImage.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }

        FileInputStream fileInputStream = new FileInputStream(sourceImage);
        conn = (HttpURLConnection) new URL(URL_UPLOADWORKERPROFILEIMG).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        conn.setRequestProperty("profile", imageFilePath.toString());
        dos = new DataOutputStream(conn.getOutputStream());

        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"profile\";filename=\"" + imageFilePath.toString() + "\"" + lineEnd);
        dos.writeBytes(lineEnd);

        bytesAvailable = fileInputStream.available();
        Log.i("Huzza", "Initial .available : " + bytesAvailable);

        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];

        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        long size = sourceImage.length();
        long sizeWrote = 0;

        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);

            sizeWrote = sizeWrote + bufferSize;
            task._publishProgress((int)((float)sizeWrote/(float)size));

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        int serverResponseCode = conn.getResponseCode();

        fileInputStream.close();
        dos.flush();
        dos.close();

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            return Utils.parseUploadProfileResponse(sb.toString());
        }else {
            return null;
        }
    }

    public static File downloadWorkerProfileImage(Worker worker) throws IOException{
        InputStream inputStream = null;
        File file = null;
        OutputStream outputStream = null;
        URLConnection connection = null;
        int count;
        try{
            file = FilesUtils.destinationPathForProfiles(worker);

            String imageUrl = Tools.URL_PROFILE_NETW+worker.getPhoto();
            URL imageURL = new URL(imageUrl);
            connection = new URL(imageUrl).openConnection();
            long size = connection.getContentLength();

            inputStream = new BufferedInputStream(imageURL.openStream(), 8192);
            outputStream = new FileOutputStream(file);

            byte[] bytes = new byte[1024];
            long sizeWrote = 0;

            while ((count = inputStream.read(bytes)) != -1){
                sizeWrote = sizeWrote + count;

                outputStream.write(bytes, 0, count);
            }
            outputStream.flush();
            return file;
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    public static class Utils{
        public static ArrayList<Worker> parseToWorkersList(String json){
            try{
                ArrayList<Worker> workers = new ArrayList<>();
                JSONArray array = new JSONArray(json);
                for (int i=0; i<array.length(); i++ ){
                    Worker worker = new Worker();
                    worker.setID(array.optJSONObject(i).getInt("ID"));
                    worker.setName(array.optJSONObject(i).getString("_name"));
                    worker.setNationality(array.optJSONObject(i).getString("nationality"));
                    worker.setPhoneNumber(array.optJSONObject(i).getInt("phone")+"");
                    worker.setPhoto(array.optJSONObject(i).getString("photo"));
                    worker.setQuartier(array.optJSONObject(i).getString("street"));
                    worker.setPasswd(array.optJSONObject(i).getString("passwd"));
                    worker.setVille(array.optJSONObject(i).getString("town"));
                    worker.setNbr_sollicitations(array.optJSONObject(i).getInt("nbrsollicitations"));
                    worker.setNote(array.optJSONObject(i).getDouble("avgnote"));
                    workers.add(worker);
                }
                return workers;
            } catch (Exception e){
                if(BuildConfig.DEBUG) {
                    Log.v("PARSE JSON WKRS", "[JSONException] e : " + e.getMessage());
                    Log.v("PARSE JSON WKRS", json);
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static ArrayList<String> parseToString(String json){
            try{
                ArrayList<String> strings = new ArrayList<>();
                System.out.println(json);
                JSONArray array = new JSONArray(json);
                for (int i=0; i<array.length(); i++ ){
                    String s = array.optJSONObject(i).getString("res");
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

        public static ArrayList<Strikes> parseToStrikes(String json){
            try{
                ArrayList<Strikes> strikes = new ArrayList<>();
                JSONArray array = new JSONArray(json);
                for (int i=0; i<array.length(); i++ ){
                    Strikes s = new Strikes();
                    s.setID(array.optJSONObject(i).getInt("ID"));
                    s.setType(array.optJSONObject(i).getInt("_type"));
                    s.setGravity(array.optJSONObject(i).getInt("_gravity"));
                    s.setMessage(array.optJSONObject(i).getString("_message"));
                    s.setWorkerID(array.optJSONObject(i).getInt("_worker"));
                    strikes.add(s);
                }
                return strikes;
            } catch (Exception e){
                if(BuildConfig.DEBUG) {
                    Log.v("PARSE JSON WKRS", "[JSONException] e : " + e.getMessage());
                    Log.v("PARSE JSON WKRS", json);
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static String[] parseWorkerAccountSettings(String json){
            try{
                String[] res = new String[3];
                JSONArray array = new JSONArray(json);
                JSONObject object = array.getJSONObject(0);
                res[0] = object.getString("_accountrenewaldate");
                res[1] = object.getString("_accountexpirationdate");
                res[2] = object.getString("_remainingtime");
                return res;
            } catch (Exception e){
                if (BuildConfig.DEBUG){
                    Log.v("PARSE JSON WKRS", "[JSONException] e : " + e.getMessage());
                    Log.v("PARSE JSON WKRS", json);
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static HashMap<String, String> parseUploadProfileResponse(String json){
            try{
                HashMap<String , String> map = new HashMap<>();
                JSONObject object = new JSONObject(json);
                map.put("error", object.getString("error"));
                map.put("file", object.getString("file"));
                map.put("file_up_url", object.getString("file_up_url"));
                return map;
            } catch (Exception e){
                Log.v("PARSE UP PROF", "[JSONException] e : " + e.getMessage());
                Log.v("PARSE UP PROF", json);
                e.printStackTrace();
            }
            return null;
        }

    }

}
