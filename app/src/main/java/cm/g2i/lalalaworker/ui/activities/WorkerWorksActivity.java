package cm.g2i.lalalaworker.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.WorkerWorksController;
import cm.g2i.lalalaworker.models.WorkerWork;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.adapters.WorkerWorksFullListAdapter;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class WorkerWorksActivity extends AppCompatActivity {

    private RelativeLayout rlOk;
    private RelativeLayout rlLoading;
    private ProgressBar progressLoading;
    private TextView errorMessage;
    private AppCompatButton reload;

    private Toolbar toolbar;
    private RecyclerView works;
    private AppCompatButton addWork;

    private ArrayList<WorkerWork> workerWorks;
    private ArrayList<WorkerWork> workerWorksInLocal;
    private ArrayList<WorkerWork> workerWorksInNet;

    private int workerID = 0;

    private WorkerWorksAsynTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_works);

        initializeWidgets();
        workerID = getIntent().getIntExtra(Tools.WORKER_INTENT_KEY, 0);
    }

    @Override
    public void onStart(){
        super.onStart();
        task = new WorkerWorksAsynTask();
        task.execute(workerID);
    }

    @Override
    public void onStop(){
        super.onStop();
        if (task!=null) task.cancel(true);
    }

    public void initializeWidgets(){
        initializeLoadingView();
        toolbar = (Toolbar) findViewById(R.id.activity_worker_works_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_worker_works_toolbar_title));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        works = (RecyclerView) findViewById(R.id.activity_worker_works_works_list);
        addWork = (AppCompatButton) findViewById(R.id.activity_worker_works_btn_add_work);

        works.setLayoutManager(new LinearLayoutManager(this));
        works.setAdapter(new WorkerWorksFullListAdapter(this));

        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerWorksActivity.this, AddWorkActivity.class);
                intent.putExtra(Tools.WORKER_INTENT_KEY, workerID);
                startActivity(intent);
            }
        });
    }

    public void initializeLoadingView(){
        rlOk = (RelativeLayout) findViewById(R.id.activity_worker_works_rl_ok);
        rlLoading = (RelativeLayout) findViewById(R.id.activity_worker_works_rl_loading);
        progressLoading = (ProgressBar) findViewById(R.id.loading_layout_progress);
        errorMessage = (TextView) findViewById(R.id.loading_layout_error_message);
        reload = (AppCompatButton) findViewById(R.id.loading_layout_reload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void bindData(){
        /*workerWorks = new ArrayList<>();
        Worker worker = SearchFragment.randomWorker();

        for (int i=0; i<17; i++){
            WorkerWork workerWork = new WorkerWork();
            workerWork.setWorkerID(worker.getID());
            workerWork.setWork(SearchFragment.ramdomWork());
            workerWorks.add(workerWork);
        }*/

    }

    class WorkerWorksAsynTask extends AsyncTask<Integer, Void, Void>{
        private Exception exception;
        @Override
        protected Void doInBackground(Integer... integers) {
            try{
                workerWorksInLocal = LaLaLaSQLiteOperations.getWorkerWorks(integers[0], getApplicationContext());
                workerWorksInNet = WorkerWorksController.workerWorksList(integers[0]);
            } catch (Exception e){
                exception = e;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rlOk.setVisibility(View.INVISIBLE);
            rlLoading.setVisibility(View.VISIBLE);
            progressLoading.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(View.INVISIBLE);
            reload.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exception==null){
                rlOk.setVisibility(View.VISIBLE);
                rlLoading.setVisibility(View.INVISIBLE);
                //((WorkerWorksFullListAdapter)works.getAdapter()).setWorkerWorks(workerWorks);
                new ArrangeWorksInLocalAsyncTask().execute();
            }
            else{
                /*rlOk.setVisibility(View.INVISIBLE);
                rlLoading.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.INVISIBLE);
                errorMessage.setVisibility(View.VISIBLE);
                reload.setVisibility(View.VISIBLE);
                if (!(exception instanceof SocketTimeoutException)) errorMessage.setText(getString(R.string.other_ioexception_message));*/
                workerWorks = workerWorksInLocal;
                rlOk.setVisibility(View.VISIBLE);
                rlLoading.setVisibility(View.INVISIBLE);
                ((WorkerWorksFullListAdapter)works.getAdapter()).setWorkerWorks(workerWorks);
            }

        }
    }

    class ArrangeWorksInLocalAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            if (workerWorksInNet!=null){
                if (workerWorksInLocal == null) workerWorksInLocal = new ArrayList<>();
                for (WorkerWork workerWork: workerWorksInNet) {
                    if (!workerWorksInLocal.contains(workerWork)) {
                        workerWorksInLocal.add(workerWork);
                    }
                }
                LaLaLaSQLiteOperations.updateWorkerWorks(workerWorksInLocal, workerID, getApplicationContext());
            }
            workerWorks = workerWorksInLocal;
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((WorkerWorksFullListAdapter)works.getAdapter()).setWorkerWorks(workerWorks);
        }
    }
}
