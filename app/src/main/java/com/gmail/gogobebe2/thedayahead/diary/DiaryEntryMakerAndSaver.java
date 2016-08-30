package com.gmail.gogobebe2.thedayahead.diary;

import android.widget.EditText;

import java.util.Calendar;

public class DiaryEntryMakerAndSaver {

    /**
     * This method is only used for the first week of the current month.
     */
    static void makeAndSaveDiaryEntry(EditText editText, int dayOfWeekInt, int hourOfDayInt, int minuteOfHourInt) {
        makeAndSaveDiaryEntry(editText, Calendar.MONTH, dayOfWeekInt - ((Calendar.WEEK_OF_MONTH - 1) * 7), hourOfDayInt, minuteOfHourInt);
    }

    /**
     * This method is used for any week of any month.
     */
    static void makeAndSaveDiaryEntry(EditText editText, int monthOfYearInt, int dayOfMonthInt, int hourOfDayInt, int minuteOfHourInt) {
        saveDiaryEntry(new DiaryEntry(getCurrentDiaryEntryText(editText), monthOfYearInt, dayOfMonthInt, hourOfDayInt, minuteOfHourInt));
    }

    private static void saveDiaryEntry(DiaryEntry diaryEntry) {
        diaryEntry.saveDiary();
    }

    private static String getCurrentDiaryEntryText(EditText editText) {
        return editText.getText().toString();

    }
}
