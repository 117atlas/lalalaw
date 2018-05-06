package cm.g2i.lalalaworker.ui.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.ui.adapters.NewsAdapter;

public class NewsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView news;

    private ArrayList<News> newses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initializeComponents();
        new GetNewsAsyncTask().execute();
    }

    public void initializeComponents(){
        toolbar = (Toolbar) findViewById(R.id.activity_news_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.frag_account_somenews));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        news = (RecyclerView) findViewById(R.id.activity_news_news);
        news.setAdapter(new NewsAdapter(this));
        news.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_clear){
            new DeleteNewsAsyncTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    class GetNewsAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            newses = LaLaLaSQLiteOperations.getNews(getApplicationContext());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((NewsAdapter)news.getAdapter()).setNewses(newses);
            new UpdateNewsAsyncTask().execute();
        }
    }

    class DeleteNewsAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            LaLaLaSQLiteOperations.deleteNews(getApplicationContext());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            newses = null;
            ((NewsAdapter)news.getAdapter()).setNewses(newses);
        }
    }

    class UpdateNewsAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            LaLaLaSQLiteOperations.updateNews(getApplicationContext());
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
