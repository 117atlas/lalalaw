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
 * Created by Sim'S on 29/07/2017.
 */

public class WorkerWorksFullListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<WorkerWork> workerWorks;

    private static final int VIEW_TYPE = 1;
    private static final int VIEW_TYPE_ZERO = -1;

    public WorkerWorksFullListAdapter(Context context){
        this.context = context;
    }

    public void setWorkerWorks(ArrayList<WorkerWork> workerWorks){
        if (workerWorks!= null && workerWorks.size()>0) this.workerWorks = workerWorks;
        else{
            workerWorks = new ArrayList<>();
            workerWorks.add(new WorkerWork(-1, context.getString(R.string.empty_list_workersworks)));
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE)
            return new WorkerWorksFullListViewHolder(inflater.inflate(R.layout.worker_works_full_list_vh_layout, parent, false));
        else
            return new EmptyListViewHolder(inflater.inflate(R.layout.empty_list_vh_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkerWorksFullListViewHolder)
            ((WorkerWorksFullListViewHolder)holder).bindData(position);
        else
            ((EmptyListViewHolder)holder).bindData(workerWorks.get(position).getWork());
    }

    @Override
    public int getItemViewType(int position) {
        return workerWorks.get(position).getWorkerID()>0?VIEW_TYPE:VIEW_TYPE_ZERO;
    }

    @Override
    public int getItemCount() {
        return workerWorks==null?0:workerWorks.size();
    }

    class WorkerWorksFullListViewHolder extends RecyclerView.ViewHolder implements ErrorFragment.Retry{
        private TextView work;
        private AppCompatButton deleteWork;

        private int currentPosition;

        public WorkerWorksFullListViewHolder(View itemView) {
            super(itemView);

            work = (TextView) itemView.findViewById(R.id.worker_works_full_list_vh_work);
            deleteWork = (AppCompatButton) itemView.findViewById(R.id.worker_works_full_list_vh_delete_work);

            deleteWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RemoveWorkOnWorkerAsyncTask removeWorkOnWorkerAsyncTask = new RemoveWorkOnWorkerAsyncTask();
                    removeWorkOnWorkerAsyncTask.execute(work.getText().toString(), ""+currentPosition);
                }
            });
        }

        public void bindData(int position){
            currentPosition = position;
            work.setText(workerWorks.get(position).getWork());
        }

        @Override
        public void retry() {
            RemoveWorkOnWorkerAsyncTask removeWorkOnWorkerAsyncTask = new RemoveWorkOnWorkerAsyncTask();
            removeWorkOnWorkerAsyncTask.execute(work.getText().toString(), ""+currentPosition);
        }

        public void showErrorFragment(String message){
            ErrorFragment errorFragment = ErrorFragment.newInstance(message, this);
            FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
            errorFragment.show(manager, ErrorFragment.class.getName());
        }

        class RemoveWorkOnWorkerAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>> {
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
                    e.printStackTrace();
                    exception = e;
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
                    WorkerWorksFullListAdapter.this.workerWorks.remove(currentPosition);
                    WorkerWorksFullListAdapter.this.notifyDataSetChanged();
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
