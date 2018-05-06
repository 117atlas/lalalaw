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
import android.widget.TextView;

import cm.g2i.lalalaworker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidationDialogFragment extends DialogFragment {

    private TextView message;
    private AppCompatButton cancel;

    private static final String MESSAGE_KEY = "MESSAGE";

    public ValidationDialogFragment() {
        // Required empty public constructor
    }

    public static ValidationDialogFragment newInstance(String message){
        ValidationDialogFragment validationDialogFragment = new ValidationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_KEY, message);
        validationDialogFragment.setArguments(bundle);
        return validationDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_validation_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        message = (TextView) view.findViewById(R.id.frag_validation_dialog_message);
        cancel = (AppCompatButton) view.findViewById(R.id.frag_validation_dialog_cancel);

        message.setText(getArguments().getString(MESSAGE_KEY, ""));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
