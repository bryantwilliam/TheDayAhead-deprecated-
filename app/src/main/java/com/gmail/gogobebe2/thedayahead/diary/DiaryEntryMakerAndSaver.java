package com.gmail.gogobebe2.thedayahead.diary;

import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;

public class DiaryEntryMakerAndSaver {

    /**
     * This method is only used for the first week of the current month.
     */
    static void makeAndSaveDiaryEntry(EditText editText, int dayOfWeekInt, int hourOfDayInt, int minuteOfHourInt, Context context) {
        makeAndSaveDiaryEntry(editText, Calendar.MONTH, dayOfWeekInt - ((Calendar.WEEK_OF_MONTH - 1) * 7), hourOfDayInt, minuteOfHourInt, context);
    }

    /**
     * This method is used for any week of any month.
     */
    static void makeAndSaveDiaryEntry(EditText editText, int monthOfYearInt, int dayOfMonthInt, int hourOfDayInt, int minuteOfHourInt, Context context) {
        saveDiaryEntry(new DiaryEntry(getCurrentDiaryEntryText(editText), monthOfYearInt, dayOfMonthInt, hourOfDayInt, minuteOfHourInt), context);
    }

    private static void saveDiaryEntry(DiaryEntry diaryEntry, Context context) {
        diaryEntry.saveDiary(context);
    }

    private static String getCurrentDiaryEntryText(EditText editText) {
        return editText.getText().toString();

    }
}
