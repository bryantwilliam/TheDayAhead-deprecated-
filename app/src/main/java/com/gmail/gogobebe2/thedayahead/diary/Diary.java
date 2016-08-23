package com.gmail.gogobebe2.thedayahead.diary;

public class Diary {
    private String text;
    private int day;
    private int hour;
    private int minute;

    Diary(String text, int day, int hour, int minute) {
        this.text = text;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getText() {
        return text;
    }

    private void saveDiary(Diary diary) {
        // TODO
    }

    private Diary loadDiary() {
        return new Diary(null, -1, -1, -1); // TODO
    }
}
