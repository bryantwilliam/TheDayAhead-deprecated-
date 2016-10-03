package com.gmail.gogobebe2.thedayahead.diary;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.gogobebe2.thedayahead.MainActivity;
import com.gmail.gogobebe2.thedayahead.timetable.Period;

import java.util.Calendar;

public class DiaryEntryManager {

    /**
     * Used for the first week of the current month.
     */
    static void makeAndSaveDiaryEntry(EditText editText, int dayOfWeekInt, int hourOfDayInt,
                                      int minuteOfHourInt, Context context,
                                      LinearLayout linearLayoutDiaryEntriesList) {
        Calendar calendar = Calendar.getInstance();
        makeAndSaveDiaryEntry(editText, calendar.get(Calendar.MONTH), dayOfWeekInt + ((calendar.get(Calendar.WEEK_OF_MONTH) - 1)
                * 7), hourOfDayInt, minuteOfHourInt, context, linearLayoutDiaryEntriesList);
    }

    /**
     * Used for any week of any month.
     */
    static void makeAndSaveDiaryEntry(EditText editText, int monthOfYearInt, int dayOfMonthInt,
                                      int hourOfDayInt, int minuteOfHourInt, Context context,
                                      LinearLayout linearLayoutDiaryEntriesList) {
        saveDiaryEntry(new DiaryEntry(getCurrentDiaryEntryText(editText), monthOfYearInt,
                dayOfMonthInt, hourOfDayInt, minuteOfHourInt), context);
        updateDiaryEntryListView(context, linearLayoutDiaryEntriesList);
    }

    public static void updateDiaryEntryListView(Context context, LinearLayout linearLayoutDiaryEntriesList) {
        linearLayoutDiaryEntriesList.removeAllViewsInLayout();
        for (DiaryEntry diaryEntry : DiaryEntry.loadDiaries(context)) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView entryDate = new TextView(context);

            String minuteOfHourStr = "" + diaryEntry.getMinuteOfHour();
            if (minuteOfHourStr.length() == 1) minuteOfHourStr = "0" + minuteOfHourStr;

            String date = diaryEntry.getDayOfMonth() + "/" + diaryEntry.getMonthOfYear() + "/"
                    + Calendar.getInstance().get(Calendar.YEAR) + " - " + diaryEntry.getHourOfDay()
                    + ":" + minuteOfHourStr;

            entryDate.setText(date);
            entryDate.setTypeface(null, Typeface.BOLD);
            entryDate.setLayoutParams(layoutParams);

            TextView entryText = new TextView(context);
            entryText.setText(diaryEntry.getText());
            entryText.setLayoutParams(layoutParams);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(entryDate);
            linearLayout.addView(entryText);
            linearLayout.setLayoutParams(layoutParams);

            linearLayoutDiaryEntriesList.addView(linearLayout);
            
            // TODO: highlight due times:
            Calendar currentCalendar = Calendar.getInstance();
            int currentMonthOfYear = currentCalendar.get(Calendar.MONTH);
            int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
            int currentHourOfDay = currentCalendar.get(Calendar.HOUR_OF_DAY);
            int currentMinuteOfHour = currentCalendar.get(Calendar.MINUTE);

            Calendar entryCalendar = Calendar.getInstance();
            entryCalendar.set(entryCalendar.get(Calendar.YEAR), diaryEntry.getMonthOfYear(), diaryEntry.getDayOfMonth());
            int dayOfWeekInt = entryCalendar.get(Calendar.DAY_OF_WEEK);

            Period period = MainActivity.timetable.getPeriod(dayOfWeekInt,
                    diaryEntry.getHourOfDay(), diaryEntry.getMinuteOfHour());

            if (diaryEntry.getMinuteOfHour() < currentMinuteOfHour
                    && diaryEntry.getHourOfDay() <= currentHourOfDay
                    && diaryEntry.getDayOfMonth() <= currentDayOfMonth
                    && diaryEntry.getMonthOfYear() <= currentMonthOfYear) {

            }
        }

    }

    private static void saveDiaryEntry(DiaryEntry diaryEntry, Context context) {
        diaryEntry.saveDiary(context);
    }

    private static String getCurrentDiaryEntryText(EditText editText) {
        Editable text = editText.getText();

        if (text == null || text.toString().equals("")) return "{empty}";
        else return text.toString();
    }
}
