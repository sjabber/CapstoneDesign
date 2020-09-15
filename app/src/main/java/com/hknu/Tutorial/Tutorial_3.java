package com.hknu.Tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.hknu.capstone_design.R;

public class Tutorial_3 extends Fragment {
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static Tutorial_3 newInstance(int page, String title) {
        Tutorial_3 fragment = new Tutorial_3();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorial_1, container, false);
        EditText tvLabel = (EditText) view.findViewById(R.id.editText);
        tvLabel.setText(page + " -- " + title);
        return view;
    }
}
