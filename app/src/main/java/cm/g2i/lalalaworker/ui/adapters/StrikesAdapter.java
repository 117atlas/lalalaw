package cm.g2i.lalalaworker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.models.Strikes;

import java.util.ArrayList;

/**
 * Created by Sim'S on 09/09/2017.
 */

public class StrikesAdapter extends RecyclerView.Adapter<StrikesAdapter.StrikeViewHolder> {

    private Context context;
    private ArrayList<Strikes> strikes;

    public StrikesAdapter(Context context){
        this.context = context;
    }

    public void setStrikes(ArrayList<Strikes> strikes){
        this.strikes = strikes;
        notifyDataSetChanged();
    }

    @Override
    public StrikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new StrikeViewHolder(inflater.inflate(R.layout.strikes_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(StrikeViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return strikes==null?0:strikes.size();
    }

    class StrikeViewHolder extends RecyclerView.ViewHolder{

        private TextView gravity;
        private TextView message;

        public StrikeViewHolder(View itemView) {
            super(itemView);
            gravity = (TextView) itemView.findViewById(R.id.strikes_vh_gravity);
            message = (TextView) itemView.findViewById(R.id.strikes_vh_message);
        }

        public void bindData(int position){
            gravity.setText(strikes.get(position).getGravity()+"");
            message.setText(strikes.get(position).getMessage());
        }
    }
}
