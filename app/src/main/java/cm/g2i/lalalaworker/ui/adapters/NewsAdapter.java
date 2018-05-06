package cm.g2i.lalalaworker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.models.News;

import java.util.ArrayList;

/**
 * Created by Sim'S on 09/09/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<News> newses;

    public NewsAdapter(Context context){
        this.context = context;
    }

    public void setNewses(ArrayList<News> newses){
        this.newses = newses;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType==1) return new NewsViewHolder(inflater.inflate(R.layout.news_view_holder_new, parent, false));
        else return new NewsViewHolder(inflater.inflate(R.layout.news_view_holder_old, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((NewsViewHolder)holder).bindData(position);
    }

    @Override
    public int getItemCount() {
        return newses==null?0:newses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return newses.get(position).isNew()?1:0;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder{
        protected TextView message;

        public NewsViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.news_vh_message);
        }

        public void bindData(int position){
            message.setText(newses.get(position).getMessage());
        }
    }
}
