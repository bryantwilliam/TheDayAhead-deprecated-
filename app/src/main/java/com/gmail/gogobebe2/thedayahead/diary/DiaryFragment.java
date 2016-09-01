package com.gmail.gogobebe2.thedayahead.diary;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gmail.gogobebe2.thedayahead.MainActivity;
import com.gmail.gogobebe2.thedayahead.R;
import com.gmail.gogobebe2.thedayahead.TheDayAheadFragment;
import com.gmail.gogobebe2.thedayahead.timetable.SubjectType;

import java.util.Calendar;

public class DiaryFragment extends TheDayAheadFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    public DiaryFragment() {/* Required empty public constructor*/}

    private LinearLayout fragmentDiary;
    private LinearLayout diaryDialogByDateAndTime;
    private ListView listviewPeriodSelector;
    private ListView listviewDiaryEntries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Inflate the layouts for this fragment
        diaryDialogByDateAndTime = (LinearLayout) inflater.inflate(R.layout.fragment_diary_dialog_bydateandtime, container, false);
        fragmentDiary = (LinearLayout) inflater.inflate(R.layout.fragment_diary, container, false);
        listviewPeriodSelector = (ListView) diaryDialogByDateAndTime.findViewById(R.id.listView_periodSelector);
        listviewDiaryEntries = (ListView) fragmentDiary.findViewById(R.id.listView_diaryEntries);

        ((Switch) fragmentDiary.findViewById(R.id.switch_dateTimeOrPeriod))
                .setOnCheckedChangeListener(this);
        fragmentDiary.findViewById(R.id.button_pickDateOrPeriod).setOnClickListener(this);
        fragmentDiary.findViewById(R.id.button_doneDateAndTimeSelection).setOnClickListener(this);
        fragmentDiary.findViewById(R.id.button_pickDate).setOnClickListener(this);
        fragmentDiary.findViewById(R.id.button_pickTime).setOnClickListener(this);

        return fragmentDiary;
    }

    @NonNull
    @Override
    protected String getTitle() {
        return "DiaryEntry";
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Switch switchView = (Switch) buttonView;
        String buttonTitle = "Save entry and pick ";

        Button button = (Button) switchView.findViewById(R.id.button_pickDateOrPeriod);

        if (isChecked) {
            if (MainActivity.timetable == null) {
                switchView.setChecked(false);
                Toast.makeText(getContext(), "You need to create a timetable instance "
                        + "by logging in on the \"Timetable\" tab", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            for (SubjectType subjectType : MainActivity.timetable.getSubjects()) {
                Button subjectButton = new Button(getContext());
                subjectButton.setText(subjectType.getName());
                subjectButton.setOnClickListener(this);
                listviewPeriodSelector.addView(subjectButton);
            }

            listviewPeriodSelector.setVisibility(View.VISIBLE);
            diaryDialogByDateAndTime.setVisibility(View.GONE);
            buttonTitle += "period";

        } else {
            listviewPeriodSelector.setVisibility(View.GONE);
            diaryDialogByDateAndTime.setVisibility(View.VISIBLE);
            buttonTitle += "date/time";
        }
        button.setText(buttonTitle);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;

            int dayInt;
            int hourInt;
            int minuteInt;
            int monthInt;

            EditText editText = (EditText) button.findViewById(R.id.editText_diary);
            TimePicker timePicker = (TimePicker) button.findViewById(R.id.timePicker);
            DatePicker datePicker = (DatePicker) button.findViewById(R.id.datePicker);
            LinearLayout linearLayoutDialogByDateAndTime = (LinearLayout) button.findViewById(
                    R.id.linearlayout_dialogByDateAndTime);

            switch (button.getId()) {
                // * Used "return" in this switch statement when the computer doesn't need to go
                //   onto the rest of the method.
                case R.id.button_pickDateOrPeriod:
                    // TODO show fragment_dairy_dialog.xml
                    return; // *
                case R.id.button_doneDateAndTimeSelection:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        monthInt = datePicker.getMonth();
                        dayInt = datePicker.getDayOfMonth();
                        hourInt = timePicker.getHour();
                        minuteInt = timePicker.getMinute();

                        DiaryEntryMakerAndSaver.makeAndSaveDiaryEntry(editText, monthInt, dayInt, hourInt, minuteInt, getContext());
                    } else {
                        Toast.makeText(getContext(), "Android version too low to support this " +
                                        "function, please use the period selection option only",
                                Toast.LENGTH_LONG).show();
                    }
                    for (int index = 0; index < linearLayoutDialogByDateAndTime.getChildCount(); index++)
                        linearLayoutDialogByDateAndTime.getChildAt(index).setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                    return; // *
                case R.id.button_pickDate:
                    for (int index = 0; index < linearLayoutDialogByDateAndTime.getChildCount(); index++)
                        linearLayoutDialogByDateAndTime.getChildAt(index).setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                    return; // *
                case R.id.button_pickTime:
                    for (int index = 0; index < linearLayoutDialogByDateAndTime.getChildCount(); index++)
                        linearLayoutDialogByDateAndTime.getChildAt(index).setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    return; // *
            }

            for (SubjectType subjectType : MainActivity.timetable.getSubjects()) {
                String subjectName = subjectType.getName();
                if (button.getText().equals(subjectName)) {
                    int firstPeriodIndex = subjectType.getFirstPeriodIndex();

                    dayInt = subjectType.getFirstDayIndex();
                    hourInt = MainActivity.timetable.getHour(firstPeriodIndex);
                    minuteInt = MainActivity.timetable.getMinute(firstPeriodIndex);

                    DiaryEntryMakerAndSaver.makeAndSaveDiaryEntry(editText, dayInt, hourInt
                            , minuteInt, getContext());
                }
            }
        }
    }

    private void updateDiaryEntryListView() { // TODO USE IT
        for (DiaryEntry diaryEntry : DiaryEntry.loadDiaries(getContext())) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView entryDate = new TextView(getContext());
            String date = diaryEntry.getDayOfMonth() + "/" + diaryEntry.getMonthOfYear() + "/"
                    + Calendar.YEAR + " - " + diaryEntry.getHourOfDay() + ":" + diaryEntry.getMinuteOfHour();
            entryDate.setText(date);
            entryDate.setTypeface(null, Typeface.BOLD);
            entryDate.setLayoutParams(layoutParams);

            TextView entryText = new TextView(getContext());
            entryText.setText(diaryEntry.getText());
            entryText.setLayoutParams(layoutParams);

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(entryDate);
            linearLayout.addView(entryText);
            linearLayout.setLayoutParams(layoutParams);

            listviewDiaryEntries.removeAllViewsInLayout();
            listviewDiaryEntries.addView(linearLayout);
        }

    }
}