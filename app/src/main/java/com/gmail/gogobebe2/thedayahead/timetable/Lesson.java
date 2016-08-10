package com.gmail.gogobebe2.thedayahead.timetable;

import android.view.View;
import android.widget.LinearLayout;

import org.jsoup.nodes.Element;


public class Lesson extends Period {
    private String subjectName;
    private String teacherInitials;
    private String classroom;
    private LinearLayout view;

    public String getSubjectName() {
        return this.subjectName;
    }

    public String getTeacherInitials() {
        return this.teacherInitials;
    }

    public String getClassroom() {
        return this.classroom;
    }

    private Lesson(String subjectName, String teacherInitials, String classroom, View view) {
        super(view);
        this.subjectName = subjectName;
        this.teacherInitials = teacherInitials;
        this.classroom = classroom;
        this.view = (LinearLayout) getView();


        // TODO replace default values in each view with ones in objects.
    }

    static Lesson parseLesson(Element period, View view) {
        String subjectInfo = period.getElementsByClass("result").text();
        String[] classroomAndTeacherInitials;
        if (subjectInfo == null || subjectInfo.equals(" ")) return null;
        else {
            classroomAndTeacherInitials = subjectInfo.split(" ");
            return new Lesson(period.getElementsByAttribute("strong").first().text(),
                    classroomAndTeacherInitials[0], classroomAndTeacherInitials[1], view);
        }
    }
}
