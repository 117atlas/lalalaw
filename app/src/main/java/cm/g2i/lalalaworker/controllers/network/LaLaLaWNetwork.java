package cm.g2i.lalalaworker.controllers.network;

import android.net.Uri;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sim'S on 01/08/2017.
 */

public class LaLaLaWNetwork {

    public static HttpURLConnection newConnection(String url) throws MalformedURLException, IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        return conn;
    }

    public static HttpURLConnection newConnectionWithParams(String url, Pair<String, String>... params) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(25000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("POST");

        Uri.Builder builder = new Uri.Builder();
        int number = params.length;
        for (int i=0; i<number; i++){
            if (params[i].second!=null && !params[i].second.isEmpty()) builder.appendQueryParameter(params[i].first, params[i].second);
        }
        String query = builder.build().getEncodedQuery();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        System.out.println(query);
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();
        return conn;
    }

    public static String readInputStream(InputStream is) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null){
            builder.append(line);
        }
        //closeInputStream(is);
        return builder.toString();
    }

    public static void closeInputStream(InputStream is) throws IOException{
        if (is != null){
            is.close();
        }
    }

    public static void closeConnection(HttpURLConnection connection){
        if (connection!= null) connection.disconnect();
    }



}
