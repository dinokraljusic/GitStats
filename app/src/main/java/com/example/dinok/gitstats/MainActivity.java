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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private GithubApp mApp;
    private Repository repository;
    private Integer current;

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
            while (getRepository() != null && getRepository().getDayCommits().size() == 0)//it happens that a valid request  returns empty JSON object the first time
                getApiData();
            return getRepository();
        }


        @Override
        protected void onPostExecute(Repository repository) {
            super.onPostExecute(repository);

            if (repository != null && !refreshing) {
                viewPager = (ViewPagerNoSwipe) findViewById(R.id.viewpager);
                if (getSupportFragmentManager().getFragments() == null) {
                    createFragments(repository);
                    setupViewPager(viewPager);
                }
                else {
                    refreshExisting();
                    replaceViewPager(viewPager);
                }
                tabLayout.setupWithViewPager(viewPager);
            }
            else if (repository != null && refreshing) {
                refreshExisting();
            }
            else
                Toast.makeText(MainActivity.this, "Fetching failed, try again!", Toast.LENGTH_SHORT).show();
            refreshing = false;
        }
    }

    public void refreshExisting() {
        dayFragment = (OneFragment) getSupportFragmentManager().getFragments().get(0);
        dayFragment.setRepository(repository);
        dayFragment.setType(OneFragment.Type.DAY);
        dayFragment.fillData();

        weekFragment = (OneFragment) getSupportFragmentManager().getFragments().get(1);
        weekFragment.setRepository(repository);
        weekFragment.setType(OneFragment.Type.WEEK);
        weekFragment.fillData();

        monthFragment = (OneFragment) getSupportFragmentManager().getFragments().get(2);
        monthFragment.setRepository(repository);
        monthFragment.setType(OneFragment.Type.MONTH);
        monthFragment.fillData();
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
        }, 1000);
    }


    private void setupViewPager(ViewPagerNoSwipe viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(dayFragment, "DAY");
        adapter.addFragment(weekFragment, "WEEK");
        adapter.addFragment(monthFragment, "MONTH");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void resetViewPager(ViewPagerNoSwipe viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.removeAllViews();
        adapter.removeFragment(dayFragment, "DAY");
        adapter.removeFragment(weekFragment, "WEEK");
        adapter.removeFragment(monthFragment, "MONTH");
        viewPager.setAdapter(adapter);
        dayFragment = null;
        monthFragment = null;
        weekFragment = null;
    }

    private void replaceViewPager(ViewPagerNoSwipe viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.replaceFramgent(getSupportFragmentManager().getFragments().get(0), "DAY");
        adapter.replaceFramgent(getSupportFragmentManager().getFragments().get(1), "WEEK");
        adapter.replaceFramgent(getSupportFragmentManager().getFragments().get(2), "MONTH");
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

        public void removeFragment(Fragment fragment, String title) {
            mFragmentList.remove(fragment);
            mFragmentTitleList.remove(title);
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        public void replaceFramgent(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            getSupportFragmentManager().beginTransaction().detach(fragment).commit();
            getSupportFragmentManager().beginTransaction().attach(fragment).commit();
           // getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, fragment, fragment.getTag()).commit();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
