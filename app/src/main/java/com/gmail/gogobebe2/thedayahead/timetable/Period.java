package com.gmail.gogobebe2.thedayahead.timetable;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

public class Period {
    private View view;
    private TimetableFragment timetableFragment;
    private final int IMPORTANT_COLOUR;
    private final int CURRENT_SESSION_COLOUR;
    private boolean important = false;
    private boolean current = false;

    Period(View view, TimetableFragment timetableFragment) {
        this.timetableFragment = timetableFragment;
        this.view = view;
        this.IMPORTANT_COLOUR = getColour(android.R.color.holo_red_light);
        this.CURRENT_SESSION_COLOUR = getColour(android.R.color.holo_blue_light);
    }

    public void highlightImportant() {
        highlight(IMPORTANT_COLOUR);
        important = true;
    }

    public void highlightAsCurrentSession() {
        highlight(CURRENT_SESSION_COLOUR);
        current = true;
    }

    private int getColour(int COLOUR_ID) {
        return ContextCompat.getColor(timetableFragment.getContext(), COLOUR_ID);
    }

    private void highlight(final int COLOUR) {
        if (!important) {
            timetableFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setBackgroundColor(COLOUR);
                }
            });
        }
    }

    public boolean isHighlightedAsCurrent() {
        return current;
    }

    public boolean isHighlightedAsImportant() {
        return important;
    }

    public void unHighlightAsImportant() {
        important = false;
        if (!current) view.setBackgroundColor(Color.WHITE);
        else highlightAsCurrentSession();
    }

    public void unHighlightAsCurrent() {
        current = false;
        if (!important) view.setBackgroundColor(Color.WHITE);
        else highlightImportant();
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
