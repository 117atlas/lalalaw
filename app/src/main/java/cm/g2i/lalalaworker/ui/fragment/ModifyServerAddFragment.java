package cm.g2i.lalalaworker.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.settings.Settings;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModifyServerAddFragment extends DialogFragment {

    private EditText serverAdd;
    private AppCompatButton apply;
    private AppCompatButton cancel;

    public interface CaptureServerAdd{
        public void captureServerAdd(String serverAdd);
    }

    private CaptureServerAdd captureServerAdd;
    private String oldServerAdd;

    public ModifyServerAddFragment() {
        // Required empty public constructor
    }

    public static ModifyServerAddFragment newInstance(String serverAdd, CaptureServerAdd captureServerAdd){
        ModifyServerAddFragment modifyServerAddFragment = new ModifyServerAddFragment();
        Bundle args = new Bundle();
        args.putString(Settings.serverAddName, serverAdd);
        modifyServerAddFragment.setArguments(args);
        modifyServerAddFragment.captureServerAdd = captureServerAdd;
        return modifyServerAddFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify_server_add, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        serverAdd = (EditText) view.findViewById(R.id.frag_modify_server_add_server_add);
        apply = (AppCompatButton) view.findViewById(R.id.frag_modify_server_add_ok);
        cancel = (AppCompatButton) view.findViewById(R.id.frag_modify_server_add_cancel);

        oldServerAdd = getArguments().getString(Settings.serverAddName)==null?"10.0.2.2":getArguments().getString(Settings.serverAddName);
        serverAdd.setText(oldServerAdd);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newServerAdd = serverAdd.getText().toString();
                if (!newServerAdd.equals(oldServerAdd)){
                    captureServerAdd.captureServerAdd(newServerAdd);
                }
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
