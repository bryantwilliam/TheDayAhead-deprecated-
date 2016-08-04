package com.gmail.gogobebe2.thedayahead.timetable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TimetableParser {
    private Week week;

    public TimetableParser(String htmlString) {
        Document html = Jsoup.parse(htmlString);
        Element timetableTableElement = html.getElementsByAttribute("tbody").first();
        Elements rows = timetableTableElement.getElementsByAttribute("tr");



        for (Element row : rows) {
            Elements columns = row.getElementsByAttribute("td");
            if (columns.isEmpty()) {
                Elements topRow = row.getElementsByAttribute("th");
                Element weekElement = topRow.first();
                topRow.remove(0);
                week = Week.parseWeek(weekElement);

                for (Day day : Day.parseDays(row.getElementsByAttribute("th"))) week.getDays().put(day,
                        new ArrayList<Period>());
            }
            else {
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    // keySet() is still ordered because LinkedHashMap not HashMap.
                    Day day = (Day) week.getDays().keySet().toArray()[columnIndex];
                    Period period = Period.parsePeriod(columns.get(columnIndex));
                    week.getDays().get(day).add(period);
                }
            }

        }
    }

    public Week getWeek() {
        return this.week;
    }
}
