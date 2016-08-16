package com.gmail.gogobebe2.thedayahead.timetable;

import android.widget.TextView;

import com.gmail.gogobebe2.thedayahead.R;

import org.jsoup.nodes.Element;

import java.util.LinkedHashMap;
import java.util.List;

public class Week {
    private LinkedHashMap<Day, List<Period>> days = new LinkedHashMap<>();
    private int weekNumber;

    private Week(final int weekNumber, TimetableFragment timetableFragment) {
        this.weekNumber = weekNumber;
        //Log.i(Utils.getTagName(timetableFragment), "weekNumber: " + weekNumber);
        final TextView textView = (TextView) timetableFragment.getActivity().findViewById(R.id.textView_weekNum);
        final String weekNumText = "Week: " + weekNumber;
        timetableFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(weekNumText);
            }
        });
    }

    public int getWeekNumber() {
        return this.weekNumber;
    }

    public LinkedHashMap<Day, List<Period>> getDays() {
        return this.days;
    }

    static Week parseWeek(Element weekElement, TimetableFragment timetableFragment) {
        return new Week(Integer.parseInt(weekElement.getElementById("week").val()), timetableFragment);
    }
}