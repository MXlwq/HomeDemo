package com.example.mi.coordinatorlayoutdemo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout mTextView;
    private ImageView mImageView;

    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;

    private int mCurrentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("LWQ", "positionOffset:" + positionOffset);
                Log.e("LWQ", "positionOffsetPixels:" + positionOffsetPixels);
                Log.e("LWQ", "position:" + position);
                if (mCurrentIndex == position) {
                    if (mMainFragment != null) {
                        mMainFragment.moveEditTextPosition(positionOffset);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("LWQ", "onPageSelected" + position);
                mCurrentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("LWQ", "state:" + state);
                if (state == 1) {
                    ScrollOrietationUtils.getInstance().setChangePage(true);
                } else if (state == 0) {
                    ScrollOrietationUtils.getInstance().setChangePage(false);
                }
            }
        });
        mViewPager.setOffscreenPageLimit(2);
        mCircleIndicator.setViewPager(mViewPager);
    }


    class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        /**
         * 根据id生成fragment，写好这个就好了
         *
         * @param position
         * @return
         */
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment fragment = null;

            if (position == 0) {
                fragment = new MainFragment();
                mMainFragment = (MainFragment) fragment;
            } else if (position == 1) {
                fragment = new SecondFragment();
            }
            return fragment;
        }

        /**
         * 可以使已知数，也可以是一个集合的长度
         *
         * @return
         */
        @Override
        public int getCount() {
            return 2;
        }
    }

    private MainFragment mMainFragment;
}
