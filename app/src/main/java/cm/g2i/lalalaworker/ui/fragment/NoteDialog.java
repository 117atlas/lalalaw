package cm.g2i.lalalaworker.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cm.g2i.lalalaworker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteDialog extends DialogFragment {

    private AppCompatRatingBar note;
    private AppCompatButton noteBtn;

    public interface NoteListener {
        public void getNote(double _note);
    }

    public NoteDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_note_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        note = (AppCompatRatingBar) view.findViewById(R.id.frag_note_dialog_note);
        noteBtn = (AppCompatButton) view.findViewById(R.id.frag_note_dialog_note_btn);
        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double _note = note.getRating();
                NoteListener noteListener = (NoteListener) getActivity();
                noteListener.getNote(_note);
                dismiss();
            }
        });
    }

}
