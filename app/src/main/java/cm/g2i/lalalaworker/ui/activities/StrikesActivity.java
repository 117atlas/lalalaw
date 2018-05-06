package cm.g2i.lalalaworker.ui.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.models.Strikes;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.adapters.StrikesAdapter;

public class StrikesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView gravityLevel;
    private ProgressBar gravity;
    private AppCompatButton gravityDetails;
    private RecyclerView strikes;

    private ArrayList<Strikes> strikesList;
    private ArrayList<Strikes> strikesListInNet;
    private ArrayList<Strikes> strikesListInLocal;
    private Worker worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strikes);

        worker = (Worker) getIntent().getSerializableExtra(Tools.WORKER_INTENT_KEY);
        initializeComponents();
        new GetStrikesAsyncTask().execute();
    }

    public void initializeComponents(){
        toolbar = (Toolbar) findViewById(R.id.activity_strikes_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.frag_account_strikes));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        gravityLevel = (TextView) findViewById(R.id.activity_strikes_gravity_level_label);
        gravity = (ProgressBar) findViewById(R.id.activity_strikes_gravity_level);
        gravityDetails = (AppCompatButton) findViewById(R.id.activity_strikes_gravity_details);
        strikes = (RecyclerView) findViewById(R.id.activity_strikes_strikes);

        strikes.setLayoutManager(new LinearLayoutManager(this));
        strikes.setAdapter(new StrikesAdapter(getApplicationContext()));
    }

    class GetStrikesAsyncTask extends AsyncTask<Void, Void, Void>{
        private Exception exception;
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                strikesListInLocal = LaLaLaSQLiteOperations.getStrikes(getApplicationContext());
                strikesListInNet = WorkerController.getStrikes(worker.getID());
                if (strikesListInNet!= null) LaLaLaSQLiteOperations.insertStrikes(strikesListInNet, getApplicationContext());
                strikesListInLocal = LaLaLaSQLiteOperations.getStrikes(getApplicationContext());
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            strikesList = strikesListInLocal;
            ((StrikesAdapter)strikes.getAdapter()).setStrikes(strikesList);
            setGravityPanel();
        }
    }

    public void setGravityPanel(){
        int _gravity = 0;
        if (strikesList!=null){
            for (Strikes _strikes: strikesList){
                _gravity += _strikes.getGravity();
            }
        }
        int clr = Integer.parseInt(String.valueOf(Math.round(2.55*_gravity)));
        gravity.setProgress(_gravity);
        gravity.getProgressDrawable().setColorFilter(Color.argb(255, clr, 255-clr, 0), PorterDuff.Mode.SRC_IN);
        //setProgressColor(clr);
        gravityLevel.setText(getString(R.string.activity_strike_gravity_level_label) + " " + (_gravity%6));
    }

    public void setProgressColor(int clr){
        String color = colorDecToHex(clr, 255-clr, 0);
        final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners,     null, null));
        // Sets the progressBar color
        pgDrawable.getPaint().setColor(Color.parseColor(color));
        // Adds the drawable to your progressBar
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        gravity.setProgressDrawable(progress);
    }

    public static String colorDecToHex(int p_red, int p_green, int p_blue)
    {
        String red = Integer.toHexString(p_red);
        String green = Integer.toHexString(p_green);
        String blue = Integer.toHexString(p_blue);

        if (red.length() == 1)
        {
            red = "0" + red;
        }
        if (green.length() == 1)
        {
            green = "0" + green;
        }
        if (blue.length() == 1)
        {
            blue = "0" + blue;
        }

        String colorHex = "#" + red + green + blue;
        return colorHex;
    }
}
