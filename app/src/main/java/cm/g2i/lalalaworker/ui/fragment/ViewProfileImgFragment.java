package cm.g2i.lalalaworker.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.others.Tools;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewProfileImgFragment extends AppCompatDialogFragment {

    private ImageView profile;
    private String profileUrl;

    public ViewProfileImgFragment() {
        // Required empty public constructor
    }

    public static ViewProfileImgFragment newInstance(String url){
        Bundle args = new Bundle();
        args.putString("ProfileURL", url);
        ViewProfileImgFragment viewProfileImgFragment = new ViewProfileImgFragment();
        viewProfileImgFragment.setArguments(args);
        return viewProfileImgFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_profile_img, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        profileUrl = getArguments().getString("ProfileURL");
        profileUrl = profileUrl==null?Tools.URL_PROFILE_DEFAULT:profileUrl;
        this.setCancelable(true);

        profile = (ImageView) view.findViewById(R.id.frag_view_profile_img_profile);
        Tools.renderProfileImage(profile, profileUrl, getContext());
    }

}
