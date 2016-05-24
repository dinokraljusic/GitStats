package com.example.dinok.gitstats;

/**
 * Created by dinok on 5/23/2016.
 */

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


public class OneFragment extends Fragment  {

    private Integer current = 0;
    TextView tvTodayTotal;
    private List<Integer> totals;
    private ArrayList<Integer> data1;
    private ArrayList<Integer> data2;
    private ArrayList<Integer> data3;
    LineChartView chart;

    int type, day, month;//0-day, 1-week, 2-month
    ProgressBar progressBar;
    View view;
    TextView tvDate;
    TextView tvDescription;
    TextView tvReadme;

    /*public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (view == null) return;
        super.setUserVisibleHint(isVisibleToUser);

        if (type == 0) {
            //tvTotaltext.setText("Today total");
            //progressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.DST_IN);
            MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appblue)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.appblue)));
            MainActivity.tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#007aff"));
            MainActivity.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#007aff"));
        } else if (type == 1) {
            //tvTotaltext.setText("Week total");
            //progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.DST_IN);
            MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appgreen)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.appgreen)));
            MainActivity.tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#00C951"));
            MainActivity.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00C951"));
        } else if (type == 2) {
            //tvTotaltext.setText("Month total");
            //progressBar.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.DST_IN);
            MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apporange)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.apporange)));
            MainActivity.tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#FFCC33"));
            MainActivity.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFCC33"));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String fullname = this.getArguments().getString("full_name");
        String description = this.getArguments().getString("description");
        setTotals(this.getArguments().getIntegerArrayList("totals"));
        String readme = this.getArguments().getString("readme");

        type = this.getArguments().getInt("type");
        day = this.getArguments().getInt("day");
        month = this.getArguments().getInt("month");


        view = inflater.inflate(R.layout.fragment_one, container, false);
        TextView tvFullName = (TextView) view.findViewById(R.id.full_name);
        //listMusic.setAdapter(new MusicBaseAdapter(getActivity(), listMusics));
        tvFullName.setText(fullname);
        tvDescription = (TextView) view.findViewById(R.id.description);
        tvDescription.setText(description);
        tvTodayTotal = (TextView) view.findViewById(R.id.today_total);
        tvTodayTotal.setText(getTotals() != null && getTotals().size()>0 && getTotals().get(getCurrent())!=null ? getTotals().get(getCurrent()).toString() : "0");
        tvReadme = (TextView) view.findViewById(R.id.readme);
        tvReadme.setText(readme);

        TextView tvTotaltext = (TextView) view.findViewById(R.id.total_text);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        //int max = Math.max(totals.get(0), totals.get(1));
        //if(max < )
        int max = Collections.max(getTotals());
        double d = Double.parseDouble(getTotals() != null && getTotals().size()>0 && getTotals().get(getCurrent())!=null ? getTotals().get(getCurrent()).toString() : "0") * 100;
        Double p = d / max;
        progressBar.setProgress(p.intValue());

        tvDate = (TextView) view.findViewById(R.id.date);

        if (type == 0) {
            tvTotaltext.setText("Today total");
            //progressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.DST_IN);
            //MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appblue)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.appblue)));
        } else if (type == 1) {
            tvTotaltext.setText("Week total");
            //progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.DST_IN);
            //MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appgreen)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.appgreen)));

        } else if (type == 2) {
            tvTotaltext.setText("Month total");
            //progressBar.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.DST_IN);
            // MainActivity.toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apporange)));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.apporange)));
        }


        setData1(new ArrayList<Integer>(getArguments().getIntegerArrayList("data1")));
        setData2(new ArrayList<Integer>(getArguments().getIntegerArrayList("data2")));
        setData3(new ArrayList<Integer>(getArguments().getIntegerArrayList("data3")));

        chart = (LineChartView) view.findViewById(R.id.chart);
        drawGraph(getData1());

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
                        //tvTodayTotal.setText(totals!=null ? totals.get(current).toString() : "0");
                        refreshData();
                        Log.i("SWIPE", "Right to Left");
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        Log.i("SWIPE", "Left to Right");
                        if (getCurrent() == 2) setCurrent(0);
                        else setCurrent(getCurrent() + 1);
                        //tvTodayTotal.setText(totals!=null ? totals.get(current).toString() : "0");
                        refreshData();
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

    public void refreshFromApi(String description, String readme) {
        if (tvDescription != null)
            tvDescription.setText(description);
        if (tvReadme != null)
            tvReadme.setText(readme);
    }

    public void refreshData() {
        if (tvTodayTotal != null)
            tvTodayTotal.setText(getTotals() != null ? getTotals().get(getCurrent()).toString() : "0");

        int max = Collections.max(getTotals());
        double d = Double.parseDouble((getTotals().get(getCurrent())).toString()) * 100;
        Double p = d / max;
        progressBar.setProgress(p.intValue());

        switch (type) {
            case 0:
                //progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.appblue), PorterDuff.Mode.DST_IN);
                progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.appblue)));
                break;
            case 1:
                //progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.appgreen), PorterDuff.Mode.DST_IN);
                progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.appgreen)));
                break;
            case 2:
                //progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.apporange), PorterDuff.Mode.DST_IN);
                progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.apporange)));
                break;
        }
        int da = day;
        switch (getCurrent()) {
            case 0:
                drawGraph(getData1());
                String ma = "";
                ma = (month - 1 - getCurrent() <= 0) ? getResources().getStringArray(R.array.months)[month - 1 - getCurrent() + 12] : getResources().getStringArray(R.array.months)[month - 1 - getCurrent()];
                ma += " " + da;
                tvDate.setText(ma);
                break;
            case 1:
                drawGraph(getData2());
                ma = (month - 1 - getCurrent() <= 0) ? getResources().getStringArray(R.array.months)[month - 1 - getCurrent() + 12] : getResources().getStringArray(R.array.months)[month - 1 - getCurrent()];
                da -= 1;
                ma += " " + da;
                tvDate.setText(ma);
                break;
            case 2:
                drawGraph(getData3());
                ma = (month - 1 - getCurrent() <= 0) ? getResources().getStringArray(R.array.months)[month - 1 - getCurrent() + 12] : getResources().getStringArray(R.array.months)[month - 1 - getCurrent()];
                da -= 2;
                ma += " " + da;
                tvDate.setText(ma);
                break;
        }
    }

    public void drawGraph(ArrayList<Integer> data) {
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


       /* if(type == 0)
             axisX = Axis.generateAxisFromRange(0, 23, 1);
        else if(type == 1)
            axisX = Axis.generateAxisFromRange(0, 6, 1);
        else if(type == 2)
            axisX = Axis.generateAxisFromRange(0, 30, 1);*/

