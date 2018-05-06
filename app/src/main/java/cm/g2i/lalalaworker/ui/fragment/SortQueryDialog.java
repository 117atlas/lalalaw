package cm.g2i.lalalaworker.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cm.g2i.lalalaworker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SortQueryDialog extends DialogFragment {

    private AppCompatRadioButton notes;
    private AppCompatRadioButton sollicitations;
    private AppCompatRadioButton nothing;
    private AppCompatButton ok;
    private AppCompatButton cancel;

    interface OnSortQueryListener{
        public void getSortBy(String sortBy);
    }

    public SortQueryDialog() {
        // Required empty public constructor
    }

    public static SortQueryDialog newInstance(String item){
        SortQueryDialog sortQueryDialog = new SortQueryDialog();
        Bundle bundle = new Bundle();

        int itemChecked = 0;
        if (item.equals("notes")) itemChecked = 1;
        else if (item.equals("sollicitations")) itemChecked = 2;

        bundle.putInt("ItemChecked", itemChecked);
        sortQueryDialog.setArguments(bundle);
        return sortQueryDialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_sort_query_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        notes = (AppCompatRadioButton) view.findViewById(R.id.sort_dialog_1);
        sollicitations = (AppCompatRadioButton) view.findViewById(R.id.sort_dialog_2);
        nothing = (AppCompatRadioButton) view.findViewById(R.id.sort_dialog_0);
        ok = (AppCompatButton) view.findViewById(R.id.sort_dialog_ok);
        cancel = (AppCompatButton) view.findViewById(R.id.sort_dialog_cancel);

        if (getArguments()==null) nothing.setChecked(true);
        else{
            switch (getArguments().getInt("ItemChecked")){
                case 0: nothing.setChecked(true); break;
                case 1: notes.setChecked(true); break;
                case 2: sollicitations.setChecked(true); break;
            }
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSortQuery();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void sendSortQuery(){
        OnSortQueryListener onSortQueryListener = (OnSortQueryListener) getTargetFragment();
        if (nothing.isChecked()){
            onSortQueryListener.getSortBy("nothing");
        }
        else if(notes.isChecked()){
            onSortQueryListener.getSortBy("notes");
        }
        else if(sollicitations.isChecked()){
            onSortQueryListener.getSortBy("sollicitations");
        }
        dismiss();
    }
}
