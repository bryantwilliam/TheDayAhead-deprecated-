package com.gmail.gogobebe2.thedayahead.timetable;

import org.jsoup.nodes.Element;

public class Period {
    private String subjectName;
    private String teacherInitials;
    private String classroom;

    public String getSubjectName() {
        return this.subjectName;
    }

    public String getTeacherInitials() {
        return this.teacherInitials;
    }

    public String getClassroom() {
        return this.classroom;
    }

    public Period(String subjectName, String teacherInitials, String classroom) {
        this.subjectName = subjectName;
        this.teacherInitials = teacherInitials;
        this.classroom = classroom;
    }

    static Period parsePeriod(Element period) {
        String[] classroomAndTeacherInitials = period.getElementsByClass("result").text().split(" ");
        return new Period(period.getElementsByAttribute("strong").first().text(),
                classroomAndTeacherInitials[0], classroomAndTeacherInitials[1]);
    }
}