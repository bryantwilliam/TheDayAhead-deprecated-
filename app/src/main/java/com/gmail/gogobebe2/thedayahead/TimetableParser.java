package com.gmail.gogobebe2.thedayahead;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TimetableParser {
    private Week week;

    TimetableParser(String htmlString) {
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

                for (Day day : Day.parseDays(row.getElementsByAttribute("th"))) week.days.put(day,
                        new ArrayList<Period>());
            }
            else {
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    // keySet() is still ordered because LinkedHashMap not HashMap.
                    Day day = (Day) week.days.keySet().toArray()[columnIndex];
                    Period period = Period.parsePeriod(columns.get(columnIndex));
                    week.days.get(day).add(period);
                }
            }

        }
    }

    private static class Week {
        private LinkedHashMap<Day, List<Period>> days = new LinkedHashMap<>();
        private int weekNumber;

        private Week(int weekNumber) {
            this.weekNumber = weekNumber;
        }

        private static Week parseWeek(Element weekElement) {
            return new Week(Integer.parseInt(weekElement.getElementById("week").val()));
        }
    }

    private static class Day {
        private String name;
        private String date;

        private Day(String name, String date) {
            this.date = date;
            this.name = name;
        }

        private static Day[] parseDays(Elements dayRow) {
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

    private static class Period {
        private String subjectName;
        private String teacherInitials;
        private String classroom;

        public Period(String subjectName, String teacherInitials, String classroom) {
            this.subjectName = subjectName;
            this.teacherInitials = teacherInitials;
            this.classroom = classroom;
        }

        private static Period parsePeriod(Element period) {
            String[] classroomAndTeacherInitials = period.getElementsByClass("result").text().split(" ");
            return new Period(period.getElementsByAttribute("strong").first().text(),
                    classroomAndTeacherInitials[0], classroomAndTeacherInitials[1]);
        }
    }
}
