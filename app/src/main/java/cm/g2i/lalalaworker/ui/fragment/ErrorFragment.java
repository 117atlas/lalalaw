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
public class ErrorFragment extends DialogFragment {

    private TextView message;
    private AppCompatButton cancel;
    private AppCompatButton retry;

    private boolean hideRetryButton;

    private Retry _retry;

    public void set_retry(Retry _retry){
        this._retry = _retry;
    }

    public ErrorFragment() {
        // Required empty public constructor
    }

    public interface Retry {
        public void retry();
    }

    public static ErrorFragment newInstance(String message){
        ErrorFragment errorFragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(ErrorFragment.class.getName(), message);
        errorFragment.setArguments(args);
        return errorFragment;
    }

    public static ErrorFragment newInstance(String message, Retry _retry){
        ErrorFragment errorFragment = new ErrorFragment();
        errorFragment.set_retry(_retry);
        Bundle args = new Bundle();
        args.putString(ErrorFragment.class.getName(), message);
        errorFragment.setArguments(args);
        return errorFragment;
    }

    public static ErrorFragment newInstance(String message, Retry _retry, boolean hideRetryButton){
        ErrorFragment errorFragment = newInstance(message, _retry);
        errorFragment.hideRetryButton = hideRetryButton;
        return errorFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_sign_in_error, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        message = (TextView) view.findViewById(R.id.frag_sign_in_error_message);
        cancel = (AppCompatButton) view.findViewById(R.id.frag_sign_in_error_cancel);
        retry = (AppCompatButton) view.findViewById(R.id.frag_sign_in_error_retry);

        message.setText(getArguments().getString(ErrorFragment.class.getName()));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getTargetFragment()!=null) ((Retry)getTargetFragment()).retry();
                if (_retry!=null) _retry.retry();
                dismiss();
            }
        });

        if (hideRetryButton) retry.setVisibility(View.INVISIBLE);
        else retry.setVisibility(View.VISIBLE);
    }

}
