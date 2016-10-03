package com.gmail.gogobebe2.thedayahead.timetable;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

public class Period {
    private View view;
    private TimetableFragment timetableFragment;
    private final int IMPORTANT_COLOUR;
    private final int CURRENT_SESSION_COLOUR;

    Period(View view, TimetableFragment timetableFragment) {
        this.IMPORTANT_COLOUR = getColour(android.R.color.holo_red_light);
        this.CURRENT_SESSION_COLOUR = getColour(android.R.color.holo_blue_light);
        this.timetableFragment = timetableFragment;
        this.view = view;
    }

    public void highlightImportant() {
        highlight(IMPORTANT_COLOUR);
    }

    public void highlightAsCurrentSession() {
        highlight(CURRENT_SESSION_COLOUR);
    }

    private int getColour(int COLOUR_ID) {
        return ContextCompat.getColor(timetableFragment.getContext(), COLOUR_ID);
    }

    private void highlight(final int COLOUR) {
        timetableFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundColor(COLOUR);
            }
        });
    }

    public boolean isHighlightedAsCurrent() {
        return view.getSolidColor() == CURRENT_SESSION_COLOUR;
    }

    public boolean isHighlightedAsImportant() {
        return view.getSolidColor() == IMPORTANT_COLOUR;
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
