package com.gmail.gogobebe2.thedayahead.timetable;

import android.os.AsyncTask;

import java.util.Calendar;

public class TimetableHighlighter extends AsyncTask<Void, Void, Void> {
    private Timetable timetable;
    private Period currentPeriod;

    TimetableHighlighter(Timetable timetable) {
        this.timetable = timetable;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Highlights current period:

        Calendar calendar = Calendar.getInstance();
        Period newPeriod = timetable.getPeriod(calendar.get(Calendar.DAY_OF_WEEK),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        if (newPeriod != null) {
            if (currentPeriod != null) {
                if (!currentPeriod.equals(newPeriod)) return null;
                currentPeriod.unHighlight();
            }
            currentPeriod = newPeriod;
            currentPeriod.highlightAsCurrentSession();
        }

        return null;
    }

    // TODO find way to keep this running.
}
