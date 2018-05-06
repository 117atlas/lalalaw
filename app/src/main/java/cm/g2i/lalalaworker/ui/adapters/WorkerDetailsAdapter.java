package cm.g2i.lalalaworker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.models.Comment;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;

import java.util.ArrayList;

/**
 * Created by Sim'S on 21/07/2017.
 */

public class WorkerDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> list;
    private Context context;

    private final static int WORKER_VIEWTYPE = 1;
    private final static int WORK_VIEWTYPE = 2;
    private final static int HEADER_VIEWTYPE = 3;
    private final static int COMMS_VIEWTYPE = 4;
    private final static int BUTTON_VIEWTYPE = 5;

    public WorkerDetailsAdapter(Context context){
        this.context = context;
    }

    public void updateList(ArrayList<Object> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder = null;
        Log.i("onCreateViewHolder", viewType+"");
        switch (viewType){
            case WORKER_VIEWTYPE:{
                viewHolder = new WorkerViewHolder(inflater.inflate(R.layout.worker_viewholder_layout, parent, false));
            } break;
            case WORK_VIEWTYPE:{
                viewHolder = new WorkViewHolder(inflater.inflate(R.layout.work_viewholder_layout, parent, false));
            } break;
            case HEADER_VIEWTYPE:{
                viewHolder = new HeaderViewHolder(inflater.inflate(R.layout.items_list_header, parent, false));
            } break;
            case COMMS_VIEWTYPE:{
                viewHolder = new CommsViewHolder(inflater.inflate(R.layout.comms_viewholder_layout, parent, false));
            } break;
            case BUTTON_VIEWTYPE:{
                viewHolder = new CommentViewHolder(inflater.inflate(R.layout.comment_button_viewholder_layout, parent, false));
            } break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkerViewHolder) ((WorkerViewHolder) holder).bindData((Worker)list.get(position));
        else if (holder instanceof WorkViewHolder) ((WorkViewHolder) holder).bindData((WorkerWork)list.get(position));
        else if (holder instanceof CommsViewHolder) ((CommsViewHolder) holder).bindData((Comment) list.get(position));
        else if (holder instanceof HeaderViewHolder) ((HeaderViewHolder) holder).setHeaderTitle((String) list.get(position));
        else if (holder instanceof CommentViewHolder) ((CommentViewHolder) holder).bindData();
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = list.get(position);
        if (o instanceof Worker){
            return WORKER_VIEWTYPE;
        }
        if (o instanceof WorkerWork) return WORK_VIEWTYPE;
        if (o instanceof Comment) return COMMS_VIEWTYPE;
        if (o instanceof String){
            String s = (String) o;
            if (s.equals("Comment")){
                return BUTTON_VIEWTYPE;
            }
            else{
                return HEADER_VIEWTYPE;
            }
        }
        return 0;
    }

    class WorkerViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile;
        private TextView name;
        private TextView localisation;
        private TextView nationality;
        private TextView phone;
        private AppCompatButton call;
        private AppCompatButton sms;
        private AppCompatButton giveNote;

        public WorkerViewHolder(View itemView) {
            super(itemView);

            profile = (ImageView) itemView.findViewById(R.id.worker_vh_profile);
            name = (TextView) itemView.findViewById(R.id.worker_vh_name);
            localisation = (TextView) itemView.findViewById(R.id.worker_vh_localisation);
            nationality = (TextView) itemView.findViewById(R.id.worker_vh_nationality);
            phone = (TextView) itemView.findViewById(R.id.worker_vh_phone);
            call = (AppCompatButton) itemView.findViewById(R.id.worker_vh_phonecall);
            sms = (AppCompatButton) itemView.findViewById(R.id.worker_vh_phonesms);
            giveNote = (AppCompatButton) itemView.findViewById(R.id.worker_vh_tonote);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            giveNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void bindData(Worker worker){
            name.setText(worker.getName());
            localisation.setText(worker.getQuartier()+" - "+worker.getVille());
            nationality.setText(worker.getNationality());
            phone.setText(worker.getPhoneNumber());
        }

    }

    class WorkViewHolder extends RecyclerView.ViewHolder{
        private TextView work;
        public WorkViewHolder(View itemView) {
            super(itemView);

            work = (TextView) itemView.findViewById(R.id.work_vh_work);
        }

        public void bindData(WorkerWork workerWork){
            work.setText(workerWork.getWork());
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView headerTitle;
        public HeaderViewHolder(View itemView) {
            super(itemView);

            headerTitle = (TextView) itemView.findViewById(R.id.items_list_header_title);
        }

        public void setHeaderTitle(String title){
            headerTitle.setText(title);
        }
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

        public void bindData(Comment comment){
            name.setText(comment.getUser().getName());
            date.setText(comment.getDate());
            comm.setText(comment.getComment());
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        private AppCompatButton comment;
        public CommentViewHolder(View itemView) {
            super(itemView);

            comment = (AppCompatButton) itemView.findViewById(R.id.comment_button_vh_comment);
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bindData(){

        }
    }

}
