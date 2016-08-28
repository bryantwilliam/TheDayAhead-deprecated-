package com.gmail.gogobebe2.thedayahead.timetable;

import android.os.AsyncTask;

import com.gmail.gogobebe2.thedayahead.MainActivity;

import java.util.Calendar;

public class TimetableHighlighter extends AsyncTask<Void, Void, Void> {
    private Period currentPeriod;

    TimetableHighlighter(Timetable timetable) {
        MainActivity.timetable = timetable;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Highlights current period:

        Calendar calendar = Calendar.getInstance();
        Period newPeriod = MainActivity.timetable.getPeriod(calendar.get(Calendar.DAY_OF_WEEK),
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
