package cm.g2i.lalalaworker.controllers.network;

import android.util.Log;
import android.util.Pair;

import cm.g2i.lalalaworker.BuildConfig;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.models.Strikes;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Sim'S on 09/09/2017.
 */

public class NewsController {

    private static final String ADD = Settings.ADD;
    private static final String URL_GETNEWSESFORWORKER = "http://"+ADD+"/getnewsesforworker.php";
    private static final String URL_INSERTNEW = "http://"+ADD+"/insertnew.php";
    private static final String URL_SENDNOTIF = "http://"+ADD+"/gcm/send_message.php";

    public static ArrayList<News> getNews(int workerID) throws IOException {
        InputStream is = null;
        ArrayList<News> newses = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_GETNEWSESFORWORKER, new Pair<>("worker_id", ""+workerID));
            connection.connect();
            is = connection.getInputStream();
            newses = Utils.parseToNews(LaLaLaWNetwork.readInputStream(is));
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return newses;
    }

    public static String insertNewFromUsr(News news) throws IOException{
        InputStream is = null;
        String res = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_INSERTNEW, new Pair<>("worker_id", news.getWorkerID()+""),
                    new Pair<>("type", news.getType()+""), new Pair<>("message", news.getMessage()));
            connection.connect();
            is = connection.getInputStream();
            res = LaLaLaWNetwork.readInputStream(is);
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static String sendNotif(News news) throws IOException{
        InputStream is = null;
        String res = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_SENDNOTIF, new Pair<>("worker", news.getWorkerID()+""),
                    new Pair<>("type", news.getType()+""), new Pair<>("message", news.getMessage()));
            connection.connect();
            is = connection.getInputStream();
            res = LaLaLaWNetwork.readInputStream(is);
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    private static class Utils{
        public static ArrayList<News> parseToNews(String json){
            try{
                ArrayList<News> newses = new ArrayList<>();
                JSONArray array = new JSONArray(json);
                for (int i=0; i<array.length(); i++ ){
                    News news = new News(array.optJSONObject(i).getInt("ID"), array.optJSONObject(i).getInt("_type"), array.optJSONObject(i).getString("_message"),
                            array.optJSONObject(i).getInt("_worker"));
                    newses.add(news);
                }
                return newses;
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
