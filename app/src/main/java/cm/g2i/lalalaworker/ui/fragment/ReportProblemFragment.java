package cm.g2i.lalalaworker.ui.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.network.UserController;
import cm.g2i.lalalaworker.others.Tools;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportProblemFragment extends AppCompatDialogFragment {

    private EditText name;
    private EditText problem;
    private AppCompatButton report;
    private AppCompatButton cancel;

    public ReportProblemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_problem, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        name = (EditText) view.findViewById(R.id.frag_report_problem_usrname);
        problem = (EditText) view.findViewById(R.id.frag_report_problem_usrproblem);
        report = (AppCompatButton) view.findViewById(R.id.frag_report_problem_report);
        cancel = (AppCompatButton) view.findViewById(R.id.frag_report_problem_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _name = name.getText().toString().replace("'", "\\'");
                String _problem = problem.getText().toString().replace("'", "\\'");
                _name = _name.isEmpty()?null:_name;
                new ReportProblemAsyncTask().execute(_name, _problem);
            }
        });
    }

    class ReportProblemAsyncTask extends AsyncTask<String, Void, Boolean>{
        private Exception exception;
        private ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                return UserController.reportProblem(strings[0], strings[1]);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getContext(), null, getString(R.string.progress_dialog_message_wait), true, false);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (loading!=null && loading.isShowing()) loading.dismiss();
            if (aBoolean){
                Toast.makeText(getContext(), getString(R.string.frag_report_problem_success), Toast.LENGTH_LONG).show();
                dismiss();
            }
            else{
                Toast.makeText(getContext(), getString(R.string.frag_report_problem_error), Toast.LENGTH_LONG).show();
            }
        }
    }

}
