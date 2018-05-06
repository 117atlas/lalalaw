package cm.g2i.lalalaworker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.models.WorkerWork;

import java.util.ArrayList;

/**
 * Created by Sim'S on 23/07/2017.
 */

public class WorkerWorksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<WorkerWork> workerWorks = null;
    private Context context;

    private static final int VIEW_TYPE = 1;
    private static final int VIEW_TYPE_ZERO = -1;

    public WorkerWorksAdapter(Context context){
        this.context = context;
    }

    public void setWorkerWorks(ArrayList<WorkerWork> list){
        if (list!=null && list.size()>0) workerWorks = list;
        else{
            workerWorks = new ArrayList<>();
            workerWorks.add(new WorkerWork(-1, context.getString(R.string.empty_list_works)));
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType==VIEW_TYPE)
            return new WorkViewHolder(inflater.inflate(R.layout.work_viewholder_layout, parent, false));
        else
            return new EmptyListViewHolder(inflater.inflate(R.layout.empty_list_vh_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkerWorksAdapter.WorkViewHolder)
            ((WorkerWorksAdapter.WorkViewHolder)holder).bindData(position);
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

    class WorkViewHolder extends RecyclerView.ViewHolder{
        private TextView work;
        public WorkViewHolder(View itemView) {
            super(itemView);

            work = (TextView) itemView.findViewById(R.id.work_vh_work);
        }

        public void bindData(int position){
            work.setText(workerWorks.get(position).getWork());
        }
    }
}
