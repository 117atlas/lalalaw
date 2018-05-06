package cm.g2i.lalalaworker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.services.Date;
import cm.g2i.lalalaworker.models.History;
import cm.g2i.lalalaworker.models.HistoryUnit;

/**
 * Created by Sim'S on 26/07/2017.
 */

public class UserHistoryAdapter extends RecyclerView.Adapter<UserHistoryAdapter.UserHistoryViewHolder> {

    private History history;
    private Context context;

    public UserHistoryAdapter(Context context){
        this.context = context;
    }

    public void setHistory(History history){
        this.history = history;
        notifyDataSetChanged();
    }

    @Override
    public UserHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == HistoryUnit.TYPE_CALL){
            return new UserHistoryViewHolder(inflater.inflate(R.layout.user_history_list_cell__call, parent, false));
        }
        else if (viewType == HistoryUnit.TYPE_SMS){
            return new UserHistoryViewHolder(inflater.inflate(R.layout.user_history_list_cell__sms, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(UserHistoryViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemViewType(int position) {
        return history.getUnits().get(position).getType();
    }

    @Override
    public int getItemCount() {
        if (history==null) return 0;
        if (history.getUnits()==null) return 0;
        return history.getUnits().size();
    }

    class UserHistoryViewHolder extends RecyclerView.ViewHolder{

        private TextView date;

        public UserHistoryViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.user_history_list_cell_date);
        }

        public void bindData(int position){
            date.setText(Date.TimeMillisdateToString(history.getUnits().get(position).getDate(), context));
        }
    }

}
