package com.example.dinok.gitstats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dinok on 5/22/2016.
 */
public class DayCommit {
    private Date date;
    private Integer total;
    private List<Integer> hours;

    DayCommit() {
        setHours(new ArrayList<Integer>());
        for(int i=0; i<24; i++)
            getHours().add(0);
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
}
