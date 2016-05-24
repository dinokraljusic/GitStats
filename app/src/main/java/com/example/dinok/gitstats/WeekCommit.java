package com.example.dinok.gitstats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dinok on 5/22/2016.
 */
public class WeekCommit {
    private Date date;
    private Integer total;
    private List<Integer> days;

    public WeekCommit(){
        days = new ArrayList<Integer>();
        for(int i=0; i<7; i++)
            days.add(0);
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
}
