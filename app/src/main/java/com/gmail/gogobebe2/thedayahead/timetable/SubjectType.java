package com.gmail.gogobebe2.thedayahead.timetable;

public class SubjectType {
    private String name;
    private int firstDayIndex;
    private int firstPeriodIndex;

    SubjectType(String name, int firstDayIndex, int firstPeriodIndex) {
        this.name = name;
        this.firstDayIndex = firstDayIndex;
        this.firstPeriodIndex = firstPeriodIndex;
    }

    public String getName() {
        return this.name;
    }

    public int getFirstDayIndex() {
        return this.firstDayIndex;
    }

    public int getFirstPeriodIndex() {
        return this.firstPeriodIndex;
    }
}
