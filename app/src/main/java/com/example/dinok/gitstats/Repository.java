package com.example.dinok.gitstats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by dinok on 5/21/2016.
 */
public class Repository {
    private String fullName;
    private String description;
    private String readme;
    private List<DayCommit> dayCommits;
    private List<WeekCommit> weekCommits;
    private List<MonthCommit> monthCommits;
    private Date date;

    public Repository() {
        setDayCommits(new ArrayList<DayCommit>());
        setWeekCommits(new ArrayList<WeekCommit>());
        setMonthCommits(new ArrayList<MonthCommit>());
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void fromJson(JSONObject jsonObj) throws JSONException {
        fullName = jsonObj.getString("full_name");
        description = jsonObj.getString("description");
    }

    public void setTotals(JSONArray jsonArray) throws JSONException {

        //create week commits
        for (int i = 0; i < 3; i++)
            createWeekCommit(jsonArray.getJSONObject(jsonArray.length() - 1 - i));

        //create month commits
        for (int i = jsonArray.length() - 1; i >= 0; i--)
            if (!addMonthCommit(jsonArray.getJSONObject(i)))
                break;

        //create day commits
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
        Long timestamp = (Long) jsonObject.getLong("week");
        Date date = new Date(timestamp * 1000);
        Calendar calendarNow = Calendar.getInstance();
        int dayOfWeek = calendarNow.get(Calendar.DAY_OF_WEEK);
        JSONArray days = (JSONArray) jsonObject.getJSONArray("days");
        calendar.setTime(date);


        createDayCommit(calendar, days, dayOfWeek);
        calendar.add(Calendar.DATE, -1);
        if (dayOfWeek > 1) {
            createDayCommit(calendar, days, dayOfWeek - 1);
            calendar.add(Calendar.DATE, -1);
            if (dayOfWeek > 2) {
                createDayCommit(calendar, days, dayOfWeek - 2);
            } else {
                JSONObject jsonObject2 = jsonArray.getJSONObject(jsonArray.length() - 2);
                JSONArray days2 = (JSONArray) jsonObject.getJSONArray("days");
                createDayCommit(calendar, days2, 6);
            }
        } else {
            JSONObject jsonObject2 = jsonArray.getJSONObject(jsonArray.length() - 2);
            JSONArray days2 = (JSONArray) jsonObject2.getJSONArray("days");
            createDayCommit(calendar, days2, 6);
            calendar.add(Calendar.DATE, -1);
            createDayCommit(calendar, days2, 5);
        }
    }

    public void createDayCommit(Calendar calendar, JSONArray days, int i) throws JSONException {
        DayCommit dayCommit = new DayCommit();
        dayCommit.setDate(calendar.getTime());
        dayCommit.setTotal((Integer) days.get(i));
        getDayCommits().add(dayCommit);
    }

    public void createWeekCommit(JSONObject jsonObject) throws JSONException {
        Long timestamp = (Long) jsonObject.getLong("week");
        Date date = new Date(timestamp * 1000);
        WeekCommit weekCommit = new WeekCommit();
        weekCommit.setDate(date);
        weekCommit.setTotal(jsonObject.getInt("total"));
        JSONArray days = (JSONArray) jsonObject.getJSONArray("days");
        for (int i = 0; i < days.length(); i++)
            weekCommit.getDays().set(i, days.getInt(i));
        getWeekCommits().add(weekCommit);
    }


    public boolean addMonthCommit(JSONObject jsonObject) throws JSONException {
        Long timestamp = (Long) jsonObject.getLong("week");
        Date date = new Date(timestamp * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (getMonthCommits().size() == 0) {
            MonthCommit monthCommit = new MonthCommit();
            monthCommit.setDate(date);
            monthCommit.setTotal((jsonObject.getInt("total")));
            JSONArray days = (JSONArray) jsonObject.getJSONArray("days");
            for (int i = 0; i < days.length(); i++)
                if (calendar.get(Calendar.DAY_OF_MONTH) + i<31)
                    monthCommit.getDays().set(calendar.get(Calendar.DAY_OF_MONTH) + i, days.getInt(i));
            monthCommits.add(monthCommit);
        } else {
            boolean found = false;
            for (MonthCommit monthCommit : getMonthCommits()) {
                Calendar monthCalendar = Calendar.getInstance();
                monthCalendar.setTime(monthCommit.getDate());
                if (monthCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    found = true;
                    monthCommit.setTotal(monthCommit.getTotal() + (jsonObject.getInt("total")));
                    JSONArray days = (JSONArray) jsonObject.getJSONArray("days");
                    for (int i = 0; i < days.length(); i++)
                        if (calendar.get(Calendar.DAY_OF_MONTH) + i<31)
                            monthCommit.getDays().set(calendar.get(Calendar.DAY_OF_MONTH) + i, days.getInt(i));
                }
            }
            if (!found && monthCommits.size() < 3) {
                MonthCommit monthCommit = new MonthCommit();
                monthCommit.setDate(date);
                monthCommit.setTotal((jsonObject.getInt("total")));
                JSONArray days = (JSONArray) jsonObject.getJSONArray("days");
                for (int i = 0; i < days.length(); i++)
                    if (calendar.get(Calendar.DAY_OF_MONTH) + i<31)
                        monthCommit.getDays().set(calendar.get(Calendar.DAY_OF_MONTH) + i, days.getInt(i));
                monthCommits.add(monthCommit);
            } else if (!found && monthCommits.size() == 3)
                return false;
        }
        return true;
    }

    public void setGraphData(JSONArray jsonArray) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject commiter = jsonObject.getJSONObject("commit").getJSONObject("author");

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(tz);
            Date date = df.parse(commiter.getString("date").replace("T", " ").replace("Z", ""));

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            for (DayCommit day : getDayCommits()) {
                Calendar calendarCommit = Calendar.getInstance();
                calendarCommit.setTime(day.getDate());
                if (calendar.get(Calendar.DATE) == calendarCommit.get(Calendar.DATE)) {
                    Integer location = calendar.get(Calendar.HOUR_OF_DAY);
                    day.getHours().set(location, day.getHours().get(location) + 1);
                    day.setTotal(day.getTotal()+1);
                    break;
                }
            }
        }
    }

    public String getReadme() {
        return readme;
    }

    public void setReadme(String readme) {
        this.readme = readme;
    }

    public List<DayCommit> getDayCommits() {
        return dayCommits;
    }

    public void setDayCommits(List<DayCommit> dayCommits) {
        this.dayCommits = dayCommits;
    }

    public List<WeekCommit> getWeekCommits() {
        return weekCommits;
    }

    public void setWeekCommits(List<WeekCommit> weekCommits) {
        this.weekCommits = weekCommits;
    }

    public List<MonthCommit> getMonthCommits() {
        return monthCommits;
    }

    public void setMonthCommits(List<MonthCommit> monthCommit) {
        this.monthCommits = monthCommit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
