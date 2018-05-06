package cm.g2i.lalalaworker.ui.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.controllers.network.WorkerWorksController;
import cm.g2i.lalalaworker.others.Tools;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchQueryDialog extends DialogFragment {

    private RelativeLayout rlOk;
    private RelativeLayout rlLoading;
    private ProgressBar progressLoading;
    private TextView errorMessage;
    private AppCompatButton reload;

    private AppCompatAutoCompleteTextView work;
    private AppCompatAutoCompleteTextView town;
    private AppCompatAutoCompleteTextView street;
    private AppCompatButton ok;
    private AppCompatButton cancel;
    private AppCompatButton reset;

    private AutoCompleteAdaptersAsyncTask autoCompleteAdaptersAsyncTask;

    interface OnSearchQueriesListener{
        public void getTown(String town);
        public void getWork(String work);
        public void getStreet(String street);
        public void searchQueryDialogResetData();
    }

    public SearchQueryDialog() {
        // Required empty public constructor
    }

    public static SearchQueryDialog newInstance(String street, String town, String work){
        SearchQueryDialog searchQueryDialog = new SearchQueryDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Tools.STREET_KEY, street);
        bundle.putString(Tools.TOWN_KEY, town);
        bundle.putString("Work", work);
        searchQueryDialog.setArguments(bundle);
        return searchQueryDialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_search_query_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeLoadingView(view);

        work = (AppCompatAutoCompleteTextView) view.findViewById(R.id.search_dialog_work);
        town = (AppCompatAutoCompleteTextView) view.findViewById(R.id.search_dialog_town);
        street = (AppCompatAutoCompleteTextView) view.findViewById(R.id.search_dialog_street);
        ok = (AppCompatButton) view.findViewById(R.id.search_dialog_ok);
        cancel = (AppCompatButton) view.findViewById(R.id.search_dialog_cancel);
        reset = (AppCompatButton) view.findViewById(R.id.search_dialog_reset);

        work.setText(getArguments().getString("Work"));
        town.setText(getArguments().getString(Tools.TOWN_KEY));
        street.setText(getArguments().getString(Tools.STREET_KEY));

        getDialog().setTitle(R.string.app_name);

        work.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSearchQueries();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoCompleteAdaptersAsyncTask!=null) autoCompleteAdaptersAsyncTask.cancel(true);
                dismiss();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                work.setText("");
                street.setText("");
                town.setText("");
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        autoCompleteAdaptersAsyncTask = new AutoCompleteAdaptersAsyncTask();
        autoCompleteAdaptersAsyncTask.execute();
    }

    public void initializeLoadingView(View view){
        rlOk = (RelativeLayout) view.findViewById(R.id.frag_search_query_dialog_ok);
        rlLoading = (RelativeLayout) view.findViewById(R.id.loading_layout_rl_loading);
        progressLoading = (ProgressBar) view.findViewById(R.id.loading_layout_progress);
        errorMessage = (TextView) view.findViewById(R.id.loading_layout_error_message);
        reload = (AppCompatButton) view.findViewById(R.id.loading_layout_reload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void sendSearchQueries(){
        OnSearchQueriesListener onSearchQueriesListener = (OnSearchQueriesListener) getTargetFragment();
        onSearchQueriesListener.getWork(work.getText().toString());
        onSearchQueriesListener.getTown(town.getText().toString());
        onSearchQueriesListener.getStreet(street.getText().toString());
        onSearchQueriesListener.searchQueryDialogResetData();
        dismiss();
    }

    class AutoCompleteAdaptersAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<String> works;
        private ArrayList<String> towns;
        private ArrayList<String> streets;
        private Exception exception;
        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                towns = WorkerController.getArrayAdaptersListForAutoCompleteTextView(Tools.TOWN_KEY);
                streets = WorkerController.getArrayAdaptersListForAutoCompleteTextView(Tools.STREET_KEY);
                works = WorkerWorksController.getWorks();
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return true;
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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            rlOk.setVisibility(View.VISIBLE);
            rlLoading.setVisibility(View.INVISIBLE);

            if (works!=null) work.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, works));
            if (towns!=null) town.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, towns));
            if (streets!=null) street.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, streets));
            work.setThreshold(1);
            town.setThreshold(1);
            street.setThreshold(1);
        }
    }
}
