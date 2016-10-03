package com.gmail.gogobebe2.thedayahead.timetable;

import android.content.Context;
import android.os.AsyncTask;

import com.gmail.gogobebe2.thedayahead.MainActivity;
import com.gmail.gogobebe2.thedayahead.diary.DiaryEntry;

import java.util.Calendar;
import java.util.List;

public class TimetableHighlighter extends AsyncTask<Void, Void, Void> {
    private Period currentPeriod;
    private Context context;

    TimetableHighlighter(Timetable timetable, Context context) {
        this.context = context;
        MainActivity.timetable = timetable;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Calendar currentCalendar = Calendar.getInstance();
        int currentMonthOfYear = currentCalendar.get(Calendar.MONTH);
        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int currentHourOfDay = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinuteOfHour = currentCalendar.get(Calendar.MINUTE);

        // Highlights as important:
        List<DiaryEntry> diaries = DiaryEntry.loadDiaries(context);
        for (DiaryEntry diaryEntry : diaries) {
            Calendar entryCalendar = Calendar.getInstance();
            entryCalendar.set(entryCalendar.get(Calendar.YEAR), diaryEntry.getMonthOfYear(),
                    diaryEntry.getDayOfMonth());
            int dayOfWeekInt = entryCalendar.get(Calendar.DAY_OF_WEEK);

            Period period = MainActivity.timetable.getPeriod(dayOfWeekInt,
                    diaryEntry.getHourOfDay(), diaryEntry.getMinuteOfHour());

            if (diaryEntry.getMinuteOfHour() < currentMinuteOfHour
                    && diaryEntry.getHourOfDay() <= currentHourOfDay
                    && diaryEntry.getDayOfMonth() <= currentDayOfMonth
                    && diaryEntry.getMonthOfYear() <= currentMonthOfYear) {
                if (period.isHighlightedAsImportant()) {
                    boolean rehighlightAsCurrent = period.isHighlightedAsCurrent();
                    period.unHighlight();
                    if (rehighlightAsCurrent) period.highlightAsCurrentSession();
                }
                diaries.remove(diaryEntry);
            } else period.isHighlightedAsImportant();
        }

        // Highlights current period:
        Period newPeriod = MainActivity.timetable.getPeriod(currentCalendar.get(Calendar.DAY_OF_WEEK),
                currentHourOfDay, currentMinuteOfHour);

        if (newPeriod != null) {
            if (currentPeriod != null) {
                if (!currentPeriod.equals(newPeriod)) return null;
                boolean rehighlightAsImportant = currentPeriod.isHighlightedAsImportant();
                currentPeriod.unHighlight();
                if (rehighlightAsImportant) currentPeriod.isHighlightedAsImportant();
            }
            currentPeriod = newPeriod;
            currentPeriod.highlightAsCurrentSession();
        }
        //
        return null;
    }

    // TODO find way to keep this running, not just when timetable is opened.
}
