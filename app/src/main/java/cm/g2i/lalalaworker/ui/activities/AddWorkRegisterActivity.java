package cm.g2i.lalalaworker.ui.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.WorkerWorksController;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;
import cm.g2i.lalalaworker.ui.adapters.WorkerWorksListForRegisterAdapter;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;
import cm.g2i.lalalaworker.ui.fragment.ValidationDialogFragment;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class AddWorkRegisterActivity extends AppCompatActivity implements ErrorFragment.Retry{

    private RecyclerView worksList;
    private AppCompatButton addWork;
    private AutoCompleteTextView work;
    private AppCompatButton finish;

    private GetWorksAsyncTask getWorksAsyncTask;
    private Worker worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work_register);

        initializeWidgets();
    }

    @Override
    public void onStart(){
        super.onStart();
        worker = LaLaLaSQLiteOperations.getWorker(this);
        System.out.println(worker);
        getWorksAsyncTask = new GetWorksAsyncTask();
        getWorksAsyncTask.execute();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (getWorksAsyncTask!= null) getWorksAsyncTask.cancel(true);
    }

    public void initializeWidgets(){
        worksList = (RecyclerView) findViewById(R.id.activity_add_work_register_works);
        addWork = (AppCompatButton) findViewById(R.id.activity_add_work_register_add_work);
        work = (AutoCompleteTextView) findViewById(R.id.activity_add_work_register_work_to_add);
        finish = (AppCompatButton) findViewById(R.id.activity_add_work_register_finish);

        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!work.getText().toString().isEmpty()){
                    if ( ((WorkerWorksListForRegisterAdapter)worksList.getAdapter()).contains(work.getText().toString()) ){
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        ValidationDialogFragment fragment = ValidationDialogFragment.newInstance(getString(R.string.has_already_this_work_validation_msg));
                        fragment.show(fragmentManager, ValidationDialogFragment.class.getName());
                    }
                    else{
                        AddWorkRegisterAsyncTask addWorkRegisterAsyncTask = new AddWorkRegisterAsyncTask();
                        addWorkRegisterAsyncTask.execute(work.getText().toString());
                    }
                }
                else{
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ValidationDialogFragment fragment = ValidationDialogFragment.newInstance(getString(R.string.no_work_validation_message));
                    fragment.show(fragmentManager, ValidationDialogFragment.class.getName());
                }


                if (worksList.getAdapter()!=null && !((WorkerWorksListForRegisterAdapter)worksList.getAdapter()).isEmpty()){
                    finish.setText(getString(R.string.activity_add_work_register_continue_));
                }
                else{
                    finish.setText(getString(R.string.activity_add_work_register_continue_ignore));
                }
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> works = ((WorkerWorksListForRegisterAdapter)worksList.getAdapter()).getWorkerWorks();
                new AddWorkRegisterInLocalAsyncTask().execute(works);
                onBackPressed();
            }
        });

        worksList.setLayoutManager(new LinearLayoutManager(this));
        worksList.setAdapter(new WorkerWorksListForRegisterAdapter(this));
    }

    @Override
    public void retry() {
        AddWorkRegisterAsyncTask addWorkRegisterAsyncTask = new AddWorkRegisterAsyncTask();
        addWorkRegisterAsyncTask.execute(work.getText().toString());
    }

    public void showErrorFragment(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    class GetWorksAsyncTask extends AsyncTask<Void, Void, Boolean>{
        private ArrayList<String> works;
        private Exception exception;
        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                works = WorkerWorksController.getWorks(worker.getID());
            } catch (Exception e){
                e.printStackTrace();
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
                //Log.i("LLLL", works==null?"null":String.valueOf(works.size()));
                work.setAdapter(new ArrayAdapter<String>(AddWorkRegisterActivity.this, android.R.layout.simple_dropdown_item_1line, works));
                work.setThreshold(1);
            }
            else{
                System.out.println("GET WORKS ASYNC T " + aBoolean);
            }
        }
    }

    class AddWorkRegisterAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>>{
        private Exception exception;
        private ProgressDialog progressDialog;
        @Override
        protected Pair<Boolean, String> doInBackground(String... strings) {
            String res = "";
            try{
                res = WorkerWorksController.addWorkForWorker(new WorkerWork(worker.getID(), strings[0]));
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
            progressDialog = new ProgressDialog(AddWorkRegisterActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
            if (booleanStringPair.first){
                ((WorkerWorksListForRegisterAdapter)worksList.getAdapter()).addWorkerWork(work.getText().toString());
                work.setText("");
            }
            else{
                if (exception==null){
                    Log.i("AddWorkRegisterAsyncT", booleanStringPair.second);
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

    class AddWorkRegisterInLocalAsyncTask extends AsyncTask<ArrayList<String>, Void, Void>{

        @Override
        protected Void doInBackground(ArrayList<String>... arrayLists) {
            ArrayList<String> works = arrayLists[0];
            ArrayList<WorkerWork> workerWorks = new ArrayList<>();
            if (works!=null){
                for (String s: works){
                    workerWorks.add(new WorkerWork(worker.getID(), s));
                }
            }
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
