package com.gmail.gogobebe2.thedayahead.timetable;

import android.view.View;
import android.widget.TableLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Timetable {
    private Week week;

    public Timetable(String htmlString, TableLayout tableLayout) {
        Document html = Jsoup.parse(htmlString);
        Element timetableTableElement = html.getElementsByAttribute("tbody").first();
        Elements rows = timetableTableElement.getElementsByAttribute("tr");


        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Element row = rows.get(rowIndex);
            Elements columns = row.getElementsByAttribute("td");
            if (columns.isEmpty()) {
                Elements topRow = row.getElementsByAttribute("th");
                Element weekElement = topRow.first();
                topRow.remove(0);
                week = Week.parseWeek(weekElement);

                for (Day day : Day.parseDays(row.getElementsByAttribute("th")))
                    week.getDays().put(day,
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


    public Week getWeek() {
        return this.week;
    }
}
