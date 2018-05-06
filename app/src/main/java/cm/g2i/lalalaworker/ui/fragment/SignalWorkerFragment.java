package cm.g2i.lalalaworker.ui.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.InsertNewFromUsrAsyncTask;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.News;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.others.Tools;

import java.net.SocketTimeoutException;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignalWorkerFragment extends DialogFragment {

    private RelativeLayout rlOk;
    private RelativeLayout rlLoading;
    private ProgressBar progressLoading;
    private TextView errorMessage;
    private AppCompatButton reload;

    private TextView header;
    private EditText reason;
    private TextView reasonCharCounter;
    private AppCompatButton submit;
    private AppCompatButton cancel;

    private int workerID;
    private String workerName;

    private SignalWorkerFeedBack signalWorkerFeedBack;

    public interface SignalWorkerFeedBack{
        public void feedback(String feedb);
    }

    public SignalWorkerFragment() {
        // Required empty public constructor
    }

    public static SignalWorkerFragment newInstance(Worker worker, SignalWorkerFeedBack signalWorkerFeedBack){
        SignalWorkerFragment signalWorkerFragment = new SignalWorkerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Tools.WORKER_INTENT_KEY, worker.getName());
        bundle.putInt(Tools.WORKER_INTENT_KEY+"_ID", worker.getID());
        signalWorkerFragment.setArguments(bundle);
        signalWorkerFragment.signalWorkerFeedBack = signalWorkerFeedBack;
        return signalWorkerFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_signal_worker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        initializeLoadingView(view);
        workerID = getArguments().getInt(Tools.WORKER_INTENT_KEY+"_ID");
        workerName = getArguments().getString(Tools.WORKER_INTENT_KEY);

        header = (TextView) view.findViewById(R.id.frag_signal_worker_header);
        reason = (EditText) view.findViewById(R.id.frag_signal_worker_reason);
        reasonCharCounter = (TextView) view.findViewById(R.id.reason_character_counter);
        submit = (AppCompatButton) view.findViewById(R.id.frag_signal_worker_submit);
        cancel = (AppCompatButton) view.findViewById(R.id.frag_signal_worker_cancel);

        header.setText(getString(R.string.frag_signal_worker_header)+" "+workerName+"?");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _reason = reason.getText().toString();
                SignalWorkerAsyncTask signalWorkerAsyncTask = new SignalWorkerAsyncTask();
                signalWorkerAsyncTask.execute(String.valueOf(workerID), _reason);
            }
        });

        reason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void initializeLoadingView(View view){
        rlOk = (RelativeLayout) view.findViewById(R.id.frag_signal_worker_ok);
        rlLoading = (RelativeLayout) view.findViewById(R.id.frag_signal_worker_rl_loading);
        progressLoading = (ProgressBar) view.findViewById(R.id.loading_layout_progress);
        errorMessage = (TextView) view.findViewById(R.id.loading_layout_error_message);
        reload = (AppCompatButton) view.findViewById(R.id.loading_layout_reload);
        rlLoading.setVisibility(View.INVISIBLE);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignalWorkerAsyncTask signalWorkerAsyncTask = new SignalWorkerAsyncTask();
                signalWorkerAsyncTask.execute(String.valueOf(workerID), reason.getText().toString());
            }
        });
    }

    class SignalWorkerAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>>{
        private Exception exception;
        private String _signal = "";
        @Override
        protected Pair<Boolean, String> doInBackground(String... strings) {
            try{
                _signal = strings[1];
                User user = LaLaLaSQLiteOperations.getUser(getContext());
                String res = WorkerController.signalWorker(Integer.parseInt(strings[0]), user.getID(), strings[1]);
                String[] tab = Tools.split(res, "|");
                return new Pair<>(Boolean.parseBoolean(tab[0]), tab[1]);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return new Pair<>(false, exception.getMessage());
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
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            if (booleanStringPair.first){
                rlOk.setVisibility(View.VISIBLE);
                rlLoading.setVisibility(View.INVISIBLE);
                signalWorkerFeedBack.feedback(getContext().getString(R.string.signal_worker_feedback));
                dismiss();

                if (!Boolean.parseBoolean(Settings.anonymousSignal.getValue())){
                    Toast.makeText(getContext(), "shdjshdshdjhsj", Toast.LENGTH_SHORT).show();
                    InsertNewFromUsrAsyncTask insertNewFromUsrAsyncTask = new InsertNewFromUsrAsyncTask();
                    Worker worker = new Worker(); worker.setName(workerName);
                    insertNewFromUsrAsyncTask.execute(worker, null, new Integer(News.TYPE_STRIKE), _signal, getContext());
                }

            }
            else{
                if (exception==null){
                    rlOk.setVisibility(View.VISIBLE);
                    rlLoading.setVisibility(View.INVISIBLE);
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
        }
    }

}
