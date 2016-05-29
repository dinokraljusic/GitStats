package com.example.dinok.gitstats;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


//import android.support.v4.app.FragmentManager;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private GithubApp mApp;
    private Repository repository;
    private Integer current;

    //*****/
    public static CustomToolBar toolbar;
    public static TabLayout tabLayout;
    private ViewPagerNoSwipe viewPager;
    private SwipeRefreshLayout swipeLayout;

    private OneFragment dayFragment;
    private OneFragment weekFragment;
    private OneFragment monthFragment;

    private boolean refreshing = false;
    public static int tries = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //TODO add edit settings activity
                mApp.resetAccessToken();
                if (getRepository() != null)
                    getRepository().delete();
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*ViewPager mViewPager;
    String repo;*/

    public void getApiData() {
        setRepository(mApp.getRepoData());
        if (getRepository() != null) {
            mApp.getReadMe(getRepository());
            mApp.getTotalData(getRepository());
            mApp.getDayCommits(getRepository());
            mApp.storeRepoId(getRepository().save());
        }
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, Repository> {

        @Override
        protected Repository doInBackground(String... params) {
            if (mApp.getInternalRepoId() != null && mApp.getInternalRepoId() > 0) {
                setRepository(Repository.findById(Repository.class, mApp.getInternalRepoId()));
                if (getRepository() != null) {
                    getRepository().setDayCommits(DayCommit.find(DayCommit.class, "REPO_ID = ?", mApp.getInternalRepoId().toString()));
                    getRepository().setWeekCommits(WeekCommit.find(WeekCommit.class, "REPO_ID = ?", mApp.getInternalRepoId().toString()));
                    getRepository().setMonthCommits(MonthCommit.find(MonthCommit.class, "REPO_ID = ?", mApp.getInternalRepoId().toString()));
                    getRepository().regenerateLists();
                }
            }

            if (getRepository() == null)
                getApiData();
            while (getRepository() != null && getRepository().getDayCommits().size() == 0)
                getApiData();
            return getRepository();
        }


        @Override
        protected void onPostExecute(Repository repository) {
            super.onPostExecute(repository);
            if (repository != null && !refreshing) {

                viewPager = (ViewPagerNoSwipe) findViewById(R.id.viewpager);
                createFragments(repository);
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            } else if (repository != null && refreshing) {
                dayFragment = (OneFragment) getSupportFragmentManager().getFragments().get(0);
                dayFragment.setRepository(repository);
                dayFragment.fillData();

                weekFragment = (OneFragment) getSupportFragmentManager().getFragments().get(1);
                weekFragment.setRepository(repository);
                weekFragment.fillData();

                monthFragment = (OneFragment) getSupportFragmentManager().getFragments().get(2);
                monthFragment.setRepository(repository);
                monthFragment.fillData();
            } else
                Toast.makeText(MainActivity.this, "Fetching failed, try again!", Toast.LENGTH_SHORT).show();
            //return;
            refreshing = false;
        }
    }

    public void createFragments(Repository repository) {
        dayFragment = new OneFragment();
        dayFragment.setRepository(repository);
        dayFragment.setType(OneFragment.Type.DAY);

        weekFragment = new OneFragment();
        weekFragment.setRepository(repository);
        weekFragment.setType(OneFragment.Type.WEEK);

        monthFragment = new OneFragment();
        monthFragment.setRepository(repository);
        monthFragment.setType(OneFragment.Type.MONTH);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        /*Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getBaseContext().getResources().getColor(R.color.appblue));*/


        mApp = new GithubApp(this, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.CALLBACK_URL);

        current = 0;

        toolbar = (CustomToolBar) findViewById(R.id.toolbar);


        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appblue)));//boja appbara
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//ukidamo back button

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//

        tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#007aff"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#007aff"));

/****/
        AsyncTaskRunner astr = new AsyncTaskRunner();
        astr.execute(" ");

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setDistanceToTriggerSync(180);

    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getRepository() != null)
                    getRepository().delete();
                mApp.resetStoredRepoId();
                setRepository(null);
                refreshing = true;
                tries = 0;
                AsyncTaskRunner astr = new AsyncTaskRunner();
                astr.execute(" ");
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }


    private void setupViewPager(ViewPagerNoSwipe viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(dayFragment, "DAY");
        adapter.addFragment(weekFragment, "WEEK");
        adapter.addFragment(monthFragment, "MONTH");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            getSupportFragmentManager().beginTransaction().attach(fragment).commit();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
