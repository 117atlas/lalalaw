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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.WorkerWorksController;
import cm.g2i.lalalaworker.models.WorkerWork;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;
import cm.g2i.lalalaworker.ui.fragment.ValidationDialogFragment;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class AddWorkActivity extends AppCompatActivity implements ErrorFragment.Retry{

    private Toolbar toolbar;
    private AutoCompleteTextView work;
    private AppCompatButton add;

    private int workerID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work);

        initializeWidgets();
        workerID = getIntent().getIntExtra(Tools.WORKER_INTENT_KEY, 0);
    }

    @Override
    public void onStart(){
        super.onStart();
        GetWorksAsyncTask getWorksAsyncTask = new GetWorksAsyncTask();
        getWorksAsyncTask.execute();
    }

    public void initializeWidgets(){
        toolbar = (Toolbar) findViewById(R.id.activity_add_work_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_add_work_toolbar_title));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        work = (AutoCompleteTextView) findViewById(R.id.activity_add_work_work);
        add = (AppCompatButton) findViewById(R.id.activity_add_work_add_btn);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (work.getText().toString().isEmpty()){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ValidationDialogFragment fragment = ValidationDialogFragment.newInstance(getString(R.string.no_work_validation_message));
                    fragment.show(fragmentManager, ValidationDialogFragment.class.getName());
                }
                else{
                    AddWorkAsyncTask addWorkAsyncTask = new AddWorkAsyncTask();
                    addWorkAsyncTask.execute(work.getText().toString());
                }
            }
        });
    }

    @Override
    public void retry() {
        AddWorkAsyncTask addWorkAsyncTask = new AddWorkAsyncTask();
        addWorkAsyncTask.execute(work.getText().toString());
    }

    public void showErrorFragment(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    class GetWorksAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<String> works;
        private Exception exception;
        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                works = WorkerWorksController.getWorks(workerID);
            } catch (Exception e){
                exception = e;
            }
            return works==null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean){
                work.setAdapter(new ArrayAdapter<String>(AddWorkActivity.this, android.R.layout.simple_dropdown_item_1line, works));
                work.setThreshold(1);
            }
            else{
                System.out.println("GET WORKS ASYNC T " + aBoolean);
            }
        }
    }

    class AddWorkAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>>{
        private Exception exception;
        private String __work;
        private ProgressDialog progressDialog;
        @Override
        protected Pair<Boolean, String> doInBackground(String... strings) {
            String res = "";
            __work = strings[0];
            try{
                res = WorkerWorksController.addWorkForWorker(new WorkerWork(workerID, strings[0]));
                boolean b = Boolean.parseBoolean(res);
                return new Pair<>(true, "true");
            } catch (Exception e){
                exception = e;
                return new Pair<>(false, res);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddWorkActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
            if (booleanStringPair.first){
                new AddWorkInLocalAsyncTask().execute(__work);
                onBackPressed();
            }
            else{
                if (exception==null){
                    Log.i("AddWorkRegisterAsyncT", booleanStringPair.second);
                    if (booleanStringPair.second.equals("This worker has already this work")){
                        FragmentManager manager = getSupportFragmentManager();
                        ValidationDialogFragment fragment = ValidationDialogFragment.newInstance(getString(R.string.has_already_this_work_validation_msg));
                        fragment.show(manager, ValidationDialogFragment.class.getName());
                    }
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

    class AddWorkInLocalAsyncTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            ArrayList<WorkerWork> workerWorks = new ArrayList<>();
            workerWorks.add(new WorkerWork(workerID, strings[0]));
            LaLaLaSQLiteOperations.insertWorkerWorks(workerWorks, getApplicationContext());
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
