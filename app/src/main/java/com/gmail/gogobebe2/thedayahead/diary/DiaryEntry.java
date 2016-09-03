package com.gmail.gogobebe2.thedayahead.diary;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class DiaryEntry {
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

    void saveDiary(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        int diaryEntryIndex = getHighestDiaryEntryIndex(sharedPreferences);

        sharedPreferencesEditor.putString("diary_text" + diaryEntryIndex, text);
        sharedPreferencesEditor.putInt("diary_monthOfYear" + diaryEntryIndex, monthOfYear);
        sharedPreferencesEditor.putInt("diary_dayOfMonth" + diaryEntryIndex, dayOfMonth);
        sharedPreferencesEditor.putInt("diary_hourOfDay" + diaryEntryIndex, hourOfDay);
        sharedPreferencesEditor.putInt("diary_minuteOfHour" + diaryEntryIndex, minuteOfHour);
        sharedPreferencesEditor.apply();
    }

    private int getHighestDiaryEntryIndex(SharedPreferences sharedPreferences) {
        int diaryEntryIndex = 0;

        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.contains("diary_text") || key.contains("diary_monthOfYear")
                    || key.contains("diary_dayOfMonth") || key.contains("diary_hourOfDay")
                    || key.contains("diary_minuteOfHour")) {
                int tempDiaryEntryIndex = key.charAt(key.length() - 1);
                if (diaryEntryIndex <= tempDiaryEntryIndex) diaryEntryIndex = ++tempDiaryEntryIndex;
            }
        }

        return diaryEntryIndex;
    }

    static List<DiaryEntry> loadDiaries(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context
                .getApplicationInfo().name, Context.MODE_PRIVATE);

        List<DiaryEntry> diaryEntries = new ArrayList<>();

        int index = 0;
        while (true) {
            String text = sharedPreferences.getString("diary_text" + index, null);
            int monthOfYear = sharedPreferences.getInt("diary_monthOfYear" + index, -1);
            int dayOfMonth = sharedPreferences.getInt("diary_dayOfMonth" + index, -1);
            int hourOfDay = sharedPreferences.getInt("diary_hourOfDay" + index, -1);
            int minuteOfHour = sharedPreferences.getInt("diary_minuteOfHour" + index, -1);

            if (text == null || monthOfYear == -1 || dayOfMonth == -1 || hourOfDay == -1
                    || minuteOfHour == -1) break;

            diaryEntries.add(new DiaryEntry(text, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour));

            index++;
        }

        return diaryEntries;
    }

}
