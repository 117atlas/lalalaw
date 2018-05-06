package cm.g2i.lalalaworker.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.services.Date;
import cm.g2i.lalalaworker.models.History;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.activities.HistoryActivity;

import java.util.ArrayList;

/**
 * Created by Sim'S on 25/07/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<History> histories;
    private Context context;

    public HistoryAdapter(Context context){
        this.context = context;
    }

    public void setHistories(ArrayList<History> list){
        histories = list;
        notifyDataSetChanged();
    }

    public ArrayList<History> getHistories(){return histories;}

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new HistoryViewHolder(inflater.inflate(R.layout.history_list_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return histories==null?0:histories.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder{

        private ImageView profile;
        private TextView name;
        private TextView details;
        private TextView lastDate;
        private AppCompatButton call;
        private AppCompatButton sms;

        private int currentposition;

        public HistoryViewHolder(View itemView) {
            super(itemView);

            profile = (ImageView) itemView.findViewById(R.id.history_list_cell_profile);
            name = (TextView) itemView.findViewById(R.id.history_list_cell_name);
            details = (TextView) itemView.findViewById(R.id.history_list_cell_details);
            lastDate = (TextView) itemView.findViewById(R.id.history_list_cell_lastdate);
            call = (AppCompatButton) itemView.findViewById(R.id.history_list_cell_phonecall);
            sms = (AppCompatButton) itemView.findViewById(R.id.history_list_cell_phonesms);

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, HistoryActivity.class);
                    intent.putExtra("History", histories.get(currentposition));
                    context.startActivity(intent);
                }
            });
        }

        public void bindData(int position){
            currentposition = position;
            name.setText(histories.get(position).getUnits().get(0).getWorker().getName());
            details.setText(histories.get(position).details(context.getString(R.string.call_label), context.getString(R.string.sms_label)));
            lastDate.setText(Date.TimeMillisdateToString(""+histories.get(position).lastDate(), context));

            System.out.println("Position: "+ position+ ":: "+histories.get(position).getUnits().get(0).getWorker().getlocalPhoto());

            Tools.renderProfileImage(profile,
                    histories.get(position).getUnits().get(0).getWorker().getlocalPhoto()==null ||
                    histories.get(position).getUnits().get(0).getWorker().getlocalPhoto().equals("null") ?
                        Tools.URL_PROFILE_DEFAULT:histories.get(position).getUnits().get(0).getWorker().getlocalPhoto(),
                    context);
        }
    }
}
