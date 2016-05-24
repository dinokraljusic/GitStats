package com.example.dinok.gitstats;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
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
                repository.delete();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TextView getActionBarTextView() {
        TextView titleTextView = null;

        try {
            Field f = getSupportActionBar().getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(getSupportActionBar());
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return titleTextView;
    }

    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    String repo;

    public void getApiData(){
        repository = mApp.getRepoData();
        if (repository != null) {
            mApp.getReadMe(repository);
            mApp.getTotalData(repository);
            mApp.getDayCommits(repository);
            mApp.storeRepoId(repository.save());
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, Repository> {

        @Override
        protected Repository doInBackground(String... params) {
            if (mApp.getInternalRepoId() != null && mApp.getInternalRepoId() > 0) {
                repository = Repository.findById(Repository.class, mApp.getInternalRepoId());
                if (repository != null) {
                    repository.setDayCommits(DayCommit.find(DayCommit.class, "REPO_ID = ?", mApp.getInternalRepoId().toString()));
                    repository.setWeekCommits(WeekCommit.find(WeekCommit.class, "REPO_ID = ?", mApp.getInternalRepoId().toString()));
                    repository.setMonthCommits(MonthCommit.find(MonthCommit.class, "REPO_ID = ?", mApp.getInternalRepoId().toString()));
                    repository.regenerateLists();
                }
            }

            if (repository == null)
                getApiData();
            return repository;
        }

        @Override
        protected void onPostExecute(Repository repository) {
            super.onPostExecute(repository);

            if (repository != null) {

                // createFragment(dayFragment, (ArrayList<Integer>) repository.getDayCommits().get(current).getHours(), (repository.getDayCommits().get(current) != null ? repository.getDayCommits().get(current).getTotal() : 0));
                // createFragment(weekFragment, (ArrayList<Integer>) repository.getWeekCommits().get(current).getDays(), (repository.getWeekCommits().get(current) != null ? repository.getWeekCommits().get(current).getTotal() : 0));
                //createFragment(monthFragment, (ArrayList<Integer>) repository.getMonthCommits().get(current).getDays(), (repository.getMonthCommits().get(current) != null ? repository.getMonthCommits().get(current).getTotal() : 0));

                createFragments();

                viewPager = (ViewPagerNoSwipe) findViewById(R.id.viewpager);
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            }
            //Toast.makeText(PersonList.this, pl.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void createFragments() {
        dayFragment = new OneFragment();
        Bundle bundle = new Bundle();
        bundle.putString("full_name", repository.getFullName());
        bundle.putString("description", repository.getDescription());

        List<Integer> totals = new ArrayList<Integer>();
        for (DayCommit dayCommit : repository.getDayCommits())
            totals.add(dayCommit != null ? dayCommit.getTotal() : 0);

        bundle.putIntegerArrayList("totals", (ArrayList<Integer>) totals);
        bundle.putString("readme", repository.getReadme());


        bundle.putIntegerArrayList("data1", (ArrayList<Integer>) repository.getDayCommits().get(0).getHours());
        bundle.putIntegerArrayList("data2", (ArrayList<Integer>) repository.getDayCommits().get(1).getHours());
        bundle.putIntegerArrayList("data3", (ArrayList<Integer>) repository.getDayCommits().get(2).getHours());//TODO: corres

        bundle.putInt("day", repository.getDayCommits().get(0).getDate() != null ? repository.getDayCommits().get(0).getDate().getDay() : 0);
        bundle.putInt("month", repository.getDayCommits().get(0).getDate() != null ? repository.getDayCommits().get(0).getDate().getMonth() + 1 : 0);
        bundle.putInt("type", 0);
        dayFragment.setArguments(bundle);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.commit();

        weekFragment = new OneFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("full_name", repository.getFullName());
        bundle2.putString("description", repository.getDescription());

        totals = new ArrayList<Integer>();
        for (WeekCommit weekCommit : repository.getWeekCommits())
            totals.add(weekCommit != null ? weekCommit.getTotal() : 0);

        bundle2.putIntegerArrayList("totals", (ArrayList<Integer>) totals);
        bundle2.putString("readme", repository.getReadme());

        bundle2.putIntegerArrayList("data1", (ArrayList<Integer>) repository.getWeekCommits().get(0).getDays());
        bundle2.putIntegerArrayList("data2", (ArrayList<Integer>) repository.getWeekCommits().get(1).getDays());
        bundle2.putIntegerArrayList("data3", (ArrayList<Integer>) repository.getWeekCommits().get(2).getDays());//TODO: corres

        bundle2.putInt("day", repository.getDayCommits().get(0).getDate() != null ? repository.getDayCommits().get(0).getDate().getDay() : 0);
        bundle2.putInt("month", repository.getDayCommits().get(0).getDate() != null ? repository.getDayCommits().get(0).getDate().getMonth() + 1 : 0);
        bundle2.putInt("type", 1);
        weekFragment.setArguments(bundle2);

        android.support.v4.app.FragmentManager fragmentManager2 = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
        fragmentTransaction2.commit();

        monthFragment = new OneFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putString("full_name", repository.getFullName());
        bundle3.putString("description", repository.getDescription());

        totals = new ArrayList<Integer>();
        for (MonthCommit monthCommit : repository.getMonthCommits())
            totals.add(monthCommit != null ? monthCommit.getTotal() : 0);

        bundle3.putIntegerArrayList("totals", (ArrayList<Integer>) totals);
        bundle3.putString("readme", repository.getReadme());

        bundle3.putIntegerArrayList("data1", (ArrayList<Integer>) repository.getMonthCommits().get(0).getDays());
        bundle3.putIntegerArrayList("data2", (ArrayList<Integer>) repository.getMonthCommits().get(1).getDays());
        bundle3.putIntegerArrayList("data3", (ArrayList<Integer>) repository.getMonthCommits().get(2).getDays());//TODO: corres

        bundle3.putInt("day", repository.getDayCommits().get(0).getDate() != null ? repository.getDayCommits().get(0).getDate().getDay() : 0);
        bundle3.putInt("month", repository.getDayCommits().get(0).getDate() != null ? repository.getDayCommits().get(0).getDate().getMonth() + 1 : 0);
        bundle3.putInt("type", 2);
        monthFragment.setArguments(bundle3);

        android.support.v4.app.FragmentManager fragmentManager3 = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
        fragmentTransaction3.commit();

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        //ActionBar actionBar = getSupportActionBar();
        // actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appgreen)));
        //  actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //   actionBar.setCustomView(R.layout.actionbar);

        mApp = new GithubApp(this, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.CALLBACK_URL);

        current = 0;


        //****http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/*/
        toolbar = (CustomToolBar) findViewById(R.id.toolbar);
        /*TextView tvTitle = findViewById(R.id.action_ba)
        ViewPager.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.)
        tvTitle.setLayoutParams();*/


        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appblue)));//boja appbara
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//ukidamo back button
        //getSupportActionBar().setCustomView(R.layout.actionbar);

        //getActionBarTextView().setText("AADSF");

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//
        //tabLayout.getTabAt(0).setCustomView(R.layout.tab_layout);

        tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#007aff"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#007aff"));

/***/
        //viewPager = (ViewPagerNoSwipe) findViewById(R.id.viewpager);
        //setupViewPager(viewPager);//****/

        AsyncTaskRunner astr = new AsyncTaskRunner();
        astr.execute(" ");

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setDistanceToTriggerSync(100);

    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                repository.delete();
                mApp.resetStoredRepoId();
                AsyncTaskRunner astr = new AsyncTaskRunner();
                astr.execute(" ");
                swipeLayout.setRefreshing(false);
            }
        }, 4000);
    }


    private void setupViewPager(ViewPagerNoSwipe viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(dayFragment, "DAY");
        adapter.addFragment(weekFragment, "WEEK");
        adapter.addFragment(monthFragment, "Month");
        viewPager.setAdapter(adapter);
        //viewPager.scro
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
           /* if(position == 0) toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appblue)));
            if(position == 1) toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appgreen)));
            if(position == 1) toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apporange)));*/
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
