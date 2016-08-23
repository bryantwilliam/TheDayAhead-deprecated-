package com.gmail.gogobebe2.thedayahead.timetable;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

public class Period {
    private View view;
    private TimetableFragment timetableFragment;

    Period(View view, TimetableFragment timetableFragment) {
        this.timetableFragment = timetableFragment;
        this.view = view;
    }

    public void highlightImportant() {
        highlight(android.R.color.holo_red_light);
    }

    public void highlightAsCurrentSession() {
        highlight(android.R.color.holo_blue_light);
    }

    private void highlight(final int COLOUR_ID) {
        final int COLOR = ContextCompat.getColor(timetableFragment.getContext(), COLOUR_ID);
        timetableFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundColor(COLOR);
            }
        });
    }

    public void unHighlight() {
        view.setBackgroundColor(Color.WHITE);
    }

    View getView() {
        return this.view;
    }

    static Period parsePeriod(View view, TimetableFragment timetableFragment) {
        return new Period(view, timetableFragment);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object) || (object instanceof Period && ((Period) object).getView().equals(view));
    }
}
