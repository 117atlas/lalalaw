package cm.g2i.lalalaworker.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.UserController;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.controllers.settings.SettingUnit;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class EditUserInfosActivity extends AppCompatActivity implements ErrorFragment.Retry{

    private Toolbar toolbar;

    private AutoCompleteTextView edit1; private TextInputLayout til1;
    private AutoCompleteTextView edit2; private TextInputLayout til2;
    private AppCompatButton apply;

    private User user;
    private int what;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_infos);

        user = (User) getIntent().getSerializableExtra(Tools.USER_INTENT_KEY);
        what = getIntent().getIntExtra(Tools.MOD_WHAT, Tools.MOD_NAME);

        initializeWidgets();
    }

    @Override
    public void onStart(){
        super.onStart();
        if (what==Tools.MOD_LOCALISATION){
            AutoCompleteAdaptersAsyncTask autoCompleteAdaptersAsyncTask = new AutoCompleteAdaptersAsyncTask();
            autoCompleteAdaptersAsyncTask.execute();
        }
        else bindData();
    }

    public void initializeWidgets(){
        toolbar = (Toolbar) findViewById(R.id.activity_edit_user_infos_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_edit_user_infos_toolbar_title));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        edit1 = (AutoCompleteTextView) findViewById(R.id.activity_edit_user_infos_edit1);
            til1 = (TextInputLayout) findViewById(R.id.activity_edit_user_infos_til1);
        edit2 = (AutoCompleteTextView) findViewById(R.id.activity_edit_user_infos_edit2);
            til2 = (TextInputLayout) findViewById(R.id.activity_edit_user_infos_til2);
        apply = (AppCompatButton) findViewById(R.id.activity_edit_user_infos_apply_mods);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (what == Tools.MOD_NAME){
                    EditUserAsyncTask editUserAsyncTask = new EditUserAsyncTask();
                    editUserAsyncTask.execute(edit1.getText().toString());
                }
                else if (what == Tools.MOD_LOCALISATION){
                    Settings.userTown.setValue(edit1.getText().toString());
                    Settings.userStreet.setValue(edit2.getText().toString());

                    EditUserLocalsAsyncTask editUserLocalsAsyncTask = new EditUserLocalsAsyncTask();
                    editUserLocalsAsyncTask.execute(Settings.userTown, Settings.userStreet);
                }
            }
        });
    }

    public void bindData(){
        switch (what){
            case Tools.MOD_NAME:{
                til2.setVisibility(View.INVISIBLE);

                til1.setHint(getString(R.string.activity_simple_account_name_label));
                edit1.setText(user.getName());
            } break;
            case Tools.MOD_LOCALISATION:{
                til2.setVisibility(View.VISIBLE);

                til1.setHint(getString(R.string.activity_simple_account_town_label));
                edit1.setText(Settings.userTown.getValue());
                til2.setHint(getString(R.string.activity_simple_account_street_label));
                edit2.setText(Settings.userStreet.getValue());

                int l = getIntent().getIntExtra(Tools.MOD_WHAT_LOCAL, Tools.MOD_TOWN);
                if (l == Tools.MOD_TOWN) edit1.requestFocus();
                else edit2.requestFocus();

            } break;
        }
    }

    @Override
    public void retry() {
        EditUserAsyncTask editUserAsyncTask = new EditUserAsyncTask();
        editUserAsyncTask.execute(edit1.getText().toString());
    }

    public void showErrorFragment(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    class AutoCompleteAdaptersAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<String> towns;
        private ArrayList<String> streets;
        private Exception exception;
        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                switch (what){
                    case Tools.MOD_LOCALISATION:{
                        towns = WorkerController.getArrayAdaptersListForAutoCompleteTextView(Tools.TOWN_KEY);
                        streets = WorkerController.getArrayAdaptersListForAutoCompleteTextView(Tools.STREET_KEY);
                        return (towns == null || streets == null);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Context context = EditUserInfosActivity.this;
            switch (what){
                case Tools.MOD_LOCALISATION:{
                    if (towns!=null) edit1.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, towns));
                    if (streets!=null) edit2.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, streets));
                } break;
            }
            edit1.setThreshold(1);
            edit2.setThreshold(1);

            bindData();
        }
    }

    class EditUserAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>>{
        private Exception exception;
        private ProgressDialog progressDialog;
        @Override
        protected Pair<Boolean, String> doInBackground(String... strings) {
            try{
                user.setName(strings[0]);
                String result = UserController.editUser(user);
                String[] tab = Tools.split(result, "|");
                return new Pair<>(Boolean.parseBoolean(tab[0]), tab[1]);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
                return new Pair<>(false, e.getMessage());
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditUserInfosActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.progress_dialog_message));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
            if (booleanStringPair.first){
                EditUserLocalsAsyncTask editUserLocalsAsyncTask = new EditUserLocalsAsyncTask();
                editUserLocalsAsyncTask.execute();
            }
            else{
                if (exception==null){
                    Log.i("EditUsrInfos", booleanStringPair.second);
                }
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

    class EditUserLocalsAsyncTask extends AsyncTask<SettingUnit, Void, Boolean>{

        @Override
        protected Boolean doInBackground(SettingUnit... settingUnits) {
            /*Account[] accounts = LaLaLaAuthenticator.getAccountsByType(LaLaLaAuthenticator.WORKER_ACCOUNT_TYPE, EditUserInfosActivity.this);
            if (accounts!=null && accounts.length>0){
                Account account = accounts[0];
            }*/
            if (what == Tools.MOD_LOCALISATION){
                long l1 = LaLaLaSQLiteOperations.updateSettingUnit(settingUnits[0], EditUserInfosActivity.this);
                long l2 = LaLaLaSQLiteOperations.updateSettingUnit(settingUnits[1], EditUserInfosActivity.this);
                return (l1>0 && l2>0);
            }
            else{
                int i = LaLaLaSQLiteOperations.updateUser(user, EditUserInfosActivity.this);
                return i>0;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String feedback = "";
            if (aBoolean) feedback = getString(R.string.set_settings_feedback_pos);
            else feedback = getString(R.string.set_settings_feedback_neg);
            Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_LONG).show();
            if (aBoolean) onBackPressed();
        }
    }
}
