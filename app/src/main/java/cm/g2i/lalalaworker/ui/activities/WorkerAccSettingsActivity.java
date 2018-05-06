package cm.g2i.lalalaworker.ui.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.fragment.ReactivateAccFragment;

public class WorkerAccSettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView accountRenewalDate;
    private TextView accountExpirationDate;
    private TextView validityTime;
    private TextView remainingTime;
    private AppCompatButton reactivateAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_acc_settings);
        initializeComponents();
        bindData();
    }

    public void initializeComponents(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_wkrs_settings_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.activity_worker_settings_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        accountRenewalDate = (TextView) findViewById(R.id.account_renewal_date);
        accountExpirationDate = (TextView) findViewById(R.id.account_expiration_date);
        validityTime = (TextView) findViewById(R.id.account_validity_time);
        remainingTime = (TextView) findViewById(R.id.account_remaining_time);
        reactivateAcc = (AppCompatButton) findViewById(R.id.activity_worker_settings_reactivateacc);
        reactivateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                ReactivateAccFragment reactivateAccFragment = ReactivateAccFragment.newInstance(Settings.worker);
                reactivateAccFragment.show(manager, ReactivateAccFragment.class.getName());
            }
        });
    }

    public void bindData(){
        String renewalDate = Settings.accountRenewalDate.getValue();
        String expirationDate = Settings.accountExpirationDate.getValue();
        accountRenewalDate.setText(Tools.writeDate(renewalDate, this));
        accountExpirationDate.setText(Tools.writeDate(expirationDate, this));
        validityTime.setText(Tools.dateDifference(expirationDate, renewalDate, this));
        remainingTime.setText(Settings.remainingTime.getValue() + " " + getString(R.string.month_label));

        if (Integer.parseInt(Settings.remainingTime.getValue())>0){
            reactivateAcc.setText(getString(R.string.frag_reactivate_acc_ext));
        }
    }
}
