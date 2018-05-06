package cm.g2i.lalalaworker.ui.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.History;
import cm.g2i.lalalaworker.others.CircleTransform;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.adapters.WorkerListAdapter;
import cm.g2i.lalalaworker.ui.fragment.AccountFragment;
import cm.g2i.lalalaworker.ui.fragment.HistoryFragment;
import cm.g2i.lalalaworker.ui.fragment.ReactivateAccFragment;
import cm.g2i.lalalaworker.ui.fragment.ReportProblemFragment;
import cm.g2i.lalalaworker.ui.fragment.SearchFragment;
import cm.g2i.lalalaworker.ui.fragment.SortQueryDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private long time1 = 0, time2 = 0;
    private int currentFragment = 1;
    private Menu appMenu = null;

    private HistoryFragment historyFragment;
    private SearchFragment searchFragment;
    private AccountFragment accountFragment;

    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;

    private SearchView searchView;

    private View navHeader;
    private DrawerLayout main;
    private NavigationView navigationView;
    private ImageView navProfileImg;
    private ImageView navHeaderBg;
    private TextView navProfileName;
    private TextView navProfilePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewPager(savedInstanceState);
        setupNavigationView(savedInstanceState);
    }

    public void setupViewPager(Bundle savedInstanceState){
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        historyFragment = new HistoryFragment();
        searchFragment = new SearchFragment();
        accountFragment = new AccountFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(historyFragment, getResources().getString(R.string.tab_history));
        adapter.addFragment(searchFragment, getResources().getString(R.string.tab_search));
        adapter.addFragment(accountFragment, getResources().getString(R.string.tab_account));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0 && historyFragment==null) historyFragment = new HistoryFragment();
                if (position==1 && searchFragment==null) searchFragment = new SearchFragment();
                if (position==2 && accountFragment==null) accountFragment = new AccountFragment();
                currentFragment = position+1;
                //enableDisableMenu(appMenu);
                /*if (position!=1){
                    getSupportActionBar().hide();
                }
                else{
                    getSupportActionBar().show();
                }*/
                onCreateOptionsMenu(appMenu);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //viewPager.setCurrentItem(1, false);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentListTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentListTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return fragmentListTitles.get(position);
        }
    }

    /**
     *
     */
    public void setupNavigationView(Bundle savedInstanceState){
        main = (DrawerLayout) findViewById(R.id.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        navProfileImg = (ImageView) navHeader.findViewById(R.id.drawer_image_profile);
        //navHeaderBg = (ImageView) navHeader.findViewById(R.id.drawer_header_bg);
        navProfileName = (TextView) navHeader.findViewById(R.id.drawer_name);
        navProfilePhone = (TextView) navHeader.findViewById(R.id.drawer_num);

        /*Glide.with(this).load("file:///android_asset/bg.jpg")
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(navHeaderBg);*/

        Glide.with(this).load("file:///android_asset/lalalalogo.png")
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(navProfileImg);
        navProfileName.setText(getString(R.string.app_name));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.simple_account: {
                        //Toast.makeText(getApplicationContext(), "Account", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, SimpleAcountActivity.class));
                    } break;
                    case R.id.settings: {
                        //Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    } break;
                    case R.id.share: {
                        //Toast.makeText(getApplicationContext(), "Share", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message));
                        startActivity(Intent.createChooser(intent, getString(R.string.nav_share_app)));
                    } break;
                    case R.id.help: {
                        //Toast.makeText(getApplicationContext(), "Help", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HelpActivity.class));
                    } break;
                    case R.id.about: {
                        //Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    } break;
                    case R.id.reportproblem:{
                        FragmentManager manager = getSupportFragmentManager();
                        ReportProblemFragment reportProblemFragment = new ReportProblemFragment();
                        reportProblemFragment.show(manager, ReportProblemFragment.class.getName());
                    }
                }
                main.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, main, toolbar, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                Toast.makeText(getApplicationContext(), "CloseDrawer", Toast.LENGTH_SHORT).show();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                Toast.makeText(getApplicationContext(), "Open Drawer", Toast.LENGTH_SHORT).show();
                super.onDrawerOpened(drawerView);
            }
        };
        main.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        if (savedInstanceState == null){

        }
    }

    @Override
    public void onBackPressed(){
        /*time2 = time1;
        time1 = SystemClock.currentThreadTimeMillis();
        if (time1-time2 > 1000){
            Toast.makeText(getApplicationContext(), R.string.closeapp, Toast.LENGTH_SHORT).show();
        }
        else{
            onDestroy();
        }*/
        super.onBackPressed();
    }

    public void enableDisableMenu(Menu menu){
        if (menu != null){
            if (currentFragment == 2) {
                for(int i=0; i<menu.size(); i++){
                    menu.getItem(i).setEnabled(true);
                }
            }
            else {
                for(int i=0; i<menu.size(); i++){
                    menu.getItem(i).setEnabled(false);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        appMenu = menu;
        appMenu.clear();
        menu.clear();
        if (currentFragment==2){
            getMenuInflater().inflate(R.menu.main_menu, menu);
            searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.app_bar_search));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!((WorkerListAdapter)searchFragment.getRecyclerView().getAdapter()).isEmpty())
                        ((WorkerListAdapter)searchFragment.getRecyclerView().getAdapter()).getFilter().filter(newText);
                    return true;
                }
            });
        }
        else if (currentFragment==1){
            getMenuInflater().inflate(R.menu.main_menu_history, menu);
        }
        else if (currentFragment==3){
            if (Settings.worker!=null)
            getMenuInflater().inflate(R.menu.main_menu_account, menu);
        }
        //enableDisableMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.app_bar_search: Toast.makeText(this, "APP BAR SEARCH", Toast.LENGTH_SHORT).show(); break;
            case R.id.app_bar_sort: {
                Toast.makeText(this, "DEEP RSRCH", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getSupportFragmentManager();
                SortQueryDialog sortQueryDialog = SortQueryDialog.newInstance(searchFragment.getSortQuery());
                sortQueryDialog.setTargetFragment(searchFragment, 300);
                sortQueryDialog.show(fragmentManager, "SortQueryDialog");
            } break;
            case R.id.app_bar_clear: {
                AppCompatDialogFragment appCompatDialogFragment = new AppCompatDialogFragment();
                new FullDeleteHistoryAsyncTask().execute();
            } break;
            case R.id.app_bar_account_news: {
                startActivity(new Intent(MainActivity.this, NewsActivity.class));
            } break;
            case R.id.app_bar_account_settings: {
                startActivity(new Intent(MainActivity.this, WorkerAccSettingsActivity.class));
            } break;
            case R.id.app_bar_myworks: {
                Intent intent = new Intent(MainActivity.this, WorkerWorksActivity.class);
                intent.putExtra(Tools.WORKER_INTENT_KEY, Settings.worker.getID());
                startActivity(intent);
            } break;
            case R.id.app_bar_mycomments: {
                Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                intent.putExtra(AccountManager.class.getName(), true);
                intent.putExtra(Tools.WORKER_INTENT_KEY, Settings.worker);
                startActivity(intent);
            } break;
            case R.id.app_bar_mystrikes: {
                Intent intent = new Intent(MainActivity.this, StrikesActivity.class);
                intent.putExtra(Tools.WORKER_INTENT_KEY, Settings.worker);
                startActivity(intent);
            } break;
        }
        return super.onOptionsItemSelected(item);
    }

    class FullDeleteHistoryAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            LaLaLaSQLiteOperations.fulldeleteHistory(getApplicationContext());
            for (History history: historyFragment.getHistories()){
                if (history!=null){
                    LaLaLaSQLiteOperations.deleteWorkerInfos(history.getUnits().get(0).getWorker().getID(), getApplicationContext());
                    if (history.getUnits().get(0).getWorker().getlocalPhoto()!=null ||
                            !history.getUnits().get(0).getWorker().getlocalPhoto().equals("null")){
                        File file = new File(history.getUnits().get(0).getWorker().getlocalPhoto());
                        if (file.exists()) file.delete();
                    }
                }
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
            historyFragment.executeTask();
        }
    }
}
