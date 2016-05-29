package com.example.dinok.gitstats;

/**
 * Created by dinok on 5/23/2016.
 */

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


public class OneFragment extends Fragment {

    private Integer current = 0;
    private Repository repository;
    private Type type;

    TextView tvTodayTotal;
    TextView tvTotaltext;
    TextView tvFullName;
    LineChartView chart;

    ProgressBar progressBar;
    View view;
    TextView tvDate;
    TextView tvDescription;
    TextView tvReadme;

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        DAY(0),
        MONTH(1),
        WEEK(2);

        private int id;

        Type(int number) {
            this.id = number;
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (view == null) return;
        super.setUserVisibleHint(isVisibleToUser);

        if (getType() == Type.DAY) {
            //tvTotaltext.setText("Today total");
            //progressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.DST_IN);
            MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appblue)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.appblue)));
            MainActivity.tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#007aff"));
            MainActivity.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#007aff"));
        } else if (getType() == Type.WEEK) {
            //tvTotaltext.setText("Week total");
            //progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.DST_IN);
            MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appgreen)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.appgreen)));
            MainActivity.tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#00C951"));
            MainActivity.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00C951"));
        } else if (getType() == Type.MONTH) {
            //tvTotaltext.setText("Month total");
            //progressBar.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.DST_IN);
            MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apporange)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.apporange)));
            MainActivity.tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#FFCC33"));
            MainActivity.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFCC33"));
        }
    }

    public void fillData() {
        if (repository != null && type != null) {
            tvFullName.setText(repository.getFullName());
            tvDescription.setText(repository.getDescription());
            tvTodayTotal.setText(getTotal());
            tvReadme.setText(repository.getReadme());

            if (getTotals() != null && getTotals().size() > 0) {
                int max = Collections.max(getTotals());
                double d = Double.parseDouble(getTotals() != null && getTotals().size() > 0 && getTotals().get(getCurrent()) != null ? getTotals().get(getCurrent()).toString() : "0") * 100;
                Double p = d / max;
                progressBar.setProgress(p.intValue());
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getDate());
            setDate(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH));

            drawGraph(getData());
        }
    }

    public String getTotal() {
        switch (getType()) {
            case DAY:
                tvTotaltext.setText("Today total");
                if (getRepository().getDayCommits().size() <= current)
                    return "0";
                else
                    return getRepository().getDayCommits().get(current).getTotal() != null ? getRepository().getDayCommits().get(current).getTotal().toString(): "0";
            case WEEK:
                tvTotaltext.setText("Week total");
                if (getRepository().getWeekCommits().size() <= current)
                    return "0";
                else
                    return getRepository().getWeekCommits().get(current).getTotal() != null ? getRepository().getWeekCommits().get(current).getTotal().toString(): "0";
            case MONTH:
                tvTotaltext.setText("Month total");
                if (getRepository().getMonthCommits().size() <= current)
                    return "0";
                else
                    return getRepository().getMonthCommits().get(current).getTotal() != null ? getRepository().getMonthCommits().get(current).getTotal().toString(): "0";
        }
        return "0";
    }

    public long getDate() {
        switch (getType()) {
            case DAY:
                if (getRepository().getDayCommits().size() <= current)
                    return 0;
                else
                    return getRepository().getDayCommits().get(current).getDate() != null ? getRepository().getDayCommits().get(current).getDate().getTime() : 0;
            case WEEK:
                if (getRepository().getWeekCommits().size() <= current)
                    return 0;
                else
                    return getRepository().getWeekCommits().get(current).getDate() != null ? getRepository().getWeekCommits().get(current).getDate().getTime() : 0;
            case MONTH:
                if (getRepository().getMonthCommits().size() <= current)
                    return 0;
                else
                    return getRepository().getMonthCommits().get(current).getDate() != null ? getRepository().getMonthCommits().get(current).getDate().getTime() : 0;
        }
        return 0;
    }

    public List<Integer> getTotals() {
        List<Integer> totals = new ArrayList<Integer>();
        switch (getType()) {
            case DAY:
                for (DayCommit commit : getRepository().getDayCommits())
                    totals.add(commit != null ? commit.getTotal() : 0);
                return totals;
            case WEEK:
                for (WeekCommit commit : getRepository().getWeekCommits())
                    totals.add(commit != null ? commit.getTotal() : 0);
                return totals;
            case MONTH:
                for (MonthCommit commit : getRepository().getMonthCommits())
                    totals.add(commit != null ? commit.getTotal() : 0);
                return totals;
        }
        return totals;
    }

    public List<Integer> getData() {
        List<Integer> totals = new ArrayList<Integer>();
        switch (getType()) {
            case DAY:
                if (getRepository().getDayCommits().size() > current)
                    return getRepository().getDayCommits().get(current).getHours();
            case WEEK:
                if (getRepository().getWeekCommits().size() > current)
                    return getRepository().getWeekCommits().get(current).getDays();
            case MONTH:
                if (getRepository().getMonthCommits().size() > current)
                    return getRepository().getMonthCommits().get(current).getDays();
        }
        return totals;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one, container, false);

        tvFullName = (TextView) view.findViewById(R.id.full_name);
        tvDescription = (TextView) view.findViewById(R.id.description);
        tvTodayTotal = (TextView) view.findViewById(R.id.today_total);
        tvReadme = (TextView) view.findViewById(R.id.readme);
        tvDate = (TextView) view.findViewById(R.id.date);
        tvTotaltext = (TextView) view.findViewById(R.id.total_text);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        chart = (LineChartView) view.findViewById(R.id.chart);

        this.current = 0;
        fillData();

        final GestureDetector gesture = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i("SWIPE", "onFling has been called!");
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_MAX_OFF_PATH = 250;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        if (getCurrent() == 0) setCurrent(2);
                        else setCurrent(getCurrent() - 1);
                        fillData();
                        Log.i("SWIPE", "Right to Left");
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        Log.i("SWIPE", "Left to Right");
                        if (getCurrent() == 2) setCurrent(0);
                        else setCurrent(getCurrent() + 1);
                        fillData();
                    }
                } catch (Exception e) {
                    // nothing
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return view;

    }

    public void setDate(int day, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.MONTH, month);
        switch (getType()) {
            case DAY:
                //calendar.add(Calendar.DATE, -getCurrent());
                tvDate.setText(calendar.get(Calendar.DATE) + " " + getResources().getStringArray(R.array.months)[calendar.get(Calendar.MONTH)]);
                break;
            case WEEK:
                //calendar.add(Calendar.DATE, -getCurrent() * 7);
                tvDate.setText(calendar.get(Calendar.DATE) + " " + getResources().getStringArray(R.array.months)[calendar.get(Calendar.MONTH)]);
                break;
            case MONTH:
               //calendar.add(Calendar.MONTH, -getCurrent());
                tvDate.setText(/*alendar.get(Calendar.DATE) + " " +*/getResources().getStringArray(R.array.months)[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR));
                break;
        }
    }


    public void drawGraph(List<Integer> data) {
        List<PointValue> values = new ArrayList<PointValue>();
        // List<Float> valuesY = new ArrayList<Float>();
        Axis axisX = new Axis();

        int prvi = -1, zadnji = data.size() - 1;
        boolean prvi_postoji = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) > 0 && !prvi_postoji) {
                prvi = i;
                prvi_postoji = true;
            }
            if (data.get(i) > 0 && i >= prvi)
                zadnji = i;
        }

        if (prvi > 0) {
            values.add(new PointValue(0, 0));
            values.add(new PointValue(prvi - 1, 0));
        }


        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) > 0) values.add(new PointValue(i, data.get(i)));
        }

        /**
         dodavanje redoslijedom:
         */
        if (zadnji < data.size() - 1) {
            values.add(new PointValue(zadnji + 1, 0));
            values.add(new PointValue(data.size() - 1, 0));
        }

        chart.setInteractive(false);
        Axis axisY = new Axis().setHasLines(true);

        String color = "";
        if (getType() == Type.DAY) {
            color = "#007aff";
            List<Float> axisValues = Arrays.asList(0.f, 3.f, 6.f, 9.f, 12.f, 15.f, 18.f, 21.f, 23.f);
            List<String> axisValueLabels = Arrays.asList("0", "3", "6", "9", "12", "15", "18", "21", "23");
            axisX = Axis.generateAxisFromCollection(axisValues, axisValueLabels);
            axisX.setName("Hours");
        }
        if (getType() == Type.WEEK) {
            color = "#00C951";
            List<Float> axisValues = Arrays.asList(0.f, 1.f, 2.f, 3.f, 4.f, 5.f, 6.f);
            List<String> axisValueLabels = Arrays.asList("S", "M", "T", "W", "T", "F", "S");
            axisX = Axis.generateAxisFromCollection(axisValues, axisValueLabels);
            axisX.setName("Days");
        }
        if (getType() == Type.MONTH) {
            color = "#FFCC33";
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getDate());
            calendar.add(Calendar.MONTH, -1);
            axisX.setName(getResources().getStringArray(R.array.months)[calendar.get(Calendar.MONTH)]);
        }

        Line line = new Line(values).setColor(Color.parseColor(color)).setCubic(false).setFilled(true).setAreaTransparency(15).setCubic(false);//.setHasLabels(true);//Color.BLUE

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);
        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);

        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);

        chart.setLineChartData(lineChartData);
    }


    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }
}
