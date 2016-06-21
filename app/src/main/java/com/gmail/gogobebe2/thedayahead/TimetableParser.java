package com.gmail.gogobebe2.thedayahead;

public class TimetableParser {
    // TODO make this class save information about the timetable.
    private Day[] days = new Day[5];
    private final String HTML;

    TimetableParser(String html) {
        this.HTML = html;
    }

    private class Day {
        private Period[] periods;
        private String date;

        private Day(Period[] periods, String date) {
            this.periods = periods;
            this.date = date;
        }
    }

    private class Period {
        private String subjectName;
        private String teacherInitials;
        private String classroom;
    }
}
