package cm.g2i.lalalaworker.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;

/**
 * Created by Sim'S on 12/08/2017.
 */

public class EmptyListViewHolder extends RecyclerView.ViewHolder {
    private TextView message;
    public EmptyListViewHolder(View itemView) {
        super(itemView);
        message = (TextView) itemView.findViewById(R.id.empty_list_message);
    }

    public void bindData(String _message){
        message.setText(_message);
    }
}
