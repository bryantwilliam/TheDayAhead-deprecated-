package com.gmail.gogobebe2.thedayahead.timetable;

import android.graphics.Color;
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

    public void unHighlight() {
        view.setBackgroundColor(Color.WHITE);
    }

    View getView() {
        return this.view;
    }

    static Period parsePeriod(View view) {
        return new Period(view);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object) || (object instanceof Period && ((Period) object).getView().equals(view));
    }
}
