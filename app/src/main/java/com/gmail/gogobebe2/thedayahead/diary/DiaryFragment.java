package com.gmail.gogobebe2.thedayahead.diary;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gmail.gogobebe2.thedayahead.R;
import com.gmail.gogobebe2.thedayahead.TheDayAheadFragment;

public class DiaryFragment extends TheDayAheadFragment implements View.OnClickListener {
    public DiaryFragment() {/* Required empty public constructor*/}

    private LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Inflate the layout for this fragment
        linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_diary, container, false);;
        return linearLayout;
    }

    @NonNull
    @Override
    protected String getTitle() {
        return "Diary";
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_saveAndSetDateAndTimeDiary) {
            EditText editText = (EditText) linearLayout.findViewById(R.id.editText_diary);
            String text = editText.getText().toString();
            int dayInt = -1;
            int hourInt = -1;
            int minuteInt = -1;
            Diary diary = new Diary(text, dayInt,hourInt, minuteInt);
        }
    }
}