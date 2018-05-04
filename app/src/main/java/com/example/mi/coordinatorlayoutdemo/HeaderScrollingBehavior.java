package com.example.mi.coordinatorlayoutdemo;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import java.lang.ref.WeakReference;

/**
 * Created by cyandev on 2016/11/3.
 */
public class HeaderScrollingBehavior extends CoordinatorLayout.Behavior<RecyclerView> {

    private boolean isExpanded = false;
    private boolean isScrolling = false;

    private WeakReference<View> headcard;
    private WeakReference<View> headcardBackground;
    private WeakReference<ViewPager> viewpager;
    private WeakReference<View> topsite;
    private Scroller scroller;
    private Scroller mRVScroller;
    private Handler handler;

    private RecyclerView mRecyclerView;

    public HeaderScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
        mRVScroller = new Scroller(context);
        handler = new Handler();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
        mRecyclerView = child;
        ViewPager viewPager = (ViewPager) ((CoordinatorLayout) parent).getParent();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) viewPager.getParent();
        headcard = new WeakReference<>(coordinatorLayout.getChildAt(0));
        headcardBackground = new WeakReference<>(coordinatorLayout.getChildAt(1));
        viewpager = new WeakReference<>(viewPager);
        topsite = new WeakReference<>(dependency);
        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
            child.layout(0, 0, parent.getWidth(), (int) (parent.getHeight()));
            return true;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    private boolean init = true;

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
        if (init) {
            child.setTranslationY(420);
            dependency.setTranslationY(0);
            init = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
        scroller.abortAnimation();
        mRVScroller.abortAnimation();
        isScrolling = false;
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dx, int dy, int[] consumed) {
        View dependentView = getHeadcard();
        //防止过度下滑
        if (dependentView.getTranslationY() - dy > 0) {
            return;
        }
        if (dependentView.getTranslationY() - dy >= -300 && ScrollOrietationUtils.getInstance().isCanPullDown()) {
            //移动ViewPager
            ((ViewPager) coordinatorLayout.getParent()).setTranslationY((float) (((ViewPager) coordinatorLayout.getParent()).getTranslationY() - dy * 28.5 / 70.5));
            child.setTranslationY((float) (child.getTranslationY() - dy * 42 / 70.5));
            //移动头图
            dependentView.setTranslationY((float) (dependentView.getTranslationY() - (float) dy * 28.5 / 70.5));
            getHeadcardBackground().setTranslationY((float) (dependentView.getTranslationY() - (float) dy * 28.5 / 70.5));
            //移动Topsite
            getTopSite().setTranslationY(dependentView.getTranslationY() / 3 + 0);
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target) {
        if (!isScrolling) {
            onUserStopDragging(800);
        }
    }

    private boolean onUserStopDragging(float velocity) {
        if (!ScrollOrietationUtils.getInstance().isCanPullDown() || ScrollOrietationUtils.getInstance().isChangePage()) {
            return false;
        }
        View dependentView = getHeadcard();
        float translateY = dependentView.getTranslationY();
        float RVtranslateY = mRecyclerView.getTranslationY();
        float minHeaderTranslate = -(dependentView.getHeight() - getDependentViewCollapsedHeight());
        float minRVHeaderTranslate = 420;

        if (translateY == 0 || translateY == minHeaderTranslate) {
            return false;
        }

        boolean targetState; // Flag indicates whether to expand the content.
        if (Math.abs(velocity) <= 800) {
            if (Math.abs(translateY) < Math.abs(translateY - minHeaderTranslate)) {
                targetState = false;
            } else {
                targetState = true;
            }
            velocity = 800; // Limit velocity's minimum value.
        } else {
            if (velocity > 0) {
                targetState = true;
            } else {
                targetState = false;
            }
        }

        float targetTranslateY = targetState ? minHeaderTranslate : 0;
        float rvtargetTranslateY = targetState ? 0 : minRVHeaderTranslate;
        getTopSite().setTranslationY(0);
        scroller.startScroll(0, (int) translateY, 0, (int) (targetTranslateY - translateY), (int) (100000 / Math.abs(velocity)));
        mRVScroller.startScroll(0, (int) RVtranslateY, 0, (int) (rvtargetTranslateY - RVtranslateY), (int) (100000 / Math.abs(velocity)));
        handler.post(flingRunnable);
        handler.post(RVflingRunnable);
        isScrolling = true;
        return true;
    }


    private float getDependentViewCollapsedHeight() {
        return getHeadcard().getResources().getDimension(R.dimen.topsite_height);
    }

    private View getHeadcard() {
        return headcard.get();
    }

    private View getHeadcardBackground() {
        return headcardBackground.get();
    }

    private View getViewPager() {
        return viewpager.get();
    }

    private View getTopSite() {
        return topsite.get();
    }


    private Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                getHeadcard().setTranslationY(scroller.getCurrY());
                getHeadcardBackground().setTranslationY(scroller.getCurrY());
                getViewPager().setTranslationY(scroller.getCurrY());
                handler.post(this);
            } else {
                isExpanded = getHeadcard().getTranslationY() != 0;
                isScrolling = false;
            }
        }
    };

    private Runnable RVflingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRVScroller.computeScrollOffset()) {
                mRecyclerView.setTranslationY((float) (mRVScroller.getCurrY()));
                if (mRVScroller.getCurrY() <= 0) {
                    ScrollOrietationUtils.getInstance().setCanPullDown(false);
                    ((MyViewPager) getViewPager()).setPagingEnabled(false);
                } else {
                    ScrollOrietationUtils.getInstance().setCanPullDown(true);
                    ((MyViewPager) getViewPager()).setPagingEnabled(true);
                }
                handler.post(this);
            } else {
                isExpanded = getHeadcard().getTranslationY() != 0;
                isScrolling = false;
            }
        }
    };
}
