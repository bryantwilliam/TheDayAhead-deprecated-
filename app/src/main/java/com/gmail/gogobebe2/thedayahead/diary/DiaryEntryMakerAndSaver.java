package com.gmail.gogobebe2.thedayahead.diary;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

public class DiaryEntryMakerAndSaver {

    /**
     * Used for the first week of the current month.
     */
    static void makeAndSaveDiaryEntry(EditText editText, int dayOfWeekInt, int hourOfDayInt,
                                      int minuteOfHourInt, Context context,
                                      ListView listViewDiaryEntries) {
        makeAndSaveDiaryEntry(editText, Calendar.MONTH, dayOfWeekInt - ((Calendar.WEEK_OF_MONTH - 1)
                * 7), hourOfDayInt, minuteOfHourInt, context, listViewDiaryEntries);
    }

    /**
     * Used for any week of any month.
     */
    static void makeAndSaveDiaryEntry(EditText editText, int monthOfYearInt, int dayOfMonthInt,
                                      int hourOfDayInt, int minuteOfHourInt, Context context,
                                      ListView listViewDiaryEntries) {
        saveDiaryEntry(new DiaryEntry(getCurrentDiaryEntryText(editText), monthOfYearInt,
                dayOfMonthInt, hourOfDayInt, minuteOfHourInt), context);
        updateDiaryEntryListView(context, listViewDiaryEntries);
    }

    private static void updateDiaryEntryListView(Context context, ListView listViewDiaryEntries) {
        for (DiaryEntry diaryEntry : DiaryEntry.loadDiaries(context)) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView entryDate = new TextView(context);
            String date = diaryEntry.getDayOfMonth() + "/" + diaryEntry.getMonthOfYear() + "/"
                    + Calendar.YEAR + " - " + diaryEntry.getHourOfDay() + ":" + diaryEntry
                    .getMinuteOfHour();
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

            listViewDiaryEntries.removeAllViewsInLayout();
            listViewDiaryEntries.addView(linearLayout);
        }

    }

    private static void saveDiaryEntry(DiaryEntry diaryEntry, Context context) {
        diaryEntry.saveDiary(context);
    }

    private static String getCurrentDiaryEntryText(EditText editText) {
        return editText.getText().toString();

    }
}
