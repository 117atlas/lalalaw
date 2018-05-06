package cm.g2i.lalalaworker.ui.activities;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.settings.SettingUnit;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.fragment.ModifyServerAddFragment;
import cm.g2i.lalalaworker.ui.fragment.SetSMSTemplateFragment;

public class SettingsActivity extends AppCompatActivity implements ModifyServerAddFragment.CaptureServerAdd,
        SetSMSTemplateFragment.CaptureSMSTemplate{

    private Toolbar toolbar;

    private Spinner numberOfSearchedWrks;
    private Switch saveHistory;
    private Switch considerUserLocals;
    private TextView serverAdd; private AppCompatButton modifyServerAdd;
    private Spinner policeSize;
    private Switch activateNotifs;
    private TextView smsTemplate; private AppCompatButton modifySmsTemplate;
    private Switch anonymousSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeComponents();
        bindData();
    }

    public void initializeComponents(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_settings_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.nav_settings));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        numberOfSearchedWrks = (Spinner) findViewById(R.id.activity_settings_spinner_number_searched_wrks);
        saveHistory = (Switch) findViewById(R.id.activity_settings_switch_save_histories);
        considerUserLocals = (Switch) findViewById(R.id.activity_settings_switch_consider_user_locals);
        serverAdd = (TextView) findViewById(R.id.activity_settings_server_add);
        modifyServerAdd = (AppCompatButton) findViewById(R.id.activity_settings_modify_server_add);
        policeSize = (Spinner) findViewById(R.id.activity_settings_police_size_spinner);
        activateNotifs = (Switch) findViewById(R.id.activity_settings_notifications);
        smsTemplate = (TextView) findViewById(R.id.activity_settings_sms_template);
        modifySmsTemplate = (AppCompatButton) findViewById(R.id.activity_settings_modify_sms_template);
        anonymousSignal = (Switch) findViewById(R.id.activity_settings_anonymous_signal);

        numberOfSearchedWrks.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.numbers_searched_wrks)));
        ((ArrayAdapter<String>)numberOfSearchedWrks.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfSearchedWrks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                if (!item.equals(Settings.numberSearchedWorkers.getValue())){
                    Settings.numberSearchedWorkers.setValue(item);
                    new SetSettingAsyncTask().execute(Settings.numberSearchedWorkers);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Boolean.parseBoolean(Settings.saveHistory.getValue()) != saveHistory.isChecked()){
                    Settings.saveHistory.setValue(String.valueOf(saveHistory.isChecked()));
                    new SetSettingAsyncTask().execute(Settings.saveHistory);
                }
            }
        });

        considerUserLocals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Boolean.parseBoolean(Settings.considerUserLocals.getValue()) != considerUserLocals.isChecked()){
                    Settings.considerUserLocals.setValue(String.valueOf(considerUserLocals.isChecked()));
                    new SetSettingAsyncTask().execute(Settings.considerUserLocals);
                }
            }
        });

        modifyServerAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModifyServerAddFragment modifyServerAddFragment = ModifyServerAddFragment.newInstance(Settings.serverAdd.getValue(), SettingsActivity.this);
                FragmentManager manager = getSupportFragmentManager();
                modifyServerAddFragment.show(manager, ModifyServerAddFragment.class.getName());
            }
        });

        ArrayAdapter<String> policeSizeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.police_sizes));
        policeSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        policeSize.setAdapter(policeSizeAdapter);
        policeSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                if (!item.equals(Settings.policeSize.getValue())){
                    Settings.policeSize.setValue(item);
                    new SetSettingAsyncTask().execute(Settings.policeSize);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        activateNotifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Boolean.parseBoolean(Settings.notifications.getValue()) != activateNotifs.isChecked()){
                    Settings.notifications.setValue(String.valueOf(activateNotifs.isChecked()));
                    new SetSettingAsyncTask().execute(Settings.notifications);
                }
            }
        });

        modifySmsTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetSMSTemplateFragment setSMSTemplateFragment = SetSMSTemplateFragment.newInstance(Settings.smsTemplate.getValue(), SettingsActivity.this);
                FragmentManager manager = getSupportFragmentManager();
                setSMSTemplateFragment.show(manager, SetSMSTemplateFragment.class.getName());
            }
        });

        anonymousSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Boolean.parseBoolean(Settings.anonymousSignal.getValue()) != anonymousSignal.isChecked()){
                    Settings.anonymousSignal.setValue(String.valueOf(anonymousSignal.isChecked()));
                    new SetSettingAsyncTask().execute(Settings.anonymousSignal);
                }
            }
        });

    }

    public void bindData(){
        numberOfSearchedWrks.setSelection(Tools.indexOf(getResources().getStringArray(R.array.numbers_searched_wrks),
                Settings.numberSearchedWorkers.getValue()), true);
        saveHistory.setChecked(Boolean.parseBoolean(Settings.saveHistory.getValue()));
        serverAdd.setText(Settings.serverAdd.getValue());
        policeSize.setSelection(Tools.indexOf(getResources().getStringArray(R.array.police_sizes), Settings.policeSize.getValue()), true);
        activateNotifs.setChecked(Boolean.parseBoolean(Settings.notifications.getValue()));
        smsTemplate.setText(Settings.smsTemplate.getValue());
    }

    @Override
    public void captureServerAdd(String _serverAdd) {
        if (!Settings.serverAdd.getValue().equals(_serverAdd)){
            Settings.serverAdd.setValue(_serverAdd);
            serverAdd.setText(_serverAdd);
            new SetSettingAsyncTask().execute(Settings.serverAdd);
        }
    }

    @Override
    public void captureSMSTemplate(String templateTitle) {
        if (!Settings.smsTemplate.getValue().equals(templateTitle)){
            Settings.smsTemplate.setValue(templateTitle);
            smsTemplate.setText(templateTitle);
            new SetSettingAsyncTask().execute(Settings.smsTemplate);
        }
    }

    class SetSettingAsyncTask extends AsyncTask<SettingUnit, Void, Long>{
        @Override
        protected Long doInBackground(SettingUnit... settingUnits) {
            return LaLaLaSQLiteOperations.updateSettingUnit(settingUnits[0], SettingsActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            String feedback = "";
            if (aLong > 0) feedback = getString(R.string.set_settings_feedback_pos);
            else feedback = getString(R.string.set_settings_feedback_neg);
            Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_LONG).show();
        }
    }
}
