package com.gmail.gogobebe2.thedayahead.timetable;

import android.os.AsyncTask;
import android.os.Handler;

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

        if (!(newPeriod == null || currentPeriod.equals(newPeriod))) {
            newPeriod.highlightAsCurrentSession();
            currentPeriod.unHighlight();
            currentPeriod = newPeriod;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        final int MINUTE = 60000;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isCancelled()) new TimetableHighlighter(timetable).execute();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, MINUTE);
    }

    void resume() {
        execute();
    }

    void pause() {
        cancel(true);
    }
}
