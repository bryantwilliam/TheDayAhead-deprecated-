package com.gmail.gogobebe2.thedayahead.timetable;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.gmail.gogobebe2.thedayahead.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Timetable {
    private Week week;
    private TimetableFragment timetableFragment;
    private TableLayout tableLayout;
    private TimetableHighlighter timetableHighlighter;

    public Timetable(String htmlString, TimetableFragment timetableFragment) {
        this.timetableFragment = timetableFragment;
        this.tableLayout = (TableLayout) timetableFragment.getTimetableLinearLayout().findViewById(R.id.tablelayout_timetable);
        Document html = Jsoup.parse(htmlString);
        Element timetableTableElement = html.getElementsByTag("tbody").first();
        Elements rows = timetableTableElement.getElementsByTag("tr");


        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Element row = rows.get(rowIndex);
            Elements columns = row.getElementsByTag("td");
            if (columns.isEmpty()) { // TOP ROW:
                Elements topRow = row.getElementsByTag("th");
                Element weekElement = topRow.remove(0);
                this.week = Week.parseWeek(weekElement, timetableFragment);

                for (Day day : Day.parseDays(topRow))
                    this.week.getDays().put(day,
                            new ArrayList<Period>());
            } else { // ALL LESSONS:
                columns.remove(0);
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {

                    // keySet() is still ordered because LinkedHashMap not HashMap.
                    Day day = (Day) week.getDays().keySet().toArray()[columnIndex];

                    Period period = null;

                    View view = tableLayout.findViewById(
                            tableLayout.getResources().getIdentifier(
                                    "lesson_r" + rowIndex + "c" + (columnIndex + 1), "id",
                                    tableLayout.getContext().getPackageName()));

                    if (view instanceof LinearLayout)
                        period = Lesson.parseLesson(columns.get(columnIndex), view, timetableFragment);
                    if (period == null) period = Period.parsePeriod(view);

                    week.getDays().get(day).add(period);
                }
            }
        }

        timetableHighlighter = new TimetableHighlighter(this);
        timetableHighlighter.execute();
    }

    public void show() {
        final RelativeLayout relativeLayout = timetableFragment.getLoginRelativeLayout();
        timetableFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                relativeLayout.setVisibility(View.GONE);
                tableLayout.setVisibility(View.VISIBLE);
            }
        });
        timetableFragment.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public Week getWeek() {
        return this.week;
    }

    public Period getPeriod(int dayInt, int hourInt, int minuteInt) {
        LinkedHashMap<Day, List<Period>> days = getWeek().getDays();

        List<Period> periods = null;

        dayInt -= 2;
        int index = 0;

        if (dayInt == 5 || dayInt == -1) return null;
        else for (Day day : days.keySet()) {
            if (index == dayInt) {
                periods = days.get(day);
                break;
            }
            index++;
        }

        if (periods == null) throw new NullPointerException("periods should never be null");

        // TODO switch/case return period from periods.
        return null;
    }

    TimetableHighlighter getTimetableHighlighter() {
        return this.timetableHighlighter;
    }
}
