package cm.g2i.lalalaworker.ui.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.WorkerWorksController;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;
import cm.g2i.lalalaworker.ui.fragment.ErrorFragment;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by Sim'S on 04/08/2017.
 */

public class WorkerWorksListForRegisterAdapter extends RecyclerView.Adapter<WorkerWorksListForRegisterAdapter.WorkerWorksListForRegisterViewHolder> {
    private Context context;
    private ArrayList<String> workerWorks;

    public WorkerWorksListForRegisterAdapter(Context context){
        this.context = context;
    }

    public void addWorkerWork(String work){
        if (workerWorks==null) workerWorks = new ArrayList<>();
        this.workerWorks.add(work);
        notifyDataSetChanged();
    }

    public boolean isEmpty(){
        return workerWorks==null||workerWorks.size()==0;
    }

    public boolean contains(String work){
        return (workerWorks!=null && workerWorks.contains(work));
    }

    public ArrayList<String> getWorkerWorks() {
        return workerWorks;
    }

    @Override
    public WorkerWorksListForRegisterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new WorkerWorksListForRegisterViewHolder(inflater.inflate(R.layout.worker_works_full_list_vh_layout_reg, parent, false));
    }

    @Override
    public void onBindViewHolder(WorkerWorksListForRegisterViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return workerWorks==null?0:workerWorks.size();
    }

    class WorkerWorksListForRegisterViewHolder extends RecyclerView.ViewHolder implements ErrorFragment.Retry{
        private TextView work;
        private AppCompatButton deleteWork;

        private int currentPosition;

        public WorkerWorksListForRegisterViewHolder(View itemView) {
            super(itemView);

            work = (TextView) itemView.findViewById(R.id.worker_works_full_list_vh_work);
            deleteWork = (AppCompatButton) itemView.findViewById(R.id.worker_works_full_list_vh_delete_work);

            deleteWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RemoveWorkOnWorkerAsyncTask removeWorkOnWorkerAsyncTask = new RemoveWorkOnWorkerAsyncTask();
                    removeWorkOnWorkerAsyncTask.execute(work.getText().toString(), String.valueOf(currentPosition));
                }
            });

        }

        public void bindData(int position){
            currentPosition = position;
            work.setText(workerWorks.get(position));
        }

        @Override
        public void retry() {
            RemoveWorkOnWorkerAsyncTask removeWorkOnWorkerAsyncTask = new RemoveWorkOnWorkerAsyncTask();
            removeWorkOnWorkerAsyncTask.execute(work.getText().toString(), String.valueOf(currentPosition));
        }

        public void showErrorFragment(String message){
            ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
            FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
            errorFragment.show(manager, ErrorFragment.class.getName());
        }

        class RemoveWorkOnWorkerAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>>{
            private int currentPosition;
            private Exception exception;
            private ProgressDialog progressDialog;
            @Override
            protected Pair<Boolean, String> doInBackground(String... strings) {
                Worker worker = LaLaLaSQLiteOperations.getWorker(context);
                String res = "";
                try{
                    res = WorkerWorksController.removeWorkOnWorker(new WorkerWork(worker.getID(), strings[0]));
                    boolean b = Boolean.parseBoolean(res);
                    currentPosition = Integer.parseInt(strings[1]);
                    return new Pair<>(true, "true");
                } catch (Exception e){
                    return new Pair<>(false, res);
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
                super.onPostExecute(booleanStringPair);
                if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
                if (booleanStringPair.first){
                    WorkerWorksListForRegisterAdapter.this.workerWorks.remove(currentPosition);
                    WorkerWorksListForRegisterAdapter.this.notifyDataSetChanged();
                }
                else{
                    if (exception==null) Log.i("AddWorkRegisterAsyncT", booleanStringPair.second);
                    else{
                        if (exception instanceof SocketTimeoutException){
                            showErrorFragment(context.getString(R.string.connection_error_message));
                        }
                        else{
                            showErrorFragment(context.getString(R.string.other_ioexception_message));
                        }
                    }
                }
            }
        }

    }

}
