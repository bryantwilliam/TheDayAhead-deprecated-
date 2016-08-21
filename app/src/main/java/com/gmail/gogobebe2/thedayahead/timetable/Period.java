package com.gmail.gogobebe2.thedayahead.timetable;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

public class Period {
    private View view;

    Period(View view) {
        this.view = view;
    }

    public void highlightImportant() {
        highlight(android.R.color.holo_red_light);
    }

    public void highlightAsCurrentSession() {
        highlight(android.R.color.background_light);
    }

    private void highlight(final int COLOUR_ID) {
        int colour;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) colour = view.getResources().getColor(
                COLOUR_ID, view.getContext().getTheme());
        else colour = view.getResources().getColor(COLOUR_ID);

        view.setBackgroundColor(colour);
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
