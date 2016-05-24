package com.example.dinok.gitstats;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dinok on 5/22/2016.
 */
public class WeekCommit extends SugarRecord {
    private Date date;
    private Integer total;
    @Ignore
    private List<Integer> days;
    private long repoId;
    private String array;

    public WeekCommit() {
        days = new ArrayList<Integer>();
        for (int i = 0; i < 7; i++)
            days.add(0);
        setTotal(0);
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    public long getRepoId() {
        return repoId;
    }

    public void setRepoId(long repoId) {
        this.repoId = repoId;
    }

    public String getArray() {
        return array;
    }

    public void setArray(String array) {
        this.array = array;
    }

    public void generateListFromArray() {
        if (array!=null && !array.isEmpty()) {
            days = new ArrayList<Integer>();
            String[] daysString = array.split(",");
            for (String day : daysString)
                days.add(Integer.parseInt(day));
        }
    }

    @Override
    public long save() {
        array = "";
        for (Integer day : getDays())
            array += day.toString() + ",";
        return super.save();
    }
}
