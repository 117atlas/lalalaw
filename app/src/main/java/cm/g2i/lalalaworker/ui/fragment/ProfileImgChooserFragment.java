package cm.g2i.lalalaworker.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileImgChooserFragment extends DialogFragment {

    public static final int GALLERY_CHOOSE = 1;
    public static final int TAKE_PHOTO_CHOOSE = 2;

    public interface ChooseDone{
        public void getChoise(int choose);
    }

    private ChooseDone chooseDone;

    public void setChooseDone(ChooseDone chooseDone){
        this.chooseDone = chooseDone;
    }

    private TextView gallery;
    private TextView takePhoto;

    public ProfileImgChooserFragment() {
        // Required empty public constructor
    }

    public static ProfileImgChooserFragment newInstance(ChooseDone chooseDone){
        ProfileImgChooserFragment profileImgChooserFragment = new ProfileImgChooserFragment();
        profileImgChooserFragment.setChooseDone(chooseDone);
        return profileImgChooserFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_profile_img_chooser, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gallery = (TextView) view.findViewById(R.id.frag_profile_img_chooser_gallery);
        takePhoto = (TextView) view.findViewById(R.id.frag_profile_img_chooser_app_photo);

        gallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gallery.setBackgroundColor(Color.YELLOW);
                return false;
            }
        });
        takePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takePhoto.setBackgroundColor(Color.YELLOW);
                return false;
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performChoose(GALLERY_CHOOSE);
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performChoose(TAKE_PHOTO_CHOOSE);
            }
        });
    }

    public void performChoose(int choose){
        if (getTargetFragment()!=null){
            ((ChooseDone)getTargetFragment()).getChoise(choose);
            dismiss();
            return;
        }
        chooseDone.getChoise(choose);
        dismiss();
    }
}
