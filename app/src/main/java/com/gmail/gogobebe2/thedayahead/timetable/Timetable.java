package com.gmail.gogobebe2.thedayahead.timetable;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.TableLayout;

import com.gmail.gogobebe2.thedayahead.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Timetable {
    private Week week;
    private TimetableFragment timetableFragment;
    private TableLayout tableLayout;

    public Timetable(String htmlString, TimetableFragment timetableFragment) {
        this.timetableFragment = timetableFragment;
        this.tableLayout = (TableLayout) timetableFragment.getTimetableLinearLayout().findViewById(R.id.tablelayout_timetable);
        Document html = Jsoup.parse(htmlString);
        Element timetableTableElement = html.getElementsByTag("tbody").first();
        Elements rows = timetableTableElement.getElementsByTag("tr");


        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Element row = rows.get(rowIndex);
            Elements columns = row.getElementsByTag("td");
            if (columns.isEmpty()) {
                Elements topRow = row.getElementsByTag("th");
                Element weekElement = topRow.remove(0);
                this.week = Week.parseWeek(weekElement);

                for (Day day : Day.parseDays(topRow))
                    this.week.getDays().put(day,
                            new ArrayList<Period>());
            } else {
                columns.remove(0);
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {

                    // keySet() is still ordered because LinkedHashMap not HashMap.
                    Day day = (Day) week.getDays().keySet().toArray()[columnIndex];

                    View view = tableLayout.findViewById(
                            tableLayout.getResources().getIdentifier("lesson_r" + rowIndex + "c" +
                                    columnIndex, "id", tableLayout.getContext().getPackageName()));

                    Period period = Lesson.parseLesson(columns.get(columnIndex), view);
                    if (period == null) period = Period.parsePeriod(view);

                    week.getDays().get(day).add(period);
                }
            }
        }
    }

    public void show() {
        timetableFragment.getLoginRelativeLayout().setVisibility(View.GONE);
        timetableFragment.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        tableLayout.setVisibility(View.VISIBLE);
    }

    public void hide() {
        timetableFragment.getLoginRelativeLayout().setVisibility(View.VISIBLE);
        timetableFragment.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tableLayout.setVisibility(View.GONE);
    }

    public Week getWeek() {
        return this.week;
    }
}
