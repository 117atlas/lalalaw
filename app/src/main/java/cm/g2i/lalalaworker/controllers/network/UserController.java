package cm.g2i.lalalaworker.controllers.network;

import android.util.Log;
import android.util.Pair;

import cm.g2i.lalalaworker.BuildConfig;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.User;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by Sim'S on 01/08/2017.
 */

public class UserController {
    private static final String ADD = Settings.ADD;
    private static final String URL_ADDUSER = "http://"+ADD+"/adduser.php";
    private static final String URL_EDITUSER = "http://"+ ADD+"/edituser.php";
    private static final String URL_REPORTPROBLEM = "http://"+ADD+"/reportproblem.php";

    public static String addUser(User user) throws IOException{
        InputStream is = null;
        String result = null;
        HttpURLConnection conn = null;
        try{
            conn = LaLaLaWNetwork.newConnectionWithParams(URL_ADDUSER, new Pair<String, String>("name", user.getName()) );
            conn.connect();
            is = conn.getInputStream();
            result = LaLaLaWNetwork.readInputStream(is);
        } finally {
            LaLaLaWNetwork.closeConnection(conn);
        }
        return result;
    }

    public static String editUser(User user) throws IOException{
        InputStream is = null;
        String result = null;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnection(URL_EDITUSER+"?user_id="+user.getID()+"&name="+user.getName());
            connection.connect();
            is = connection.getInputStream();
            result = LaLaLaWNetwork.readInputStream(is);
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return result;
    }

    public static boolean reportProblem(String name, String problem) throws IOException{
        InputStream is= null;
        boolean res = false;
        HttpURLConnection connection = null;
        try{
            connection = LaLaLaWNetwork.newConnectionWithParams(URL_REPORTPROBLEM, new Pair<>("name", name), new Pair<>("problem", problem));
            connection.connect();
            is = connection.getInputStream();
            res = Boolean.parseBoolean(LaLaLaWNetwork.readInputStream(is));
        } finally {
            LaLaLaWNetwork.closeConnection(connection);
        }
        return res;
    }

    public static User parseJsontoUser(String json){
        try {
            User user = new User();
            JSONArray array = new JSONArray(json);
            for (int i=0; i<array.length(); i++){
                user.setID(array.optJSONObject(i).getInt("ID"));
                user.setName(array.optJSONObject(i).getString("_name"));
            }
            System.out.println("Ctrl: "+user.toString());
            return user;
        } catch (Exception e){
            if(BuildConfig.DEBUG) {
                Log.v("PARSE JSON WKRS", "[JSONException] e : " + e.getMessage());
                Log.v("PARSE JSON WKRS", json);
                e.printStackTrace();
            }
        }
        return null;
    }

    private static class Utils{

    }

}
