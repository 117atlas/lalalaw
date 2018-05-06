package cm.g2i.lalalaworker.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.others.Tools;

public class SimpleAcountActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private AppCompatTextView name;
    private AppCompatTextView town;
    private AppCompatTextView street;
    private AppCompatButton modifyName;
    private AppCompatButton modifyTown;
    private AppCompatButton modifyStreet;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_acount);

        User u = (User) getIntent().getSerializableExtra(Tools.USER_INTENT_KEY);
        user = u==null?new User():u;
        
        initializeWidgets();
        //bindData();
    }

    @Override
    public void onStart(){
        super.onStart();
        GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask();
        getUserAsyncTask.execute();
    }

    public void initializeWidgets(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_simple_account_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.nav_simple_profile));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        name = (AppCompatTextView) findViewById(R.id.activity_simple_acount_name);
        town = (AppCompatTextView) findViewById(R.id.activity_simple_acount_town);
        street = (AppCompatTextView) findViewById(R.id.activity_simple_acount_street);
        modifyName = (AppCompatButton) findViewById(R.id.activity_simple_acount_edit_name);
        modifyTown = (AppCompatButton) findViewById(R.id.activity_simple_acount_edit_town);
        modifyStreet = (AppCompatButton) findViewById(R.id.activity_simple_acount_edit_street);

        modifyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimpleAcountActivity.this, EditUserInfosActivity.class);
                intent.putExtra(Tools.USER_INTENT_KEY, user);
                intent.putExtra(Tools.MOD_WHAT, Tools.MOD_NAME);
                startActivity(intent);
            }
        });

        modifyTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimpleAcountActivity.this, EditUserInfosActivity.class);
                intent.putExtra(Tools.USER_INTENT_KEY, user);
                intent.putExtra(Tools.MOD_WHAT, Tools.MOD_LOCALISATION);
                intent.putExtra(Tools.MOD_WHAT_LOCAL, Tools.MOD_TOWN);
                startActivity(intent);
            }
        });

        modifyStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimpleAcountActivity.this, EditUserInfosActivity.class);
                intent.putExtra(Tools.USER_INTENT_KEY, user);
                intent.putExtra(Tools.MOD_WHAT, Tools.MOD_LOCALISATION);
                intent.putExtra(Tools.MOD_WHAT_LOCAL, Tools.MOD_STREET);
                startActivity(intent);
            }
        });
    }

    public void bindData(){
        //name.setText(user.getName());
        town.setText(Settings.userTown.getValue());
        street.setText(Settings.userStreet.getValue());
    }

    class GetUserAsyncTask extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            user = LaLaLaSQLiteOperations.getUser(SimpleAcountActivity.this);
            return (user!=null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                name.setText(user.getName());
            }
            bindData();
            /*GetUserLocalsAsyncTask getUserLocalsAsyncTask = new GetUserLocalsAsyncTask();
            getUserLocalsAsyncTask.execute();*/
        }
    }

    /*class GetUserLocalsAsyncTask extends AsyncTask<Void, Void, Boolean>{
        private SettingUnit townSetting;
        private SettingUnit streetSetting;
        @Override
        protected Boolean doInBackground(Void... voids) {
            townSetting = LaLaLaSQLiteOperations.getSettingUnitByName(Settings.userTownName, SimpleAcountActivity.this);
            streetSetting = LaLaLaSQLiteOperations.getSettingUnitByName(Settings.userStreetName, SimpleAcountActivity.this);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (townSetting!=null) town.setText(townSetting.getValue());
            if (streetSetting!=null) street.setText(streetSetting.getValue());
        }
    }*/
}
