package cm.g2i.lalalaworker.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cm.g2i.lalalaworker.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private AppCompatButton facebook;
    private AppCompatButton twitter;
    private AppCompatButton gmail;
    private AppCompatButton website;
    private AppCompatButton license;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeComponents();
    }

    public void initializeComponents(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_about_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_about_title));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        facebook = (AppCompatButton) findViewById(R.id.activity_about_facebook);
        twitter = (AppCompatButton) findViewById(R.id.activity_about_twitter);
        gmail = (AppCompatButton) findViewById(R.id.activity_about_mail);
        website = (AppCompatButton) findViewById(R.id.activity_about_website);
        license = (AppCompatButton) findViewById(R.id.activity_about_license);

        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
        gmail.setOnClickListener(this);
        website.setOnClickListener(this);
        license.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_about_facebook:{

            } break;
            case R.id.activity_about_twitter:{

            } break;
            case R.id.activity_about_mail:{

            } break;
            case R.id.activity_about_website:{

            } break;
            case R.id.activity_about_license:{

            } break;
        }
    }
}
