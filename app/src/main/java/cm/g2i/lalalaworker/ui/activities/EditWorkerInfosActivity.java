package cm.g2i.lalalaworker.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.ArrayList;

public class EditWorkerInfosActivity extends AppCompatActivity implements ErrorFragment.Retry{

    private Toolbar toolbar;

    private AutoCompleteTextView edit1; private TextInputLayout til1;
    private AutoCompleteTextView edit2; private TextInputLayout til2;
    private EditText passwd;
    private AppCompatButton apply;

    private Worker worker;
    private int what;

    private AutoCompleteAdaptersAsyncTask autoCompleteAdaptersAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_worker_infos);

        initializeWidgets();
    }

    @Override
    public void onStart(){
        super.onStart();
        autoCompleteAdaptersAsyncTask = new AutoCompleteAdaptersAsyncTask();
        autoCompleteAdaptersAsyncTask.execute();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (autoCompleteAdaptersAsyncTask!=null) autoCompleteAdaptersAsyncTask.cancel(true);
    }

    public void initializeWidgets(){
        toolbar = (Toolbar) findViewById(R.id.activity_edit_worker_infos_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_edit_worker_infos_toolbar_title));

        edit1 = (AutoCompleteTextView) findViewById(R.id.activity_edit_worker_infos_edit1);
            til1 = (TextInputLayout) findViewById(R.id.activity_edit_worker_infos_til1);
        edit2 = (AutoCompleteTextView) findViewById(R.id.activity_edit_worker_infos_edit2);
            til2 = (TextInputLayout) findViewById(R.id.activity_edit_worker_infos_til2);
        passwd = (EditText) findViewById(R.id.activity_edit_worker_infos_passwd);
        apply = (AppCompatButton) findViewById(R.id.activity_edit_worker_infos_apply_mods);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(EditWorkerInfosActivity.this, passwd.getText().toString(), Toast.LENGTH_SHORT).show();
                if (!passwd.getText().toString().equals(worker.getPasswd())){
                    //System.out.println(worker.getPasswd());
                    ValidationDialogFragment fragment = ValidationDialogFragment.newInstance(getString(R.string.passwd_request_reason));
                    FragmentManager manager = getSupportFragmentManager();
                    fragment.show(manager, ValidationDialogFragment.class.getName());
                }
                else{
                    EditWorkerInfosAsyncTask editWorkerInfosAsyncTask = new EditWorkerInfosAsyncTask();
                    editWorkerInfosAsyncTask.execute(String.valueOf(what), edit1.getText().toString(), edit2.getText().toString());
                }
            }
        });


        what = getIntent().getIntExtra(Tools.MOD_WHAT, 1);
        worker = (Worker) getIntent().getSerializableExtra(Tools.WORKER_INTENT_KEY);
    }

    public void bindData(){
        switch (what){
            case Tools.MOD_PHONE:{
                til2.setVisibility(View.INVISIBLE);
                til1.setHint(getResources().getString(R.string.activity_edit_worker_infos_phone_hint));
                edit1.setInputType(InputType.TYPE_CLASS_PHONE);
                edit1.setText(worker.getPhoneNumber());
            } break;
            case Tools.MOD_NATIONALITY:{
                til2.setVisibility(View.INVISIBLE);
                til1.setHint(getResources().getString(R.string.activity_edit_worker_infos_nationality_hint));
                edit1.setText(worker.getNationality());
            } break;
            case Tools.MOD_LOCALISATION:{
                til2.setVisibility(View.VISIBLE);
                til1.setHint(getResources().getString(R.string.activity_edit_worker_infos_town_hint));
                til2.setHint(getResources().getString(R.string.activity_edit_worker_infos_street_hint));
                edit1.setText(worker.getVille());
                edit2.setText(worker.getQuartier());

                int l = getIntent().getIntExtra(Tools.MOD_WHAT_LOCAL, Tools.MOD_TOWN);
                if (l==Tools.MOD_TOWN) edit1.requestFocus();
                else if (l==Tools.MOD_STREET) edit2.requestFocus();

            } break;
        }
    }

    @Override
    public void retry() {
        EditWorkerInfosAsyncTask editWorkerInfosAsyncTask = new EditWorkerInfosAsyncTask();
        editWorkerInfosAsyncTask.execute(String.valueOf(what), edit1.getText().toString(), edit2.getText().toString());
    }

    public void showErrorFragment(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    class EditWorkerInfosAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>>{
        private int toModify;
        private Exception exception;
        private ProgressDialog progressDialog;

        @Override
        protected Pair<Boolean, String> doInBackground(String... strings) {
            try{
                String res = "";
                toModify = Integer.parseInt(strings[0]);
                if (toModify==Tools.MOD_LOCALISATION){
                    res = WorkerController.editWorker(worker.getID(), Tools.MOD_TOWN, strings[1]);

                    if (!Boolean.parseBoolean(Tools.split(res, "|")[0])) {
                        String[] tab = Tools.split(res, "|");
                        return new Pair<>(Boolean.parseBoolean(tab[0]), tab[1]);
                    }

                    res = WorkerController.editWorker(worker.getID(), Tools.MOD_STREET, strings[2]);
                }
                else{
                    res = WorkerController.editWorker(worker.getID(), Integer.parseInt(strings[0]), strings[1]);
                }
                String[] tab = Tools.split(res, "|");
                return new Pair<>(Boolean.parseBoolean(tab[0]), tab[1]);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return new Pair<>(false, "Exception");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditWorkerInfosActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
            if (booleanStringPair.first){
                switch (toModify){
                    case Tools.MOD_PHONE: worker.setPhoneNumber(edit1.getText().toString()); break;
                    case Tools.MOD_NATIONALITY: worker.setNationality(edit1.getText().toString()); break;
                    case Tools.MOD_LOCALISATION: worker.setVille(edit1.getText().toString());
                        worker.setQuartier(edit2.getText().toString()); break;
                }
                EditWorkerInfosAsyncTask_Local task_local = new EditWorkerInfosAsyncTask_Local();
                task_local.execute();
            }
            else{
                if (exception == null) Log.i("EditWrkInfos", booleanStringPair.second);
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

    class EditWorkerInfosAsyncTask_Local extends AsyncTask<String, Void, Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            return LaLaLaSQLiteOperations.updateWorker(worker, true, EditWorkerInfosActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            Toast.makeText(EditWorkerInfosActivity.this, R.string.edit_success, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    class AutoCompleteAdaptersAsyncTask extends AsyncTask<Void, Void, Boolean>{
        private ArrayList<String> nationalities;
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
                    case Tools.MOD_NATIONALITY: {
                        nationalities = WorkerController.getArrayAdaptersListForAutoCompleteTextView(Tools.NATIONALITY_KEY);
                        return (nationalities == null);
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
            /*String s = "AutoComAdapterAT";
            Log.i(s, aBoolean+"");
            Log.i(s+" nat", nationalities==null?"null":nationalities.size()+"");
            Log.i(s+" tow", towns==null?"null":towns.size()+"");
            Log.i(s+" str", streets==null?"null":streets.size()+"");*/

            Context context = EditWorkerInfosActivity.this;
            switch (what){
                case Tools.MOD_LOCALISATION:{
                    if (towns!=null) edit1.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, towns));
                    if (streets!=null) edit2.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, streets));
                } break;
                case Tools.MOD_NATIONALITY: {
                    if (nationalities!=null) edit1.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, nationalities));
                } break;
            }
            edit1.setThreshold(1);
            edit2.setThreshold(1);

            bindData();
        }
    }
}
