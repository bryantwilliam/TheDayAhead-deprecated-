package com.gmail.gogobebe2.thedayahead.timetable;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Day {
    private String name;
    private String date;

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date;
    }

    private Day(String name, String date) {
        this.date = date;
        this.name = name;
    }

    static Day[] parseDays(Elements dayRow) {
        Day[] days = new Day[5];
        for (int dayIndex = 0, dayRowSize = dayRow.size(); dayIndex < dayRowSize; dayIndex++) {
            Element day = dayRow.get(dayIndex);
            days[dayIndex] = parseDay(day);
        }
        return days;
    }

    private static Day parseDay(Element day) {
        String date = day.getElementsByClass("tt_date").text();
        return new Day(day.text().replace(date, ""), date);
    }
}