package com.example.mi.coordinatorlayoutdemo;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends android.support.v4.app.Fragment {

    private String mTitle;

    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_second2, container, false);
        TextView title = (TextView) v.findViewById(R.id.card_title_tv);
        title.setText(mTitle);
        return v;
    }

    public static android.support.v4.app.Fragment getInstance(String title) {
        SecondFragment sf = new SecondFragment();
        sf.mTitle = title;
        return sf;
    }
}
