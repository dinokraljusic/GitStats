package com.example.dinok.gitstats;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dinok on 5/22/2016.
 */
public class DayCommit extends SugarRecord {
    private Date date;
    private Integer total;
    @Ignore
    private List<Integer> hours;
    private long repoId;
    private String array;

    public DayCommit() {
        setHours(new ArrayList<Integer>());
        for (int i = 0; i < 24; i++)
            getHours().add(0);
        setTotal(0);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Integer> getHours() {
        return hours;
    }

    public void setHours(List<Integer> hours) {
        this.hours = hours;
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
            hours = new ArrayList<Integer>();
            String[] daysString = array.split(",");
            for (String day : daysString)
                hours.add(Integer.parseInt(day));
        }
    }

    @Override
    public long save() {
        array = "";
        for (Integer day : getHours())
            array += day.toString() + ",";
        return super.save();
    }
}
