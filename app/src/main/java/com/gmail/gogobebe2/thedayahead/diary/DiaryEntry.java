package com.gmail.gogobebe2.thedayahead.diary;

import java.util.List;

public class DiaryEntry {
    private static List<DiaryEntry> diaryEntries;

    private String text;
    private int monthOfYear;
    private int dayOfMonth;
    private int hourOfDay;
    private int minuteOfHour;

    DiaryEntry(String text, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        this.text = text;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.hourOfDay = hourOfDay;
        this.minuteOfHour = minuteOfHour;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinuteOfHour() {
        return minuteOfHour;
    }

    String getText() {
        return text;
    }

    void saveDiary() {
        // TODO
    }

    static DiaryEntry loadDiaries() {
        return null; // TODO
    }
}
