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
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.others.Tools;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetSMSTemplateFragment extends DialogFragment {

    private AppCompatButton previous;
    private AppCompatButton next;
    private TextView templateTitle;
    private TextView template;
    private AppCompatButton apply;
    private AppCompatButton cancel;

    public SetSMSTemplateFragment() {
        // Required empty public constructor
    }

    public interface CaptureSMSTemplate{
        void captureSMSTemplate(String templateTitle);
    }

    private CaptureSMSTemplate captureSMSTemplate;
    private String currentSMSTemplate;
    private int currentIndex;

    private String[] templateTitles;
    private String[] templates;

    public static SetSMSTemplateFragment newInstance(String templateTitle, CaptureSMSTemplate captureSMSTemplate){
        SetSMSTemplateFragment setSMSTemplateFragment = new SetSMSTemplateFragment();
        Bundle args = new Bundle();
        args.putString(Settings.smsTemplateName, templateTitle);
        setSMSTemplateFragment.setArguments(args);
        setSMSTemplateFragment.captureSMSTemplate = captureSMSTemplate;
        return setSMSTemplateFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_smstemplate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        templateTitles = getContext().getResources().getStringArray(R.array.sms_template_titles);
        templates = getContext().getResources().getStringArray(R.array.sms_templates);
        currentSMSTemplate = getArguments().getString(Settings.smsTemplateName)==null?"Template 1":getArguments().getString(Settings.smsTemplateName);
        currentIndex = Tools.indexOf(templateTitles, currentSMSTemplate);

        previous = (AppCompatButton) view.findViewById(R.id.frag_modify_sms_template_previous);
        next = (AppCompatButton) view.findViewById(R.id.frag_modify_sms_template_next);
        templateTitle  = (TextView) view.findViewById(R.id.frag_modify_sms_template_title);
        template = (TextView) view.findViewById(R.id.frag_set_sms_template_sms_template);
        apply = (AppCompatButton) view.findViewById(R.id.frag_modify_sms_template_ok);
        cancel = (AppCompatButton) view.findViewById(R.id.frag_modify_sms_template_cancel);

        attachTemplateAccordingIndex();

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex--;
                attachTemplateAccordingIndex();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex++;
                attachTemplateAccordingIndex();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String oldSMSTemplate = getArguments().getString(Settings.smsTemplateName)==null?"Template 1":getArguments().getString(Settings.smsTemplateName);
                if (!oldSMSTemplate.equals(currentSMSTemplate)){
                    captureSMSTemplate.captureSMSTemplate(currentSMSTemplate);
                }
                dismiss();
            }
        });

    }

    public void manageNavigationButtons(){
        if (currentIndex==0) previous.setEnabled(false);
        else previous.setEnabled(true);
        if (currentIndex==3) next.setEnabled(false);
        else next.setEnabled(true);
    }

    public void attachTemplateAccordingIndex(){
        manageNavigationButtons();
        currentSMSTemplate = templateTitles[currentIndex];
        templateTitle.setText(currentSMSTemplate);
        template.setText(templates[currentIndex]);
    }

}
