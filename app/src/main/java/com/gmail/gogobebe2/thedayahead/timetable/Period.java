package com.gmail.gogobebe2.thedayahead.timetable;

import android.view.View;

public class Period {
    private View view;

    Period(View view) {
        this.view = view;
    }

    public void highlightImportant() {
        view.setBackgroundColor(view.getResources().getColor(android.R.color.holo_red_light));
    }

    public void highlightAsCurrentSession() {
        view.setBackgroundColor(view.getResources().getColor(android.R.color.background_light));
    }

    static Period parsePeriod(View view) {
        return new Period(view);
    }
}
