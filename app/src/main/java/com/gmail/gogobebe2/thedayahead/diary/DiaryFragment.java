package com.gmail.gogobebe2.thedayahead.diary;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.gogobebe2.thedayahead.R;
import com.gmail.gogobebe2.thedayahead.TheDayAheadFragment;

public class DiaryFragment extends TheDayAheadFragment {
    public DiaryFragment() {/* Required empty public constructor*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @NonNull
    @Override
    protected String getTitle() {
        return "Diary";
    }
}