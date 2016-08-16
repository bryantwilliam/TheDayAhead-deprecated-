package com.gmail.gogobebe2.thedayahead.timetable;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.nodes.Element;

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

    private Lesson(final String subjectName, final String teacherInitials, final String classroom, View view, TimetableFragment timetableFragment) {
        super(view);
        this.subjectName = subjectName;
        this.teacherInitials = teacherInitials;
        this.classroom = classroom;
        this.linearLayout = (LinearLayout) getView();

        // Replaces default values in TextViews with the ones from the parsed html from Kmar.
        final TextView textViewSubject = (TextView) linearLayout.getChildAt(0);
        final TextView textViewTeacher = (TextView) ((LinearLayout) linearLayout.getChildAt(1)).getChildAt(0);
        final TextView textViewClass = (TextView) ((LinearLayout) linearLayout.getChildAt(1)).getChildAt(1);

        timetableFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewSubject.setText(subjectName);
                textViewTeacher.setText(teacherInitials);
                textViewClass.setText(classroom);
            }
        });
    }

    static Lesson parseLesson(Element period, View view, TimetableFragment timetableFragment) {
        String subjectInfo = period.getElementsByClass("result").text();
        if (subjectInfo == null || subjectInfo.equals("")) return null;
        else {
            subjectInfo = subjectInfo.replaceAll("\t", "").replaceAll(" ", "");
            if (subjectInfo.isEmpty()) return null;

            String teacher = subjectInfo.substring(0, 3);
            String classroom = subjectInfo.substring(3, 6);
            String subjectName = period.getElementsByTag("strong").first().text();

            /*Log.i(Utils.getTagName(timetableFragment), "teacher: " + teacher);
            Log.i(Utils.getTagName(timetableFragment), "classroom: " + classroom);
            Log.i(Utils.getTagName(timetableFragment), "subjectName: " + subjectName);*/

            return new Lesson(subjectName, teacher, classroom, view, timetableFragment);
        }
    }
}
