package cm.g2i.lalalaworker.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.CommentController;
import cm.g2i.lalalaworker.controllers.network.InsertNewFromUsrAsyncTask;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.controllers.network.WorkerWorksController;
import cm.g2i.lalalaworker.controllers.services.Date;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.Comment;
import cm.g2i.lalalaworker.models.HistoryUnit;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.models.Strikes;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.adapters.WorkerCommsAdapter;
import cm.g2i.lalalaworker.ui.adapters.WorkerWorksAdapter;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;
import cm.g2i.lalalaworker.ui.fragment.NoteDialog;
import cm.g2i.lalalaworker.ui.fragment.ReactivateAccFragment;
import cm.g2i.lalalaworker.ui.fragment.SignalWorkerFragment;
import cm.g2i.lalalaworker.ui.fragment.ViewProfileImgFragment;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class WorkerActivity extends AppCompatActivity implements NoteDialog.NoteListener, ErrorFragment.Retry, SignalWorkerFragment.SignalWorkerFeedBack{

    private Toolbar toolbar;
    private ScrollView rlOk;
    private RelativeLayout rlLoading;
    private ProgressBar progressLoading;
    private TextView errorMessage;
    private AppCompatButton reload;

    //Worker infos attributes
    private ImageView profile;
    private TextView name;
    private TextView localisation;
    private TextView nationality;
    private TextView phone;
    private AppCompatButton call;
    private AppCompatButton sms;
    private AppCompatButton giveNote;
    private AppCompatRatingBar note;
    private TextView noteString;
    private TextView sollicitations;

    //Works RecyclerView
    private RecyclerView worksRecyclerView;

    //Comms Attributes
    private RecyclerView comms;
    private AppCompatButton comment;

    private boolean fromHistory = false;
    private int workerID;
    private Worker worker;
    private ArrayList<WorkerWork> workerWorks;
    private ArrayList<Comment> comments;
    private String urlProfile = Tools.URL_PROFILE_DEFAULT;

    private WorkerDetailsAsyncTask task;
    private WorkerDetailsFromLocalAsyncTask taskFromLocal;
    private boolean allowLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        initializeWidgets();
        workerID = getIntent().getIntExtra(Tools.WORKER_INTENT_KEY, 0);
        fromHistory = getIntent().getBooleanExtra(Tools.FROM_INTENT_KEY, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fromHistory){
            taskFromLocal = new WorkerDetailsFromLocalAsyncTask();
            taskFromLocal.execute(workerID);
        }
        else{
            task = new WorkerDetailsAsyncTask();
            task.execute(workerID);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (task!=null) task.cancel(true);
    }

    public void initializeWidgets(){
        initializeLoadingView();

        toolbar = (Toolbar) findViewById(R.id.toolbar_activity_worker);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Worker infos widgets
        profile = (ImageView) findViewById(R.id.worker_vh_profile);
        name = (TextView) findViewById(R.id.worker_vh_name);
        localisation = (TextView) findViewById(R.id.worker_vh_localisation);
        nationality = (TextView) findViewById(R.id.worker_vh_nationality);
        phone = (TextView) findViewById(R.id.worker_vh_phone);
        call = (AppCompatButton) findViewById(R.id.worker_vh_phonecall);
        sms = (AppCompatButton) findViewById(R.id.worker_vh_phonesms);
        giveNote = (AppCompatButton) findViewById(R.id.worker_vh_tonote);
        note = (AppCompatRatingBar) findViewById(R.id.worker_vh_note);
        noteString = (TextView) findViewById(R.id.worker_vh_notetext);
        sollicitations = (TextView) findViewById(R.id.worker_vh_soll);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callWorker();
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            smsWorker();
            }
        });
        giveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentNoteSignal("NOTE");
            }
        });

        //Works recyclerView
        worksRecyclerView = (RecyclerView) findViewById(R.id.activity_worker_works_list);
        worksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        worksRecyclerView.setAdapter(new WorkerWorksAdapter(this));

        //Comms widgets
        comms = (RecyclerView) findViewById(R.id.activity_worker_comms_list);
        comms.setLayoutManager(new LinearLayoutManager(this));
        comms.setAdapter(new WorkerCommsAdapter(this));
        comment = (AppCompatButton) findViewById(R.id.comment_button_vh_comment);

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentNoteSignal("COMMENT");
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                //System.out.println(urlProfile);
                ViewProfileImgFragment viewProfileImgFragment = ViewProfileImgFragment.newInstance(urlProfile);
                viewProfileImgFragment.show(manager, ViewProfileImgFragment.class.getName());
            }
        });
    }

    public void initializeLoadingView(){
        rlOk = (ScrollView) findViewById(R.id.activity_worker_ok);
        rlLoading = (RelativeLayout) findViewById(R.id.activity_worker_rl_loading);
        progressLoading = (ProgressBar) findViewById(R.id.loading_layout_progress);
        errorMessage = (TextView) findViewById(R.id.loading_layout_error_message);
        reload = (AppCompatButton) findViewById(R.id.loading_layout_reload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = new WorkerDetailsAsyncTask();
                task.execute(workerID);
            }
        });
    }

    public void bindData(){
        toolbar.setTitle(worker.getName());

        name.setText(worker.getName());
        localisation.setText(worker.getQuartier()+" - "+worker.getVille());
        nationality.setText(worker.getNationality());
        phone.setText(worker.getPhoneNumber());
        note.setRating((float)worker.getNote());
        noteString.setText(worker.getNote()+"/5");
        sollicitations.setText(worker.getNbr_sollicitations()+" "+getString(R.string.solis));

        if (fromHistory){
            urlProfile = worker.getlocalPhoto()==null?
                    ((worker.getPhoto()==null || worker.getPhoto().equals("null"))?Tools.URL_PROFILE_DEFAULT:(Tools.URL_PROFILE_NETW+worker.getPhoto())):
                    worker.getlocalPhoto();
        }
        else{
            if (!worker.getPhoto().equals("null")) urlProfile = Tools.URL_PROFILE_NETW+worker.getPhoto();
        }
        Tools.renderProfileImage(profile, urlProfile, this);

        ((WorkerWorksAdapter)worksRecyclerView.getAdapter()).setWorkerWorks(workerWorks);

        ((WorkerCommsAdapter)comms.getAdapter()).setList(comments);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_worker_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.activity_worker_menu_phonecall:{
                callWorker();
            } break;
            case R.id.activity_worker_menu_phonesms:{
                smsWorker();
            } break;
            case R.id.activity_worker_menu_shareworker:{
                shareWorker();
            } break;
            case R.id.activity_worker_menu_strikeworker:{
                commentNoteSignal("SIGNAL");
            } break;
            case R.id.activity_worker_menu_note:{
                commentNoteSignal("NOTE");
            } break;
            case R.id.activity_worker_menu_comment:{
                commentNoteSignal("COMMENT");
            } break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void feedback(String feedb) {
        Toast.makeText(this, feedb, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getNote(double _note) {
        Toast.makeText(this, "Note is: "+_note, Toast.LENGTH_SHORT).show();

        NoteWorkerAsyncTask noteWorkerAsyncTask = new NoteWorkerAsyncTask();
        noteWorkerAsyncTask.execute(_note);
    }

    @Override
    public void retry() {
        task = new WorkerDetailsAsyncTask();
        task.execute(workerID);
    }

    public void showErrorFragment(String message) {
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    public void showErrorFragment(String message, boolean hideRetryButton) {
        ErrorFragment errorFragment = ErrorFragment.newInstance(message, this, hideRetryButton);
        FragmentManager manager = getSupportFragmentManager();
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    public void callWorker(){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+237"+worker.getPhoneNumber()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivityForResult(intent, 117);

        AddHistoryAsyncTask addHistoryAsyncTask = new AddHistoryAsyncTask();
        addHistoryAsyncTask.execute(HistoryUnit.TYPE_CALL);
        IncrementSollicitationsAsyncTask incrementSollicitationsAsyncTask = new IncrementSollicitationsAsyncTask(117);
        incrementSollicitationsAsyncTask.execute();
    }

    public void smsWorker(){
        Uri uri = Uri.parse("smsto:" + "+237" + worker.getPhoneNumber());
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
        int templateNum = Integer.parseInt(Tools.split(Settings.smsTemplate.getValue(), " ")[1]);
        String[] templates = getResources().getStringArray(R.array.sms_templates);
        smsIntent.putExtra("sms_body", templates[templateNum]);
        startActivity(smsIntent);

        AddHistoryAsyncTask addHistoryAsyncTask = new AddHistoryAsyncTask();
        addHistoryAsyncTask.execute(HistoryUnit.TYPE_SMS);
        IncrementSollicitationsAsyncTask incrementSollicitationsAsyncTask = new IncrementSollicitationsAsyncTask(118);
        incrementSollicitationsAsyncTask.execute();
    }

    public void signalWorker(){
        SignalWorkerFragment signalWorkerFragment = SignalWorkerFragment.newInstance(worker, this);
        FragmentManager manager = getSupportFragmentManager();
        signalWorkerFragment.show(manager, SignalWorkerFragment.class.getName());
    }

    public void noteWorker(){
        NoteDialog noteDialog = new NoteDialog();
        noteDialog.show(getSupportFragmentManager(), "NoteDialog");
    }

    public void commentWorker(){
        Toast.makeText(WorkerActivity.this, "Comment", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WorkerActivity.this, CommentsActivity.class);
        intent.putExtra(Tools.WORKER_INTENT_KEY, worker);
        startActivity(intent);
    }

    public void shareWorker(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));

        String message = getString(R.string.share_worker_message);
        String works = "";
        if (workerWorks!=null){
            for (WorkerWork www: workerWorks){
                works = works + www.getWork() + ", ";
            }
            works = works.substring(0, works.length()-2);
        }
        message = message.replace("????", worker.getName()).replace("???", works);

        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, getString(R.string.share_worker)));
    }

    public void commentNoteSignal(String what){
        CommentNoteSignalAsyncTask commentNoteSignalAsyncTask = new CommentNoteSignalAsyncTask(what);
        commentNoteSignalAsyncTask.execute();
    }

    class CommentNoteSignalAsyncTask extends AsyncTask<Void, Void, Pair<Boolean, String>>{
        private ProgressDialog progressDialog;
        private Exception exception;
        private String what;

        public CommentNoteSignalAsyncTask(String what){
            this.what = what;
        }
        @Override
        protected Pair<Boolean, String> doInBackground(Void... voids) {
            try{
                if (LaLaLaSQLiteOperations.getWorkerByID(workerID, getApplicationContext())!=null) return new Pair<>(true, "");
                String res = WorkerController.commentNoteSignal(workerID, LaLaLaSQLiteOperations.getUser(getApplicationContext()).getID());
                String[] tab = Tools.split(res, "|");
                return new Pair<>(Boolean.parseBoolean(tab[0]), tab[1]);
            } catch (Exception e){
                exception = e;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(WorkerActivity.this, "", getString(R.string.progress_dialog_message_wait), true, false);
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (progressDialog!=null&&progressDialog.isShowing()) progressDialog.dismiss();
            if (exception==null){
                if (booleanStringPair.first){
                    switch (what){
                        case "NOTE": noteWorker(); break;
                        case "COMMENT": commentWorker(); break;
                        case "SIGNAL": signalWorker(); break;
                    }
                }
                else{
                    String plus = "";
                    switch (what){
                        case "NOTE": plus = getString(R.string.plus_note); break;
                        case "COMMENT": plus = getString(R.string.plus_comment); break;
                        case "SIGNAL": plus = getString(R.string.plus_signal); break;
                    }
                    showErrorFragment(getString(R.string.worker_activity_error_comment_note_signal).replace("?", plus), true);
                }
            }
            else{
                String message = "";
                if (exception instanceof SocketTimeoutException) message = getString(R.string.connection_error_message);
                else message = getString(R.string.other_ioexception_message);
                ErrorFragment errorFragment = ErrorFragment.newInstance(message, WorkerActivity.this);
                FragmentManager manager = getSupportFragmentManager();
                errorFragment.show(manager, ErrorFragment.class.getName());
            }
        }
    }

    class WorkerDetailsFromLocalAsyncTask extends AsyncTask<Integer, Void, Void>{
        private Exception exception;

        @Override
        protected Void doInBackground(Integer... integers) {
            try{
                worker = LaLaLaSQLiteOperations.getWorkerByID(workerID, WorkerActivity.this);
                workerWorks = LaLaLaSQLiteOperations.getWorkerWorks(workerID, WorkerActivity.this);
                comments = LaLaLaSQLiteOperations.getComments(workerID, WorkerActivity.this);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
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
            if (exception==null) {
                if (worker!=null && workerWorks!=null && comments!=null) {
                    bindData();
                    System.out.println("OK");
                }
                allowLoading = (worker==null || workerWorks==null || comments==null);
                task = new WorkerDetailsAsyncTask();
                task.execute(workerID);
            }
        }
    }

    class WorkerDetailsAsyncTask extends AsyncTask<Integer, Void, Void>{
        private Exception exception;
        @Override
        protected Void doInBackground(Integer... integers) {
            try{
                worker = WorkerController.getWorkerByID(integers[0]);
                workerWorks = WorkerWorksController.workerWorksList(integers[0]);
                comments = CommentController.commentsForWorkers(worker, 3);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (allowLoading){
                rlOk.setVisibility(View.INVISIBLE);
                rlLoading.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.INVISIBLE);
                reload.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exception == null){
                if (allowLoading){
                    rlOk.setVisibility(View.VISIBLE);
                    rlLoading.setVisibility(View.INVISIBLE);
                }
                bindData();
                UpdateWorkerInfosInLocalAsyncTask updateWorkerInfosInLocalAsyncTask = new UpdateWorkerInfosInLocalAsyncTask();
                updateWorkerInfosInLocalAsyncTask.execute();
            }
            else{
                if (allowLoading){
                    rlOk.setVisibility(View.INVISIBLE);
                    rlLoading.setVisibility(View.VISIBLE);
                    progressLoading.setVisibility(View.INVISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                    reload.setVisibility(View.VISIBLE);
                    if (!(exception instanceof SocketTimeoutException)) errorMessage.setText(getString(R.string.other_ioexception_message));
                }
                else{
                    String message = "";
                    if (exception instanceof SocketTimeoutException) message = getString(R.string.connection_error_message);
                    else message = getString(R.string.other_ioexception_message);
                    ErrorFragment errorFragment = ErrorFragment.newInstance(message, WorkerActivity.this);
                    FragmentManager manager = getSupportFragmentManager();
                    errorFragment.show(manager, ErrorFragment.class.getName());
                }
            }
        }
    }

    class AddHistoryAsyncTask extends AsyncTask<Integer, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Integer... integers) {
            try{
                HistoryUnit unit = new HistoryUnit();
                unit.setDate(new Date().getTimeMillis() + "");
                System.out.println(unit.getDate() + ":::::" + new Date().dateToString(WorkerActivity.this));
                unit.setType(integers[0]);
                unit.setWorker(worker);
                unit.setUser(LaLaLaSQLiteOperations.getUser(WorkerActivity.this));
                Log.i("HISTORY", unit.toString());

                /*long r = LaLaLaSQLiteOperations.insertWorker(worker, false, WorkerActivity.this);
                if (r<0) return false;*/

                long r = LaLaLaSQLiteOperations.insertHistory(unit, WorkerActivity.this);
                Log.i("INSERT HISTORY", r + "");
                if (r < 0) return false;

                LaLaLaSQLiteOperations.updateWorkerInfos(worker, workerWorks, comments, WorkerActivity.this);
                return true;

            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                Toast.makeText(WorkerActivity.this, "Ajoute a l'historique OK", Toast.LENGTH_SHORT).show();
                DownloadWorkerProfileImgAsyncTask downloadWorkerProfileImgAsyncTask = new DownloadWorkerProfileImgAsyncTask();
                downloadWorkerProfileImgAsyncTask.execute(worker);
            }
            else{
                Toast.makeText(WorkerActivity.this, "Ajoute a l'historique Erreur", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class UpdateWorkerInfosInLocalAsyncTask extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                Worker __worker = LaLaLaSQLiteOperations.getWorkerByID(worker.getID(), WorkerActivity.this);
                System.out.println("__worker:::"+ __worker);
                if (__worker!=null) {
                    worker.setLocalPhoto(__worker.getlocalPhoto());
                    LaLaLaSQLiteOperations.updateWorkerInfos(worker, workerWorks, comments, WorkerActivity.this);
                }
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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    class DownloadWorkerProfileImgAsyncTask extends AsyncTask<Worker, Integer, File>{
        private Exception exception;
        @Override
        protected File doInBackground(Worker... workers) {
            try{
                if (!worker.getPhoto().equals("null")){
                    File file = WorkerController.downloadWorkerProfileImage(workers[0]);

                    worker.setLocalPhoto(file.getAbsolutePath());
                    System.out.println(worker.getlocalPhoto());
                    System.out.println("Update when download img "+LaLaLaSQLiteOperations.updateWorker(worker, false, WorkerActivity.this));

                    return file;
                }
                return null;
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file!=null) Toast.makeText(WorkerActivity.this, file==null?exception.getMessage():file.getPath(), Toast.LENGTH_LONG).show();
        }
    }

    class NoteWorkerAsyncTask extends AsyncTask<Double, Void, Pair<Boolean, String>>{
        private Exception exception;
        private double _note = 0.0;
        @Override
        protected Pair<Boolean, String> doInBackground(Double... doubles) {
            String res = "";
            try{
                _note = doubles[0];
                res = WorkerController.noteWorker(worker.getID(), doubles[0]);
                String[] tab = Tools.split(res, "|");
                System.out.println(res);
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
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (booleanStringPair.first){
                task = new WorkerDetailsAsyncTask();
                task.execute(workerID);

                InsertNewFromUsrAsyncTask insertNewFromUsrAsyncTask = new InsertNewFromUsrAsyncTask();
                insertNewFromUsrAsyncTask.execute(worker, null, new Integer(News.TYPE_NOTE), new String(_note + "/5"), WorkerActivity.this);
            }
            else{
                if (exception == null){
                    Toast.makeText(WorkerActivity.this, R.string.worker_activity_error_noting, Toast.LENGTH_SHORT).show();
                    System.out.println(booleanStringPair.second);
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

    class IncrementSollicitationsAsyncTask extends AsyncTask<Void, Void, Pair<Boolean, String>>{
        private Exception exception;
        private int type;

        public IncrementSollicitationsAsyncTask(int type){
            this.type = type;
        }

        @Override
        protected Pair<Boolean, String> doInBackground(Void... voids) {
            try{
                User user = LaLaLaSQLiteOperations.getUser(getApplicationContext());
                String res = WorkerController.incrementSollicitations(worker.getID(), user.getID(), type);
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
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (booleanStringPair.first){
                task = new WorkerDetailsAsyncTask();
                task.execute(workerID);
            }
            else{
                if (exception==null){
                    Toast.makeText(WorkerActivity.this, R.string.worker_activity_error_inc_sollis, Toast.LENGTH_SHORT).show();
                    System.out.println(booleanStringPair.second);
                }
                else{
                    System.out.println(exception.getMessage());
                }
            }
        }
    }

    public void test(){
        /*worker = new Worker(1, "Ademo", "Terien", "Photo", "695698569", "Tarterets", "91");
        WorkerWork workerWork = new WorkerWork(1, "Rappeur");
        WorkerWork workerWork1 = new WorkerWork(1, "Bicraveur");
        WorkerWork workerWork2 = new WorkerWork(1, "Sais pas");
        workerWorks = new ArrayList<>();
        workerWorks.add(workerWork);
        workerWorks.add(workerWork1);
        workerWorks.add(workerWork2);

        Comment comment1 = new Comment(worker, new User(1, "Usr1", "pass1"), new Date().toString(), "Commentaire 1\nAnalyse non constructive");
        Comment comment2 = new Comment(worker, new User(2, "Usr2", "pass2"), new Date().toString(), "Commentaire 1\nAnalyse non constructive");
        Comment comment3 = new Comment(worker, new User(3, "Usr3", "pass3"), new Date().toString(), "Commentaire 1\nAnalyse non constructive");
        comments  = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        //comments.add(comment3);

        setTitle(worker.getName());*/
    }
}
