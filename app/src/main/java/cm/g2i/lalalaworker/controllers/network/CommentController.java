package cm.g2i.lalalaworker.controllers.network;

import android.util.Log;
import android.util.Pair;

import cm.g2i.lalalaworker.BuildConfig;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.Comment;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Sim'S on 02/08/2017.
 */

public class CommentController {
    private static final String ADD = Settings.ADD;
    private static final String URL_GETCOMMS = "http://"+ADD+"/getcommsforworker.php";
    private static final String URL_ADDCOMM = "http://"+ADD+"/addcommforworker.php";

    public static ArrayList<Comment> commentsForWorkers(Worker worker, int limit) throws IOException{
        InputStream is = null;
        ArrayList<Comment> comments = null;
        HttpURLConnection connection = null;
        try{
            String url = URL_GETCOMMS+"?worker_id="+worker.getID();
            url = limit>0?url+"&limit="+limit:url+"";
            connection = LaLaLaWNetwork.newConnection(url);
            connection.connect();
            is = connection.getInputStream();
            comments = Utils.parseToComms(LaLaLaWNetwork.readInputStream(is), worker);
        } finally {
            if(connection!=null) LaLaLaWNetwork.closeConnection(connection);
        }
        return comments;
    }

    public static boolean addCommentForWorker(int workerID, int usrID, String comment) throws IOException{
        InputStream is = null;
        boolean res = false;
        HttpURLConnection connection = null;
        try{
            /*String url = URL_ADDCOMM+"?worker_id="+workerID+"&usr_id="+usrID+"&comment="+comment;
            System.out.println(url);
            connection = LaLaLaWNetwork.newConnection(url);*/
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_ADDCOMM, new Pair<>("worker_id", ""+workerID),
                    new Pair<>("usr_id", ""+usrID), new Pair<>("comment", comment));
            is = connection.getInputStream();
            String rrr = LaLaLaWNetwork.readInputStream(is);
            res = Boolean.parseBoolean(rrr);
            //System.out.println(rrr);
        } finally {
            if(connection!=null) LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    private static class Utils{
        public static ArrayList<Comment> parseToComms(String json, Worker w){
            try{
                ArrayList<Comment> comments = new ArrayList<>();
                JSONArray array = new JSONArray(json);
                for (int i=0; i<array.length(); i++){
                    Comment comment = new Comment();
                    comment.setWorker(w);

                    comment.setID(array.optJSONObject(i).getInt("ComID"));
                    comment.setDate(array.optJSONObject(i).getString("_date"));
                    comment.setComment(array.optJSONObject(i).getString("_comment"));

                    User u = new User();
                    u.setID(array.optJSONObject(i).getInt("ID"));
                    u.setName(array.optJSONObject(i).getString("_name"));
                    comment.setUser(u);

                    comments.add(comment);
                }
                return comments;
            } catch (Exception e){
                if (BuildConfig.DEBUG){
                    Log.v("PARSE JSON COMMS", "[JSONException] e : " + e.getMessage());
                    Log.v("PARSE JSON COMMS", json);
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
