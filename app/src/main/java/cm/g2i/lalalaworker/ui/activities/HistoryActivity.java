package cm.g2i.lalalaworker.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.models.History;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.adapters.UserHistoryAdapter;

import java.io.File;

public class HistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView profile;
    private TextView name;
    private AppCompatButton call;
    private AppCompatButton sms;
    private AppCompatButton seeProfile;
    private RecyclerView history;

    private History _history;

    private ClearHistoryAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeWidgets();
        bindData();
    }

    public void initializeWidgets(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_activity_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.tab_history));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        profile = (ImageView) findViewById(R.id.activity_history_profile);
        name = (TextView) findViewById(R.id.activity_history_name);
        call = (AppCompatButton) findViewById(R.id.activity_history_phonecall);
        sms = (AppCompatButton) findViewById(R.id.activity_history_phonesms);
        seeProfile = (AppCompatButton) findViewById(R.id.activity_history_seeprofile);
        history = (RecyclerView) findViewById(R.id.activity_history_history);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        seeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, WorkerActivity.class);
                intent.putExtra(Tools.WORKER_INTENT_KEY, _history.getUnits().get(0).getWorker().getID());
                intent.putExtra(Tools.FROM_INTENT_KEY, true);
                startActivity(intent);
            }
        });
    }

    public void bindData(){
        _history = (History) getIntent().getSerializableExtra("History");

        name.setText(_history.getUnits().get(0).getWorker().getName());
        //Tools.renderProfileImage(profile, _history.getUnits().get(0).getWorker().getlocalPhoto(), HistoryActivity.this);
        Tools.renderProfileImage(profile,
                _history.getUnits().get(0).getWorker().getlocalPhoto()==null ||
                _history.getUnits().get(0).getWorker().getlocalPhoto().equals("null")?
                        Tools.URL_PROFILE_DEFAULT:_history.getUnits().get(0).getWorker().getlocalPhoto(),
                HistoryActivity.this);

        history.setLayoutManager(new LinearLayoutManager(this));
        history.setAdapter(new UserHistoryAdapter(this));
        ((UserHistoryAdapter)history.getAdapter()).setHistory(_history);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.activity_history_menu_phonesms:{

            } break;
            case R.id.activity_history_menu_phonecall:{

            } break;
            case R.id.activity_history_menu_clear:{
                task = new ClearHistoryAsyncTask();
                task.execute(_history.getUnits().get(0).getWorker().getID());
            } break;
        }
        return super.onOptionsItemSelected(item);
    }

    class ClearHistoryAsyncTask extends AsyncTask<Integer, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... integers) {
            /*int r = LaLaLaSQLiteOperations.removeWorker(integers[0], HistoryActivity.this);
            System.out.println("Remove wrk " + r);
            if (r<0) return false;*/
            LaLaLaSQLiteOperations.deleteWorkerInfos(integers[0], HistoryActivity.this);
            int r = LaLaLaSQLiteOperations.removeHistoriesForWorker(integers[0], HistoryActivity.this);
            //System.out.println("Remove histories from wrk " + r);
            if (r<0) return false;

            if (_history.getUnits().get(0).getWorker().getlocalPhoto()!=null ||
                    !_history.getUnits().get(0).getWorker().getlocalPhoto().equals("null")){
                File file = new File(_history.getUnits().get(0).getWorker().getlocalPhoto());
                if (file.exists()) file.delete();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                onBackPressed();
                Toast.makeText(HistoryActivity.this, "Removed from history", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(HistoryActivity.this, "Error when Remove from history", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aBoolean);
        }
    }
}
