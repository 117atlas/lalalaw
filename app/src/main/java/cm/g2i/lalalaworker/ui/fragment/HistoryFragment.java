package cm.g2i.lalalaworker.ui.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.models.History;
import cm.g2i.lalalaworker.models.HistoryUnit;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.ui.adapters.HistoryAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView history;

    private ArrayList<History> histories;

    private HistoryAsyncTask task;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public ArrayList<History> getHistories(){return histories;}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        history = (RecyclerView) view.findViewById(R.id.frag_history_list);

        history.setLayoutManager(new LinearLayoutManager(getContext()));
        history.setAdapter(new HistoryAdapter(getContext()));

    }

    @Override
    public void onStart() {
        super.onStart();
        executeTask();
    }

    public void executeTask(){
        task = new HistoryAsyncTask();
        task.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void test(){
        HistoryUnit unit = new HistoryUnit(HistoryUnit.TYPE_CALL, "15/12/2017");
        History h = new History();
        h.addUnit(unit);

        ArrayList<History> histories = new ArrayList<>();
        for (int i=0; i<17; i++){
            histories.add(h);
        }

        ((HistoryAdapter)history.getAdapter()).setHistories(histories);
    }

    class HistoryAsyncTask extends AsyncTask<User, Void, Void>{
        @Override
        protected Void doInBackground(User... users) {
            histories = LaLaLaSQLiteOperations.historiesFullList(getContext());
            //System.out.println(histories==null?"Null":histories.size());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((HistoryAdapter)history.getAdapter()).setHistories(histories);
        }
    }

}
