package com.gmail.gogobebe2.thedayahead.diary;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.gmail.gogobebe2.thedayahead.MainActivity;
import com.gmail.gogobebe2.thedayahead.R;
import com.gmail.gogobebe2.thedayahead.TheDayAheadFragment;
import com.gmail.gogobebe2.thedayahead.timetable.SubjectType;

public class DiaryFragment extends TheDayAheadFragment implements View.OnClickListener {
    public DiaryFragment() {/* Required empty public constructor*/}

    private LinearLayout fragmentDiary;
    private LinearLayout fragmentDiaryDialogByPeriod;
    private LinearLayout fragmentDiaryDialogByDateAndTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Inflate the layouts for this fragment
        fragmentDiaryDialogByDateAndTime = (LinearLayout) inflater.inflate(R.layout.fragment_diary_dialog_bydateandtime, container, false);
        fragmentDiaryDialogByPeriod = (LinearLayout) inflater.inflate(R.layout.fragment_diary_dialog_byperiod, container, false);
        // TODO put subjects into periodSelector.
        fragmentDiary = (LinearLayout) inflater.inflate(R.layout.fragment_diary, container, false);
        return fragmentDiary;
    }

    @NonNull
    @Override
    protected String getTitle() {
        return "Diary";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_pickDateOrPeriod:
                Button buttonPickDateOrPeriod = (Button) view;
                String buttonTitle = "Pick ";
                int dayInt;
                int hourInt;
                int minuteInt;
                if (((Switch) buttonPickDateOrPeriod.findViewById(R.id.switch_dateTimeOrPeriod)).isChecked()) {
                    if (MainActivity.timetable == null) {
                        Toast.makeText(view.getContext(), "You need to create a timetable instance by logging in on the \"Timetable\" tab", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fragmentDiaryDialogByPeriod.setVisibility(View.VISIBLE);
                    fragmentDiaryDialogByDateAndTime.setVisibility(View.GONE);
                    buttonTitle += "Period";

                    SubjectType subjectType = null; // TODO
                    int firstPeriodIndex = subjectType.getFirstPeriodIndex();

                    dayInt = subjectType.getFirstDayIndex();
                    hourInt = MainActivity.timetable.getHour(firstPeriodIndex);
                    minuteInt = MainActivity.timetable.getMinute(firstPeriodIndex);
                }
                else {
                    fragmentDiaryDialogByPeriod.setVisibility(View.GONE);
                    fragmentDiaryDialogByDateAndTime.setVisibility(View.VISIBLE);
                    buttonTitle += "Date/Time";

                    dayInt = -1; // TODO
                    hourInt = -1; // TODO
                    minuteInt = -1; // TODO
                }
                buttonPickDateOrPeriod.setText(buttonTitle);

                EditText editText = (EditText) fragmentDiary.findViewById(R.id.editText_diary);

                String text = editText.getText().toString();

                Diary diary = new Diary(text, dayInt, hourInt, minuteInt);
                diary.saveDiary();
                break;

        }
    }
}