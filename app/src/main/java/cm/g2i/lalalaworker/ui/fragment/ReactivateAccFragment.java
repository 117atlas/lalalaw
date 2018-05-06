package cm.g2i.lalalaworker.ui.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.others.Tools;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReactivateAccFragment extends AppCompatDialogFragment {

    private AppCompatButton askfor;
    private AppCompatButton cancel;
    private Worker worker;

    public ReactivateAccFragment() {
        // Required empty public constructor
    }

    public static ReactivateAccFragment newInstance(Worker worker){
        Bundle args = new Bundle();
        args.putSerializable(Tools.WORKER_INTENT_KEY, worker);
        ReactivateAccFragment reactivateAccFragment = new ReactivateAccFragment();
        reactivateAccFragment.setArguments(args);
        return reactivateAccFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reactivate_acc, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        askfor = (AppCompatButton) view.findViewById(R.id.frag_reactivate_acc_btnok);
        cancel = (AppCompatButton) view.findViewById(R.id.frag_reactivate_acc_cancel);
        worker = (Worker) getArguments().get(Tools.WORKER_INTENT_KEY);

        if (Integer.parseInt(Settings.remainingTime.getValue())>0) askfor.setText(getString(R.string.frag_reactivate_acc_ext));
        else askfor.setText(getString(R.string.frag_reactivate_acc_reac));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        askfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String what = askfor.getText().toString().equals(getString(R.string.frag_reactivate_acc_reac))?"reac":"ext";
                new AskForReacOrExtenAsyncTask().execute(what);
            }
        });
    }

    class AskForReacOrExtenAsyncTask extends AsyncTask<String, Void, Boolean>{
        private Exception exception;
        private ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                return WorkerController.askForReacOrExtend(worker.getID(), worker.getPhoneNumber(), strings[0]);
            } catch (Exception e){
                exception = e;
                exception.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getContext(), "", getString(R.string.progress_dialog_message_wait), true, false);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (loading!=null && loading.isShowing()) loading.dismiss();
            if (aBoolean){
                Toast.makeText(getContext(), getString(R.string.frag_reactivate_acc_success), Toast.LENGTH_LONG).show();
                dismiss();
            }
            else{
                Toast.makeText(getContext(), getString(R.string.frag_reactivate_acc_error), Toast.LENGTH_LONG).show();
            }
        }
    }

}
