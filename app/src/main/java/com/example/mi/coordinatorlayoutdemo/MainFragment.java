package com.example.mi.coordinatorlayoutdemo;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends android.support.v4.app.Fragment {

    private ViewPager mNewsFlowViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private FrameLayout topsite;
    private ImageView topsiteBg;
    private LinearLayout mTextView;
    private ImageView mImageView;

    public MainFragment() {
        // Required empty public constructor
    }

    private MyPagerAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mNewsFlowViewPager = root.findViewById(R.id.news_flow_view_pager);
        mSlidingTabLayout = root.findViewById(R.id.news_flow_tab);

        for (String title : mTitles) {
            mFragments.add(new NewsFlowFragment());
        }

        mAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        mNewsFlowViewPager.setAdapter(mAdapter);
        mSlidingTabLayout.setViewPager(mNewsFlowViewPager);

        topsite = root.findViewById(R.id.topsite);
        topsiteBg = root.findViewById(R.id.topsite_bg);
        return root;
    }

    public void resetRV() {
        mSlidingTabLayout.setTranslationY(600);
        mNewsFlowViewPager.setTranslationY(600 + 150);
        for (android.support.v4.app.Fragment fragment : mFragments) {
            ((NewsFlowFragment) fragment).resetToTop();
        }
        topsite.setTranslationY(0);
        topsiteBg.setAlpha(0f);
        ScrollOrietationUtils.getInstance().setCanPullDown(true);
    }

    private ArrayList<android.support.v4.app.Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {
            "For you", "Entertainment", "CrickBall", "For you"
    };

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
