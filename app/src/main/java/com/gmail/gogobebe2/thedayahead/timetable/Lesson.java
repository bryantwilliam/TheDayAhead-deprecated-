package com.gmail.gogobebe2.thedayahead.timetable;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.gogobebe2.thedayahead.R;

import org.jsoup.nodes.Element;
import org.w3c.dom.Text;


public class Lesson extends Period {
    private String subjectName;
    private String teacherInitials;
    private String classroom;
    private LinearLayout linearLayout;

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
        this.linearLayout = (LinearLayout) getView();

        // Replaces default values in TextViews with the ones from the parsed html from Kmar.
        TextView textViewSubject = (TextView) linearLayout.getChildAt(0);
        TextView textViewTeacher = (TextView) ((LinearLayout) linearLayout.getChildAt(1)).getChildAt(0);
        TextView textViewClass = (TextView) ((LinearLayout) linearLayout.getChildAt(1)).getChildAt(1);

        textViewSubject.setText(subjectName);
        textViewTeacher.setText(teacherInitials);
        textViewClass.setText(classroom);
    }

    static Lesson parseLesson(Element period, View view) {
        String subjectInfo = period.getElementsByClass("result").text();

        if (subjectInfo == null || subjectInfo.equals(" ")) return null;
        else {
            subjectInfo = subjectInfo.replaceAll("\t", "").replaceAll(" ", "");
            String teacher = subjectInfo.substring(0, 2);
            String classroom = subjectInfo.substring(3, 5);
            return new Lesson(period.getElementsByAttribute("strong").first().text(),
                    teacher, classroom, view);
        }
    }
}
