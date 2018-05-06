package cm.g2i.lalalaworker.ui.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.CommentController;
import cm.g2i.lalalaworker.controllers.network.InsertNewFromUsrAsyncTask;
import cm.g2i.lalalaworker.models.Comment;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.adapters.WorkerCommsAdapter;
import cm.g2i.lalalaworker.ui.fragment.AccountFragment;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity implements ErrorFragment.Retry{

    private RelativeLayout rlOk;
    private RelativeLayout rlLoading;
    private ProgressBar progressLoading;
    private TextView errorMessage;
    private AppCompatButton reload;

    private Toolbar toolbar;
    private TextView workerName;
    private RecyclerView comments;
    private AppCompatEditText editTextComment;
    private AppCompatButton comment;

    private ArrayList<Comment> commentsList;
    private ArrayList<Comment> commentsListNet;
    private Worker worker;

    private GetCommentsAsyncTask getCommentsAsyncTask;
    private AddCommentAsyncTask addCommentAsyncTask;
    private GetCommentsLocalAsyncTask getCommentsLocalAsyncTask;

    private boolean fromWorkerAccount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        fromWorkerAccount = getIntent().getBooleanExtra(AccountFragment.class.getName(), false);

        initializeWidgets();
        worker = (Worker) getIntent().getSerializableExtra(Tools.WORKER_INTENT_KEY);
        workerName.setText(worker.getName());

        if (fromWorkerAccount) getSupportActionBar().setTitle(getString(R.string.frag_account_comments));
        else getSupportActionBar().setTitle(getString(R.string.worker_comms_label));

        if (fromWorkerAccount) getCommentLocalAsyncTaskMethod();
        else getCommentAsyncTaskMethod();
    }

    public void initializeWidgets(){
        initializeLoadingView();
        toolbar = (Toolbar) findViewById(R.id.activity_comments_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        workerName = (TextView) findViewById(R.id.activity_comments_worker_name);
        comments = (RecyclerView) findViewById(R.id.activity_comments_comms);
        editTextComment = (AppCompatEditText) findViewById(R.id.activity_comments_comment_edittext);
        comment = (AppCompatButton) findViewById(R.id.activity_comments_comment_btn);

        comments.setLayoutManager(new LinearLayoutManager(this));
        comments.setAdapter(new WorkerCommsAdapter(this, false));

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextComment.getText().toString().isEmpty()){
                    String comment = editTextComment.getText().toString();
                    String toast = comment.length()>=10?comment.substring(0, 10):comment;
                    Toast.makeText(CommentsActivity.this, toast, Toast.LENGTH_SHORT).show();
                    addCommentAsyncTaskMethod(comment);
                }
            }
        });
    }

    public void initializeLoadingView(){
        rlOk = (RelativeLayout) findViewById(R.id.activity_comments_rl_ok);
        rlLoading = (RelativeLayout) findViewById(R.id.activity_comments_rl_loading);
        progressLoading = (ProgressBar) findViewById(R.id.loading_layout_progress);
        errorMessage = (TextView) findViewById(R.id.loading_layout_error_message);
        reload = (AppCompatButton) findViewById(R.id.loading_layout_reload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCommentAsyncTaskMethod();
            }
        });
    }

    public void bindData(){
        ((WorkerCommsAdapter)comments.getAdapter()).setList(commentsList);
    }

    public void test(){
        /*Comment c = new Comment(worker, new User(1, "Usr1", "Pass1"), new Date().toString(), "Analyse Non Constructive\nGibsy");
        commentsList = new ArrayList<>();
        for (int i=0; i<17; i++){
            commentsList.add(c);
        }*/
    }

    @Override
    public void retry() {
        addCommentAsyncTaskMethod(editTextComment.getText().toString());
    }

    public void showErrorFragment(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    class GetCommentsLocalAsyncTask extends AsyncTask<Integer, Void, Void>{
        @Override
        protected Void doInBackground(Integer... integers) {
            try{
                commentsList = LaLaLaSQLiteOperations.getComments(integers[0], getApplicationContext());
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bindData();
            getCommentAsyncTaskMethod();
        }
    }

    class GetCommentsAsyncTask extends AsyncTask<Integer, Void, Void>{
        private Exception exception;
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exception == null) {
                rlOk.setVisibility(View.VISIBLE);
                rlLoading.setVisibility(View.INVISIBLE);
                bindData();
            }
            else{
                if (!fromWorkerAccount){
                    rlOk.setVisibility(View.INVISIBLE);
                    rlLoading.setVisibility(View.VISIBLE);
                    progressLoading.setVisibility(View.INVISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                    reload.setVisibility(View.VISIBLE);
                    if (!(exception instanceof SocketTimeoutException)) errorMessage.setText(getString(R.string.other_ioexception_message));
                }
            }
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
        protected Void doInBackground(Integer... integers) {
            try{
                commentsListNet = CommentController.commentsForWorkers(worker, -1);
                if (fromWorkerAccount){
                    if (commentsListNet!=null) {
                        LaLaLaSQLiteOperations.updateComments(commentsListNet, worker.getID(), getApplicationContext());
                        commentsList = commentsListNet;
                    }
                }
                else commentsList = commentsListNet;
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return null;
        }
    }

    public void getCommentLocalAsyncTaskMethod(){
        getCommentsLocalAsyncTask = new GetCommentsLocalAsyncTask();
        getCommentsLocalAsyncTask.execute();
    }

    public void getCommentAsyncTaskMethod(){
        getCommentsAsyncTask = new GetCommentsAsyncTask();
        getCommentsAsyncTask.execute();
    }

    public void addCommentAsyncTaskMethod(String comment){
        addCommentAsyncTask = new AddCommentAsyncTask();
        addCommentAsyncTask.execute(comment);
    }

    class AddCommentAsyncTask extends AsyncTask<String, Void, Boolean>{
        private String reason = "";
        private Exception exception;

        private ProgressDialog progressDialog;

        @Override
        protected Boolean doInBackground(String... strings) {
            User user = LaLaLaSQLiteOperations.getUser(CommentsActivity.this);
            if (user == null) {
                reason = "user null";
                return false;
            }
            try{
                return CommentController.addCommentForWorker(worker.getID(), user.getID(), strings[0]);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ///System.out.println(aBoolean);
            if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();

            if (!aBoolean){
                if (!reason.isEmpty()) System.out.println(reason);
                if (exception!=null && exception instanceof SocketTimeoutException){
                    showErrorFragment(getString(R.string.connection_error_message));
                }
                else if (exception!=null && exception instanceof IOException){
                    showErrorFragment(getString(R.string.other_ioexception_message));
                }
            }
            else{
                getCommentAsyncTaskMethod();
                editTextComment.setText("");

                InsertNewFromUsrAsyncTask insertNewFromUsrAsyncTask = new InsertNewFromUsrAsyncTask();
                insertNewFromUsrAsyncTask.execute(worker, null, new Integer(News.TYPE_COMMENT), null, CommentsActivity.this);
            }
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CommentsActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }
}
