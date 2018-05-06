package cm.g2i.lalalaworker.ui.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.controllers.settings.SettingUnit;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;
import cm.g2i.lalalaworker.ui.adapters.WorkerListAdapter;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment  implements SearchQueryDialog.OnSearchQueriesListener, SortQueryDialog.OnSortQueryListener {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton reloadList;

    private RelativeLayout rlOk;
    private RelativeLayout rlLoading;
    private ProgressBar progressLoading;
    private TextView errorMessage;
    private AppCompatButton reload;

    private ArrayList<Pair<Worker, ArrayList<WorkerWork>>> workersList = null;
    private WorkersListAsyncTask task = null;

    private String searchQueryWork = "";
    private String searchQueryStreet = "";
    private String searchQueryTown = "";
    private String sortQuery = "";
    public String getSortQuery(){
        return sortQuery;
    }

    private User user;

    /*private int page = 0;
    private boolean isListLoading = false;
    private boolean blocked = false;*/
    private SettingUnit numbSearchedWrks;

    public SearchFragment() {
        // Required empty public constructor
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("page", page);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //if (savedInstanceState!=null) page = savedInstanceState.getInt("page", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        numbSearchedWrks = Settings.numberSearchedWorkers;

        initializeLoadingView(view);

        recyclerView = (RecyclerView) view.findViewById(R.id.workers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new WorkerListAdapter(view.getContext()));

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_search);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                SearchQueryDialog dialog = SearchQueryDialog.newInstance(searchQueryStreet, searchQueryTown, searchQueryWork);
                dialog.setTargetFragment(SearchFragment.this, 300);
                dialog.show(fm, "SearchQueryDialog");
            }
        });


        /*System.out.println("rlok: "+rlOk==null);
        System.out.println("rlloading: "+rlLoading==null);
        System.out.println("progloading: "+progressLoading==null);
        System.out.println("errormess: "+errorMessage==null);
        System.out.println("reload: "+reload==null);*/

        reloadList = (FloatingActionButton) view.findViewById(R.id.fab_refresh_list);
        reloadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeTask();
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeTask();
            }
        });
        executeTask();
    }

    public void initializeLoadingView(View view){
        rlOk = (RelativeLayout) view.findViewById(R.id.frag_search_rl_ok);
        rlLoading = (RelativeLayout) view.findViewById(R.id.loading_layout_rl_loading);
        progressLoading = (ProgressBar) view.findViewById(R.id.loading_layout_progress);
        errorMessage = (TextView) view.findViewById(R.id.loading_layout_error_message);
        reload = (AppCompatButton) view.findViewById(R.id.loading_layout_reload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeTask();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //executeTask();
    }

    @Override
    public void onStop() {
        super.onStop();
        task.cancel(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getTown(String town) {
        //Toast.makeText(this.getContext(), town, Toast.LENGTH_SHORT).show();
        searchQueryTown = town;
    }

    @Override
    public void getWork(String work) {
        //Toast.makeText(this.getContext(), work, Toast.LENGTH_SHORT).show();
        searchQueryWork = work;
    }

    @Override
    public void getStreet(String street) {
        //Toast.makeText(this.getContext(), street, Toast.LENGTH_SHORT).show();
        searchQueryStreet = street;
    }

    @Override
    public void searchQueryDialogResetData() {
        executeTask();
    }

    @Override
    public void getSortBy(String sortBy) {
        //Toast.makeText(this.getContext(), sortBy, Toast.LENGTH_SHORT).show();
        sortQuery = sortBy;
        executeTask();
    }

    public void executeTask(){
        task = new WorkersListAsyncTask();
        task.execute();
    }

    /*public void executeTaskForUpdate(){
        task = new WorkersListAsyncTask(true);
        task.execute();
    }*/

    private static String[] works = {"Programmeur", "Monteur", "Graphiste", "Mathematicien", "Youtuber", "Rappeur", "Plagieur", "Distributeur", "Chimiste", "Tueur",
        "Psychologue", "Animateur", "Beatmakeur"};
    private static String[] quartiers = {"Melen", "Mvan", "Nyalla", "Logbaba", "Ndokoti", "Ndogsimbi", "KM5", "New Bell", "Mboppi", "Akwa", "St Michel", "Yassa"};
    private static String[] villes = {"Douala", "Yaounde", "Bafoussam"};
    private static String[] names = {"Ambara", "Ogolong", "Kwite", "Laouni", "Atlas", "Sims", "Sieg"};

    public static String ramdomWork(){
        double d = Math.random()*works.length-0.5;
        return works[(int)Math.round(d)];
    }

    private static String randomVille(){
        double d = Math.random()*villes.length-0.5;
        return villes[(int)Math.round(d)];
    }

    private static String randomQuartier(){
        double d = Math.random()*quartiers.length-0.5;
        return quartiers[(int)Math.round(d)];
    }

    private static String randomName(){
        double d = Math.random()*names.length-0.5;
        return names[(int)Math.round(d)];
    }

    public static Worker randomWorker(){
        Worker w = new Worker();
        w.setName(randomName());
        w.setNationality("Camerounais");
        w.setPhoneNumber("699009900");
        w.setVille(randomVille());
        w.setQuartier(randomQuartier());
        return w;
    }

    class WorkersListAsyncTask extends AsyncTask<String, Void, Void>{
        private Exception exception = null;

        public WorkersListAsyncTask(){}
        /*public WorkersListAsyncTask(boolean forUpdate){
            this.forUpdate = forUpdate;
        }*/

        @Override
        protected Void doInBackground(String... pairs) {
            try{
                user = LaLaLaSQLiteOperations.getUser(getContext());
                String limit = null, usrTown = null, usrStreet = null;

                if (numbSearchedWrks!=null) limit = numbSearchedWrks.getValue();
                //String offset = String.valueOf(page*Integer.parseInt(numbSearchedWrks.getValue()));

                if (Settings.userTown!=null) usrTown = Settings.userTown.getValue();
                if (Settings.userStreet!=null) usrStreet = Settings.userStreet.getValue();
                boolean considerUsrLocals = Settings.considerUserLocals==null?false:Boolean.parseBoolean(Settings.considerUserLocals.getValue());
                usrTown = (usrTown==null||usrTown.isEmpty()||!considerUsrLocals)?null:usrTown;
                usrStreet = (usrStreet==null||usrStreet.isEmpty()||!considerUsrLocals)?null:usrStreet;

                if (user==null)
                    if (searchQueryWork.isEmpty() && searchQueryTown.isEmpty() && searchQueryStreet.isEmpty() && sortQuery.isEmpty())
                        workersList = WorkerController.workersGrandList(new Pair<>("limit", limit), new Pair<>("usr_street", usrStreet),
                                new Pair<>("usr_town", usrTown));
                    else
                        workersList = WorkerController.workersGrandList(new Pair<>("work", searchQueryWork), new Pair<>("street", searchQueryStreet),
                                new Pair<>("town", searchQueryTown), new Pair<>("sort", sortQuery), new Pair<>("limit", limit),
                                new Pair<>("usr_street", usrStreet), new Pair<>("usr_town", usrTown));

                else
                    workersList = WorkerController.workersGrandList(new Pair<>("work", searchQueryWork), new Pair<>("street", searchQueryStreet),
                            new Pair<>("town", searchQueryTown), new Pair<>("sort", sortQuery), new Pair<>("usr_id", ""+user.getID()),
                            new Pair<>("limit", limit), new Pair<>("usr_street", usrStreet), new Pair<>("usr_town", usrTown));
                //workersList = WorkerController.workersGrandList();
            } catch (IOException e){
                e.printStackTrace();
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exception==null){
                rlOk.setVisibility(View.VISIBLE);
                rlLoading.setVisibility(View.INVISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.INVISIBLE);
                reload.setVisibility(View.INVISIBLE);
                ((WorkerListAdapter)recyclerView.getAdapter()).setData(workersList);
            }
            else{
                rlOk.setVisibility(View.INVISIBLE);
                rlLoading.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.INVISIBLE);
                errorMessage.setVisibility(View.VISIBLE);
                reload.setVisibility(View.VISIBLE);
                if (!(exception instanceof SocketTimeoutException)){
                    errorMessage.setText(getString(R.string.other_ioexception_message));
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
    }

    private void test(View view){
        /*List<Worker> workers = new ArrayList<>();
        List<List<WorkerWork>> wws = new ArrayList<>();

        for (int i=0; i<17; i++){
            Worker w = new Worker(1, randomName(), "Camerounais", "photo", "117", randomQuartier(), randomVille());
            WorkerWork ww1 = new WorkerWork(1, ramdomWork());
            WorkerWork ww2 = new WorkerWork(1, ramdomWork());
            WorkerWork ww3 = new WorkerWork(1, ramdomWork());
            /*WorkerWork ww4 = new WorkerWork(1, ramdomWork());
            WorkerWork ww5 = new WorkerWork(1, ramdomWork());
            WorkerWork ww6 = new WorkerWork(1, ramdomWork());*/

            /*List<WorkerWork> _wws = new ArrayList<>();
            _wws.add(ww1); _wws.add(ww2); _wws.add(ww3); //_wws.add(ww4); _wws.add(ww5); _wws.add(ww6);

            workers.add(w);
            wws.add(_wws);
        }


        recyclerView.setAdapter(new WorkerListAdapter(view.getContext()));*/
    }
}
