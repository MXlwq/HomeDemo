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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.lang.ref.WeakReference;

/**
 * Created by cyandev on 2016/11/3.
 */
public class HeaderScrollingBehavior extends CoordinatorLayout.Behavior<ViewPager> {

    private boolean isScrolling = false;

    private WeakReference<View> headcard;
    private WeakReference<View> headcardBackground;
    private WeakReference<ViewPager> viewpager;
    private WeakReference<View> topsite;
    private WeakReference<View> topsiteBg;
    private WeakReference<View> tabs;
    private Scroller scroller;
    private Scroller mRVScroller;
    private Scroller mTabScroller;
    private Scroller mTopsiteScroller;
    private Handler handler;

    private ViewPager mRecyclerView;

    public HeaderScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
        mRVScroller = new Scroller(context);
        mTabScroller = new Scroller(context);
        mTopsiteScroller = new Scroller(context);
        handler = new Handler();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewPager child, View dependency) {
        if (dependency.getId() == R.id.topsite) {
            mRecyclerView = child;
            ViewPager viewPager = (ViewPager) ((CoordinatorLayout) parent).getParent();
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) viewPager.getParent();
            headcard = new WeakReference<>(coordinatorLayout.getChildAt(0));
            headcardBackground = new WeakReference<>(coordinatorLayout.getChildAt(1));
            tabs = new WeakReference<>(parent.getChildAt(1));
            viewpager = new WeakReference<>(viewPager);
            topsite = new WeakReference<>(dependency);
            topsiteBg = new WeakReference<>(((FrameLayout) dependency).getChildAt(1));
        }
        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, ViewPager child, int layoutDirection) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
            child.layout(0, 0, parent.getWidth(), (int) (parent.getHeight() + 150));
            return true;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    private boolean init = true;

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewPager child, View dependency) {
        if (init) {
            getTabs().setTranslationY(600);
            child.setTranslationY(600 + 150);
            dependency.setTranslationY(0);
            init = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, ViewPager child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, ViewPager child, View directTargetChild, View target, int nestedScrollAxes) {
        scroller.abortAnimation();
        mRVScroller.abortAnimation();
        mTabScroller.abortAnimation();
        mTopsiteScroller.abortAnimation();
        isScrolling = false;
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, ViewPager child, View target, int dx, int dy, int[] consumed) {
        View dependentView = getHeadcard();
        //防止过度下滑
        if (dependentView.getTranslationY() - dy > 0) {
            return;
        }
        if (dependentView.getTranslationY() >= -300 && ScrollOrietationUtils.getInstance().isCanPullDown()) {
            float headCardTransYRatio = (float) (28.5 / 85.5);
            //移动ViewPager
            ((ViewPager) coordinatorLayout.getParent()).setTranslationY((float) (((ViewPager) coordinatorLayout.getParent()).getTranslationY() - dy * headCardTransYRatio));
            getTabs().setTranslationY((float) (getTabs().getTranslationY() - dy * (1 - headCardTransYRatio)));
            child.setTranslationY((float) (child.getTranslationY() - dy * (1 - headCardTransYRatio)));
            //移动头图
            dependentView.setTranslationY((float) (dependentView.getTranslationY() - (float) dy * headCardTransYRatio));
            getHeadcardBackground().setTranslationY(dependentView.getTranslationY());
            //移动Topsite
            getTopSite().setTranslationY(dependentView.getTranslationY());
            Log.e("LWQ", "-dependentView.getTranslationY() / 300:" + -dependentView.getTranslationY() / 300);
            getTopSiteBg().setAlpha(-dependentView.getTranslationY() / 300);
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, ViewPager child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, ViewPager child, View target) {
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
        float tabtranslateY = getTabs().getTranslationY();
        float topsitetranslateY = getTopSite().getTranslationY();
        float minHeaderTranslate = -(dependentView.getHeight() - getDependentViewCollapsedHeight());
        float minRVHeaderTranslate = 600 + 150;
        float minTabHeaderTranslate = 600;
        float minTopsiteHeaderTranslate = 0;

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
        float rvtargetTranslateY = targetState ? 150 : minRVHeaderTranslate;
        float tabtargetTranslateY = targetState ? 0 : minTabHeaderTranslate;
        float topsitetargetTranslateY = targetState ? 0 : minTopsiteHeaderTranslate;

        startScroll(scroller, flingRunnable, translateY, targetTranslateY, 300000 / Math.abs(velocity));
        startScroll(mRVScroller, RVflingRunnable, RVtranslateY, rvtargetTranslateY, 300000 / Math.abs(velocity));
        startScroll(mTabScroller, tabflingRunnable, tabtranslateY, tabtargetTranslateY, 300000 / Math.abs(velocity));
        startScroll(mTopsiteScroller, topsiteflingRunnable, topsitetranslateY, topsitetargetTranslateY, 300000 / Math.abs(velocity));
        isScrolling = true;
        return true;
    }


    private void startScroll(Scroller scroller, Runnable runnable, float start, float end, float speed) {
        scroller.startScroll(0, (int) start, 0, (int) (end - start), (int) speed);
        handler.post(runnable);
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

    private View getTabs() {
        return tabs.get();
    }

    private View getViewPager() {
        return viewpager.get();
    }

    private View getTopSite() {
        return topsite.get();
    }

    private View getTopSiteBg() {
        return topsiteBg.get();
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
                isScrolling = false;
            }
        }
    };

    private Runnable RVflingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRVScroller.computeScrollOffset()) {
                mRecyclerView.setTranslationY((float) (mRVScroller.getCurrY()));
                if (mRVScroller.getCurrY() <= 150) {
                    ScrollOrietationUtils.getInstance().setCanPullDown(false);
                    ((MyViewPager) getViewPager()).setPagingEnabled(false);
                } else {
                    ScrollOrietationUtils.getInstance().setCanPullDown(true);
                    ((MyViewPager) getViewPager()).setPagingEnabled(true);
                }
                handler.post(this);
            } else {
                isScrolling = false;
            }
        }
    };
    private Runnable tabflingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTabScroller.computeScrollOffset()) {
                getTabs().setTranslationY((float) (mTabScroller.getCurrY()));
                handler.post(this);
            } else {
                isScrolling = false;
            }
        }
    };

    private Runnable topsiteflingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTopsiteScroller.computeScrollOffset()) {
                getTopSite().setTranslationY((float) (mTopsiteScroller.getCurrY()));
                getTopSiteBg().setAlpha(-(float) (mTopsiteScroller.getCurrY()) / 300);
                handler.post(this);
            } else {
                isScrolling = false;
            }
        }
    };
}
