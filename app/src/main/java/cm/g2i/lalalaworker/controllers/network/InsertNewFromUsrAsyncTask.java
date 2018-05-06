package cm.g2i.lalalaworker.controllers.network;

import android.content.Context;
import android.os.AsyncTask;

import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;

/**
 * Created by Sim'S on 09/09/2017.
 */

public class InsertNewFromUsrAsyncTask extends AsyncTask<Object, Void, Void> {
    @Override
    protected Void doInBackground(Object... objects) {
        Worker worker = (Worker) objects[0];
        User user = (User) objects[1];
        int type = (Integer) objects[2];
        String add = (String) objects[3];
        Context context = (Context) objects[4];
        if (user==null) user = LaLaLaSQLiteOperations.getUser(context);
        try{
            NewsController.insertNewFromUsr(News.buildNewForInsertFromUsr(worker, user, type, add, context));
            NewsController.sendNotif(News.buildNewForInsertFromUsr(worker, user, type, add, context));
        } catch (Exception e){
            e.printStackTrace();
        }
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
