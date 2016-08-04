package com.gmail.gogobebe2.thedayahead.timetable;

import org.jsoup.nodes.Element;

import java.util.LinkedHashMap;
import java.util.List;

public class Week {
    private LinkedHashMap<Day, List<Period>> days = new LinkedHashMap<>();
    private int weekNumber;

    private Week(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getWeekNumber() {
        return this.weekNumber;
    }

    public LinkedHashMap<Day, List<Period>> getDays() {
        return this.days;
    }

    static Week parseWeek(Element weekElement) {
        return new Week(Integer.parseInt(weekElement.getElementById("week").val()));
    }
}