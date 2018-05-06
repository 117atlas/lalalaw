package cm.g2i.lalalaworker.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.models.WorkerWork;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.activities.WorkerActivity;

import java.util.ArrayList;

/**
 * Created by Sim'S on 06/07/2017.
 */

public class WorkerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable{

    private String query;

    private ArrayList<Pair<Worker, ArrayList<WorkerWork>>> data;
    private Context context;
    private boolean isEmpty = false;

    private static final int VIEW_TYPE = 1;
    private static final int VIEW_TYPE_ZERO = -1;

    private static final String ADD = "http://"+ Settings.ADD;

    private ArrayList<Pair<Worker, ArrayList<WorkerWork>>> filteredData;

    public WorkerListAdapter(Context context) {
        this.context = context;
    }

    public boolean isEmpty(){
        return isEmpty;
    }

    public void setData(ArrayList<Pair<Worker, ArrayList<WorkerWork>>> data){
        if (data!=null && data.size()>0){
            this.data = data;
            this.filteredData = data;
            isEmpty = false;
        }
        else{
            this.data = new ArrayList<>();
            Worker worker = new Worker(); worker.setID(-1);
            data.add(new Pair<Worker, ArrayList<WorkerWork>>(worker, null));
            this.filteredData = data;
            isEmpty = true;
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Pair<Worker, ArrayList<WorkerWork>>> data){
        if (data!=null && data.size()>0){
            for (Pair<Worker, ArrayList<WorkerWork>> pair: data){
                this.data.add(pair);
                this.filteredData = this.data;
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE){
            View view = inflater.inflate(R.layout.worker_list_cell, parent, false);
            return new WorkerListViewHolder(view);
        }
        else{
            return new EmptyListViewHolder(inflater.inflate(R.layout.empty_list_vh_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkerListViewHolder)
            ((WorkerListViewHolder)holder).display(position);
        else
            ((EmptyListViewHolder)holder).bindData(context.getString(R.string.empty_list_worker));
    }

    @Override
    public int getItemViewType(int position) {
        return filteredData.get(position).first.getID()>0?VIEW_TYPE:VIEW_TYPE_ZERO;
    }

    @Override
    public int getItemCount() {
        return filteredData==null?0:filteredData.size()  ;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                query = charSequence.toString().toLowerCase();

                if (!query.isEmpty()){
                    filteredData = new ArrayList<>();
                    int i=0;

                    while (i<data.size()){
                        String worksString = "";
                        if (data.get(i).second != null){
                            for (WorkerWork w: data.get(i).second){
                                worksString = worksString + w.getWork().toLowerCase() + "|";
                            }
                        }
                        Worker worker = data.get(i).first;

                        if (!worker.getQuartier().toLowerCase().contains(query) && !worker.getVille().toLowerCase().contains(query)
                                && !worker.getName().toLowerCase().contains(query) && !worksString.contains(query)){

                        }
                        else{
                            Pair<Worker, ArrayList<WorkerWork>> pair = new Pair<>(worker, data.get(i).second);
                            filteredData.add(pair);
                        }
                        i++;
                    }
                }
                else{
                    filteredData = data;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = new Object[]{filteredData};
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<Pair<Worker, ArrayList<WorkerWork>>>) (((Object[])filterResults.values)[0]);
                notifyDataSetChanged();
            }
        };
    }


    class WorkerListViewHolder extends RecyclerView.ViewHolder{

        private int currentPosition;

        private ImageView imageView;
        private TextView worker;
        private TextView works;
        private AppCompatRatingBar note;
        private TextView sollicitations;

        public WorkerListViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.workers_list_cell_workerimg);
            worker = (TextView) itemView.findViewById(R.id.workers_list_cell_workername);
            works = (TextView) itemView.findViewById(R.id.workers_list_cell_workerworks);
            note = (AppCompatRatingBar) itemView.findViewById(R.id.workers_list_cell_note);
            sollicitations = (TextView) itemView.findViewById(R.id.workers_list_cell_solis);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, WorkerActivity.class);
                    intent.putExtra(Tools.WORKER_INTENT_KEY, filteredData.get(currentPosition).first.getID());
                    context.startActivity(intent);
                }
            });
        }

        public void display(int position){

            currentPosition = position;
            String urlProfile = Tools.URL_PROFILE_DEFAULT;
            if (!filteredData.get(position).first.getPhoto().equals("null")){
                urlProfile = ADD+"/"+filteredData.get(position).first.getPhoto();
                //Toast.makeText(context, urlProfile, Toast.LENGTH_LONG).show();
            }


            Tools.renderRoundedProfileImage(imageView, urlProfile, context);

            worker.setText(filteredData.get(position).first.getName());

            String workerworks = "";
            for (WorkerWork workerWork: filteredData.get(position).second){
                workerworks = workerworks + "" + workerWork.getWork()+", ";
            }
            workerworks = workerworks.substring(0, workerworks.length()-2);
            works.setText(workerworks);

            note.setRating((float) filteredData.get(position).first.getNote());
            sollicitations.setText(filteredData.get(position).first.getNbr_sollicitations()+" "+context.getString(R.string.solis));
        }

    }
}
