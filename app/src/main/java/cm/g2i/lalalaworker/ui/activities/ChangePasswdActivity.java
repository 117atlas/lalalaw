package cm.g2i.lalalaworker.ui.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;
import cm.g2i.lalalaworker.ui.fragment.ValidationDialogFragment;

import java.net.SocketTimeoutException;

public class ChangePasswdActivity extends AppCompatActivity implements ErrorFragment.Retry{

    private Toolbar toolbar;

    private EditText oldPasswd;
    private EditText newPasswd;
    private EditText confirm;
    private AppCompatButton apply;

    private Worker worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwd);

        initializeWidgets();
    }

    public void initializeWidgets(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_activity_change_passwd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_change_passwd_toolbar_title));

        worker = (Worker) getIntent().getSerializableExtra(Tools.WORKER_INTENT_KEY);
        if (worker == null) worker = LaLaLaSQLiteOperations.getWorker(this);

        oldPasswd = (EditText) findViewById(R.id.activity_change_passwd_old_passwd);
        newPasswd = (EditText) findViewById(R.id.activity_change_passwd_new_passwd);
        confirm = (EditText) findViewById(R.id.activity_change_passwd_confirm_passwd);
        apply = (AppCompatButton) findViewById(R.id.activity_change_passwd_apply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!oldPasswd.getText().toString().equals(worker.getPasswd())){
                    ValidationDialogFragment fragment = ValidationDialogFragment.newInstance(getString(R.string.wrong_old_passwd));
                    FragmentManager manager = getSupportFragmentManager();
                    fragment.show(manager, ValidationDialogFragment.class.getName());
                    return;
                }

                if (!newPasswd.getText().toString().equals(confirm.getText().toString())){
                    ValidationDialogFragment fragment = ValidationDialogFragment.newInstance(getString(R.string.validation_message_confirm_passwd));
                    FragmentManager manager = getSupportFragmentManager();
                    fragment.show(manager, ValidationDialogFragment.class.getName());
                }
                else{
                    String s = "Old: "+oldPasswd.getText().toString()+"\n";
                    s = s + "New: "+newPasswd.getText().toString()+"\n";
                    s = s + "Confirm: "+confirm.getText().toString();
                    Toast.makeText(ChangePasswdActivity.this, s, Toast.LENGTH_SHORT).show();

                    ChangePasswdAsyncTask changePasswdAsyncTask = new ChangePasswdAsyncTask();
                    changePasswdAsyncTask.execute(newPasswd.getText().toString());
                }
            }
        });
    }

    @Override
    public void retry() {
        ChangePasswdAsyncTask changePasswdAsyncTask = new ChangePasswdAsyncTask();
        changePasswdAsyncTask.execute(newPasswd.getText().toString());
    }

    public void showErrorFragment(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    class ChangePasswdAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>>{
        private Exception exception;
        private ProgressDialog progressDialog;
        @Override
        protected Pair<Boolean, String> doInBackground(String... strings) {
            String res = "";
            try{
                res = WorkerController.editWorker(worker.getID(), Tools.MOD_PASSWD, strings[0]);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            String[] tab = Tools.split(res, "|");
            return new Pair<>(Boolean.parseBoolean(tab[0]), tab[1]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChangePasswdActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
            if (booleanStringPair.first){
                worker.setPasswd(newPasswd.getText().toString());

                ChangePasswdLocalAsyncTask changePasswdLocalAsyncTask = new ChangePasswdLocalAsyncTask();
                changePasswdLocalAsyncTask.execute();
            }
            else{
                if (exception==null) Log.i("ChangePasswdErr", booleanStringPair.second);
                else{
                    if (exception instanceof SocketTimeoutException){
                        showErrorFragment(getString(R.string.connection_error_message));
                    }
                    else{
                        showErrorFragment(getString(R.string.other_ioexception_message));
                    }
                }
            }
        }
    }

    class ChangePasswdLocalAsyncTask extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... voids) {
            return LaLaLaSQLiteOperations.updateWorker(worker, true, ChangePasswdActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer >= 0){
                Toast.makeText(ChangePasswdActivity.this, R.string.edit_success, Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
    }
}
