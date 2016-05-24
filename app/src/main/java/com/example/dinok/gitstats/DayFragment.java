package com.example.dinok.gitstats;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by dinok on 5/21/2016.
 */
public class DayFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String fullname = this.getArguments().getString("full_name");
        String description = this.getArguments().getString("description");
        Integer todayTotal = this.getArguments().getInt("today_total");
        String readme =  this.getArguments().getString("readme");


        View view = inflater.inflate(R.layout.fragment_day, container, false);
        TextView tvFullName = (TextView) view.findViewById(R.id.full_name);
        //listMusic.setAdapter(new MusicBaseAdapter(getActivity(), listMusics));
        tvFullName.setText(fullname);
        TextView tvDescription = (TextView) view.findViewById(R.id.description);
        tvDescription.setText(description);
        TextView tvTodayTotal = (TextView) view.findViewById(R.id.today_total);
        tvTodayTotal.setText(todayTotal.toString());
        TextView tvReadme = (TextView) view.findViewById(R.id.readme);
        tvReadme.setText(readme);

        ArrayList<Integer> data = new ArrayList<Integer>(getArguments().getIntegerArrayList("mjesec"));
        List<PointValue> values = new ArrayList<PointValue>();

        for (int i=0; i<data.size(); i++){
            if(data.get(i)!=0)
                values.add(new PointValue(i, data.get(i)));
        }

        LineChartView chart = (LineChartView) view.findViewById(R.id.chart);
        chart.setInteractive(false);
        //chart.getAxesRenderer().

        //chart.setZoomType(ZoomType zoomType);
        //chart.setContainerScrollEnabled(boolean isEnabled, ContainerScrollType type);

        //List<PointValue> values = new ArrayList<PointValue>();
        //values.add(new PointValue(0, 2));
        //values.add(new PointValue(1, 4));
        //values.add(new PointValue(2, 3));
        //values.add(new PointValue(3, 4));

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.parseColor("#00C951")).setCubic(true).setFilled(true).setAreaTransparency(15);//.setHasLabels(true);//Color.BLUE
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);
        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        //if (hasAxesNames) {
            axisX.setName("March");
           // axisY.setName("Axis Y");
       // }
        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);


        //LineChartView chart2 = new LineChartView(this);
        //chart.setLineChartData(data);
        chart.setLineChartData(lineChartData);

        return view;


    }

}
