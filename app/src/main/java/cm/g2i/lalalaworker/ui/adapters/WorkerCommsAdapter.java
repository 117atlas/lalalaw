package cm.g2i.lalalaworker.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.models.Comment;

import java.util.ArrayList;

/**
 * Created by Sim'S on 24/07/2017.
 */

public class WorkerCommsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Comment> list;
    private Context context;

    private static final int VIEW_TYPE = 1;
    private static final int VIEW_TYPE_ZERO = -1;
    private boolean fromUsr = true;

    public WorkerCommsAdapter(Context context){
        this.context = context;
    }

    public WorkerCommsAdapter(Context context, boolean fromUsr){
        this.context = context;
        this.fromUsr = fromUsr;
    }

    public void setList(ArrayList<Comment> list){
        if (list!=null && list.size()>0)
            this.list = list;
        else {
            this.list = new ArrayList<>();
            this.list.add(new Comment(-1, null, null, null, null));
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE)
            return new CommsViewHolder(inflater.inflate(R.layout.comms_viewholder_layout, parent, false));
        else
            return new EmptyListViewHolder(inflater.inflate(R.layout.empty_list_vh_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommsViewHolder)
            ((CommsViewHolder)holder).bindData(position);
        else
            ((EmptyListViewHolder)holder).bindData(fromUsr?context.getString(R.string.empty_list_comms):context.getString(R.string.empty_list_workerscomms));
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getID()>0?VIEW_TYPE:VIEW_TYPE_ZERO;
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class CommsViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView date;
        private TextView comm;

        public CommsViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.comms_vh_name);
            date = (TextView) itemView.findViewById(R.id.comms_vh_date);
            comm = (TextView) itemView.findViewById(R.id.comms_vh_comm);
        }

        public void bindData(int position){
            Comment comment = list.get(position);
            name.setText(comment.getUser().getName());
            date.setText(comment.getDate());
            comm.setText(comment.getComment());

            if (comment.getWorker().getName().equals(comment.getUser().getName())){
                Toast.makeText(context, comment.getWorker().getName()+":::"+comment.getUser().getName(), Toast.LENGTH_LONG).show();
                name.setBackgroundColor(Color.BLUE);
                name.setTextColor(Color.WHITE);
            }
            else{
                name.setBackgroundColor(Color.WHITE);
                name.setTextColor(Color.BLUE);
            }
        }
    }
}
