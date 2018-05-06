package cm.g2i.lalalaworker.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.UserController;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;

import java.net.SocketTimeoutException;

public class RegisterUserActivity extends AppCompatActivity implements ErrorFragment.Retry{

    private EditText name;
    private TextView validator;
    private AppCompatButton register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        //System.out.println(LaLaLaSQLiteOperations.deleteAllUsers(this));
        //System.out.println("Remove worker: "+LaLaLaSQLiteOperations.removeOthersWorkers(this));
        //LaLaLaSQLiteOperations.deleteWorkerInfos(48, this);
        new InitAppAsyncTask().execute();
    }

    public void initializeWidgets(){
        name = (EditText) findViewById(R.id.activity_register_user_name);
        register = (AppCompatButton) findViewById(R.id.activity_register_user_continue);
        validator = (TextView) findViewById(R.id.activity_register_user_name_validator);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("CLICK");
                try{
                    if (name.getText().toString().isEmpty()){
                        validator.setText(getString(R.string.activity_register_user_name_validator));
                    }
                    else{
                        validator.setText("");
                        RegisterUserAsyncTask task = new RegisterUserAsyncTask();
                        task.execute(name.getText().toString());
                    }
                }
                catch (Exception e){
                    Log.e("Exception Auth", e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void retry() {
        RegisterUserAsyncTask task = new RegisterUserAsyncTask();
        task.execute(name.getText().toString());
    }

    public void showErrorFragment(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    class RegisterUserAsyncTask extends AsyncTask<String, Void, Boolean>{
        private Exception exception;
        private ProgressDialog progressDialog;
        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                /*System.out.println("AV: AUTH");
                LaLaLaAuthenticator.createAccount(strings[0], RegisterUserActivity.this);
                System.out.println("AP: AUTH");*/

                User user = new User(); user.setName(strings[0]);
                user = UserController.parseJsontoUser(UserController.addUser(user));

                LaLaLaSQLiteOperations.insertUser(user, RegisterUserActivity.this);
                System.out.println(LaLaLaSQLiteOperations.getUser(RegisterUserActivity.this).toString());

            } catch (Exception e){
                Log.e("Exception Auth", e.toString());
                e.printStackTrace();
                exception = e;
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterUserActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
            if (aBoolean) startActivity(new Intent(RegisterUserActivity.this, MainActivity.class));
            else{
                if (exception!=null){
                    if (exception instanceof SocketTimeoutException){
                        showErrorFragment(getString(R.string.connection_error_message));
                    }
                    else{
                        showErrorFragment(getString(R.string.other_ioexception_message));
                    }
                }
                else{

                }
            }
        }
    }

    class InitAppAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            User user = LaLaLaSQLiteOperations.getUser(RegisterUserActivity.this);
            Settings.user = user;
            if (user!=null){
                Settings.initializeSettings(RegisterUserActivity.this);
                Settings.worker = LaLaLaSQLiteOperations.getWorker(getApplicationContext());
                System.out.println(user);
                startActivity(new Intent(RegisterUserActivity.this, MainActivity.class));
            }
            initializeWidgets();
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
