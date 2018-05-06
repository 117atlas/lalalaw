package cm.g2i.lalalaworker.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appauthenticator.LaLaLaAuthenticator;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;
import cm.g2i.lalalaworker.ui.fragment.ValidationDialogFragment;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class RegisterWorkerActivity extends AppCompatActivity implements ErrorFragment.Retry{

    private EditText phone;
    private AutoCompleteTextView nationality;
    private AutoCompleteTextView town;
    private AutoCompleteTextView street;
    private EditText passwd;
    private EditText confirmPasswd;
    private AppCompatCheckBox acceptLicenceTerms;
    private TextView licenseTerms;
    private AppCompatButton createWA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_worker);

        initializeWidgets();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AutoCompleteAdaptersAsyncTask autoCompleteAdaptersAsyncTask = new AutoCompleteAdaptersAsyncTask();
        autoCompleteAdaptersAsyncTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void initializeWidgets(){
        phone = (EditText) findViewById(R.id.activity_register_worker_phone);
        nationality = (AutoCompleteTextView) findViewById(R.id.activity_register_worker_nationality);
        town = (AutoCompleteTextView) findViewById(R.id.activity_register_worker_town);
        street = (AutoCompleteTextView) findViewById(R.id.activity_register_worker_street);
        passwd = (EditText) findViewById(R.id.activity_register_worker_passwd);
        confirmPasswd = (EditText) findViewById(R.id.activity_register_worker_confirmpasswd);
        acceptLicenceTerms = (AppCompatCheckBox) findViewById(R.id.activity_register_worker_accept_conditions);
        licenseTerms = (TextView) findViewById(R.id.activity_register_worker_conditions_link);
        createWA = (AppCompatButton) findViewById(R.id.activity_register_worker_create_account);

        createWA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInfos()){
                    RegisterWorkerAsyncTask registerWorkerAsyncTask = new RegisterWorkerAsyncTask();
                    Worker worker = new Worker();
                    worker.setPhoneNumber(phone.getText().toString());
                    worker.setNationality(nationality.getText().toString());
                    worker.setVille(town.getText().toString());
                    worker.setQuartier(street.getText().toString());
                    worker.setPasswd(passwd.getText().toString());
                    System.out.println(worker);
                    registerWorkerAsyncTask.execute(worker);
                }
            }
        });

        licenseTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                licenseTerms.setTextColor(getResources().getColor(R.color.colorAccentLight));
            }
        });
    }

    public boolean checkInfos(){
        Pattern pattern = Pattern.compile("^6[2379][0-9]{7}$");
        if (phone.getText().toString().isEmpty()){
            phone.requestFocus();
            showValidationDialog(getString(R.string.validation_message_phone));
            return false;
        }
        if (!pattern.matcher(phone.getText().toString()).matches()){
            phone.requestFocus();
            showValidationDialog(getString(R.string.validation_message_phone_regex));
            return false;
        }
        if (nationality.getText().toString().isEmpty()){
            nationality.requestFocus();
            showValidationDialog(getString(R.string.validation_message_nationality));
            return false;
        }
        if (town.getText().toString().isEmpty()){
            town.requestFocus();
            showValidationDialog(getString(R.string.validation_message_town));
            return false;
        }
        if (street.getText().toString().isEmpty()){
            street.requestFocus();
            showValidationDialog(getString(R.string.validation_message_street));
            return false;
        }
        if (passwd.getText().toString().isEmpty()){
            passwd.requestFocus();
            showValidationDialog(getString(R.string.validation_message_passwd));
            return false;
        }
        if (!confirmPasswd.getText().toString().equals(passwd.getText().toString())){
            confirmPasswd.requestFocus();
            showValidationDialog(getString(R.string.validation_message_confirm_passwd));
            return false;
        }
        if (!acceptLicenceTerms.isChecked()){
            acceptLicenceTerms.requestFocus();
            showValidationDialog(getString(R.string.validation_message_license_accept));
            return false;
        }
        return true;
    }

    public void showValidationDialog(String message){
        ValidationDialogFragment validationDialogFragment = ValidationDialogFragment.newInstance(message);
        FragmentManager manager = getSupportFragmentManager();
        validationDialogFragment.show(manager, ValidationDialogFragment.class.getName());
    }

    @Override
    public void retry() {
        if (checkInfos()){
            RegisterWorkerAsyncTask registerWorkerAsyncTask = new RegisterWorkerAsyncTask();
            Worker worker = new Worker();
            worker.setPhoneNumber(phone.getText().toString());
            worker.setNationality(nationality.getText().toString());
            worker.setVille(town.getText().toString());
            worker.setQuartier(street.getText().toString());
            worker.setPasswd(passwd.getText().toString());
            System.out.println(worker);
            registerWorkerAsyncTask.execute(worker);
        }
    }

    public void showErrorFragment(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    class AutoCompleteAdaptersAsyncTask extends AsyncTask<Void, Void, Boolean>{
        private ArrayList<String> nationalities;
        private ArrayList<String> towns;
        private ArrayList<String> streets;
        private Exception exception;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                nationalities = WorkerController.getArrayAdaptersListForAutoCompleteTextView(Tools.NATIONALITY_KEY);
                towns = WorkerController.getArrayAdaptersListForAutoCompleteTextView(Tools.TOWN_KEY);
                streets = WorkerController.getArrayAdaptersListForAutoCompleteTextView(Tools.STREET_KEY);
            } catch (Exception e){
                exception = e;
                e.printStackTrace();
            }
            return (nationalities == null || towns == null || streets == null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String s = "AutoComAdapterAT";
            Log.i(s, aBoolean+"");
            Log.i(s+" nat", nationalities==null?"null":nationalities.size()+"");
            Log.i(s+" tow", towns==null?"null":towns.size()+"");
            Log.i(s+" str", streets==null?"null":streets.size()+"");

            Context context = RegisterWorkerActivity.this;
            if (nationalities!=null) nationality.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, nationalities));
            if (towns!=null) town.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, towns));
            if (streets!=null) street.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, streets));
            nationality.setThreshold(1);
            town.setThreshold(1);
            street.setThreshold(1);
        }
    }

    class RegisterWorkerAsyncTask extends AsyncTask<Worker, Void, Boolean>{
        private String reason = "";
        private Exception exception;
        private ProgressDialog progressDialog;
        @Override
        protected Boolean doInBackground(Worker... workers) {
            try{
                User user = LaLaLaSQLiteOperations.getUser(RegisterWorkerActivity.this);
                Worker worker = workers[0];
                worker = WorkerController.addWorker(worker, user.getID());
                if (worker==null) {
                    reason = "ADD WORKER WKRS CONTRLL";
                    return false;
                }

                LaLaLaSQLiteOperations.insertWorker(worker, true, RegisterWorkerActivity.this);
                LaLaLaAuthenticator.createWorkerAccount(worker.getName(), worker.getPasswd(), RegisterWorkerActivity.this);
                return true;
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterWorkerActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
            if (!aBoolean){
                if (!reason.isEmpty()) System.out.println("Register Wrk Async reason of echec " + reason);
                if (exception==null){}
                else{
                    if (exception instanceof SocketTimeoutException){
                        showErrorFragment(getString(R.string.connection_error_message));
                    }
                    else{
                        showErrorFragment(getString(R.string.other_ioexception_message));
                    }
                }
            }
            else{
                startActivity(new Intent(RegisterWorkerActivity.this, AddWorkRegisterActivity.class));
            }
        }
    }
}