        for (int i = 0; i < data.size(); i++) {
            /*if (i>0 && data.get(i-1) == 0 && data.get(i)==0 && i<data.size()-1)
                i++;
            else*/
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

        String color = "";
        if (type == 0)
            color = "#007aff";
        if (type == 1)
            color = "#00C951";
        if (type == 2)
            color = "#FFCC33";
        Line line = new Line(values).setColor(Color.parseColor(color)).setCubic(false).setFilled(true).setAreaTransparency(15);//.setHasLabels(true);//Color.BLUE
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);
        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        //Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        // Axis axisX = Axis.generateAxisFromRange(0, data.size(), 1);
        //Axis axisY = Axis.generateAxisFromCollection(valuesY).setHasLines(true);

        //if (hasAxesNames) {
        if (type == 0)
            axisX.setName("Hours");
        if (type == 1)
            axisX.setName("Days");
        if (type == 2) {
            String m = "";
            m = (month - 1 - getCurrent() <= 0) ? getResources().getStringArray(R.array.months)[month - 1 - getCurrent() + 12] : getResources().getStringArray(R.array.months)[month - 1 - getCurrent()];
            axisX.setName(m);
        }

        //axisY.setName("Axis Y");
        // }
        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);


        //LineChartView chart2 = new LineChartView(this);
        //chart.setLineChartData(data);
        chart.setLineChartData(lineChartData);
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public List<Integer> getTotals() {
        return totals;
    }

    public void setTotals(List<Integer> totals) {
        this.totals = totals;
    }

    public ArrayList<Integer> getData1() {
        return data1;
    }

    public void setData1(ArrayList<Integer> data1) {
        this.data1 = data1;
    }

    public ArrayList<Integer> getData2() {
        return data2;
    }

    public void setData2(ArrayList<Integer> data2) {
        this.data2 = data2;
    }

    public ArrayList<Integer> getData3() {
        return data3;
    }

    public void setData3(ArrayList<Integer> data3) {
        this.data3 = data3;
    }
}
