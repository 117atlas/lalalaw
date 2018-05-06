package cm.g2i.lalalaworker.controllers.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.ArrayList;
import java.util.Random;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.ui.activities.MainActivity;
import cm.g2i.lalalaworker.ui.activities.NewsActivity;

/**
 * Created by Sim'S on 10/09/2017.
 */

public class LaLaLaGCMListenerService extends GcmListenerService {

    private static final String TAG = "LaLaLaGCMLstnrService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */


    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        int type = 117;
        try{
            type = Integer.parseInt(data.getString("type"));
        } catch (Exception e){
            e.printStackTrace();
            type = 117;
        }

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, type);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, int type) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_view);

        Intent intent = new Intent(this, NewsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.lalalalogo)
                /*.setContentTitle(title)
                .setContentText(message)*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setContent(remoteViews);

        remoteViews.setTextViewText(R.id.notification_view_text, message);
        int srcTypeImageId = R.drawable.ic_announcement_white_18dp;
        switch (type){
            case News.TYPE_NOTE: srcTypeImageId = R.drawable.ic_star_rate_white_18dp; break;
            case News.TYPE_COMMENT: srcTypeImageId = R.drawable.ic_comment_white_18dp; break;
            case News.TYPE_STRIKE: srcTypeImageId = R.drawable.ic_announcement_white_18dp; break;
            case News.TYPE_ACCOUNT_EXPIRATION: srcTypeImageId = R.drawable.ic_date_range_white_18dp; break;
            case News.TYPE_STRIKE_LEVEL: srcTypeImageId = R.drawable.ic_warning_white_18dp; break;
        }
        remoteViews.setImageViewResource(R.id.notification_view_type_notif_img, srcTypeImageId);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // On génère un nombre aléatoire pour pouvoir afficher plusieurs notifications
        notificationManager.notify(new Random().nextInt(9999), notificationBuilder.build());

        News news = new News();
        news.setMessage(message);
        news.setType(type);
        news.setNew(true);
        new InsertNewAsyncTask().execute(news);
    }

    class InsertNewAsyncTask extends AsyncTask<News, Void, Void>{
        @Override
        protected Void doInBackground(News... newses) {
            System.out.println("jhdsjhdskhdjshkdjskjdhkshjkdhkshdkjhskhdjkjk");
            ArrayList<News> a = new ArrayList<>();
            a.add(newses[0]);
            LaLaLaSQLiteOperations.insertNews(a, getApplicationContext());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
