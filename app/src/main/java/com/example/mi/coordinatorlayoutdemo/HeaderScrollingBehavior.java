package com.example.mi.coordinatorlayoutdemo;

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
    private WeakReference<View> topsite;
    private Scroller scroller;
    private Handler handler;

    public HeaderScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
        handler = new Handler();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
        ViewPager viewPager = (ViewPager) ((CoordinatorLayout) parent).getParent();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) viewPager.getParent();
        View view = coordinatorLayout.getChildAt(0);
        headcard = new WeakReference<>(view);
        topsite = new WeakReference<>(dependency);
        return true;
    }

//    @Override
//    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
//        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
//        if (lp.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
//            child.layout(0, 0, parent.getWidth(), (int) (parent.getHeight() + 420));
//            return true;
//        }
//        return super.onLayoutChild(parent, child, layoutDirection);
//    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
        if (ScrollOrietationUtils.getInstance().isChangePage()) {
            return false;
        }

        Log.e("LWQ", "onDependentViewChanged");
        Resources resources = getHeadcard().getResources();
        final float progress = 1 + getHeadcard().getTranslationY() / 300;

        Log.e("LWQ", "!!!!!!!!!!!!!!!!!!!!!" + getHeadcard().getTranslationY());
        Log.e("LWQ", "!!!!!!!!!!!!!!!!!!!!!progress " + progress);
        Log.e("LWQ", "!!!!!!!!!!!!!!!!!!!!!" + 420 * progress);
        child.setTranslationY(420 * progress);

//        getTopSite().setTranslationY(50 * (progress-1));
//        getTopSite().setTranslationY(-420 * progress);
        float scale = 1 + 0.4f * (1.f - progress);
//        dependency.setScaleX(scale);
//        dependency.setScaleY(scale);

        dependency.setAlpha(progress);
        dependency.setTranslationY(-50 * (1 - progress));

        return true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
        if (ScrollOrietationUtils.getInstance().isChangePage()) {
            return false;
        }
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
        if (ScrollOrietationUtils.getInstance().isChangePage()) {
            return;
        }
        scroller.abortAnimation();
        isScrolling = false;
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dx, int dy, int[] consumed) {
//        if (dy < 0) {
//            return;
//        }
        if (ScrollOrietationUtils.getInstance().isChangePage()) {
            return;
        }
        View dependentView = getHeadcard();
        float newTranslateY = dependentView.getTranslationY() - dy;
        float minHeaderTranslate = -(dependentView.getHeight() - getDependentViewCollapsedHeight());

        Log.e("LWQ", "newTranslateY:" + newTranslateY);
        Log.e("LWQ", "minHeaderTranslate:" + minHeaderTranslate);
        if (newTranslateY > 0) {
            return;
        }
        if (newTranslateY > minHeaderTranslate) {
            ((ViewPager) coordinatorLayout.getParent()).setTranslationY(newTranslateY);
            //移动头图
            Log.e("LWQ", "dependentView.setTranslationY(newTranslateY);");
            dependentView.setTranslationY(newTranslateY);
//            if (getTopSite() != null) {
//                getTopSite().setTranslationY(newTranslateY);
//            }
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
//        if (dyUnconsumed > 0) {
//            return;
//        }
//
//        View dependentView = getHeadcard();
//        float newTranslateY = dependentView.getTranslationY() - dyUnconsumed;
//        final float maxHeaderTranslate = 0;
//
//        if (newTranslateY < maxHeaderTranslate) {
//            dependentView.setTranslationY(newTranslateY);
//        }
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, float velocityX, float velocityY) {
        if (ScrollOrietationUtils.getInstance().isChangePage()) {
            return false;
        }
        return onUserStopDragging(velocityY);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target) {
        if (ScrollOrietationUtils.getInstance().isChangePage()) {
            return;
        }
        if (!isScrolling) {
            onUserStopDragging(800);
        }
    }

    private boolean onUserStopDragging(float velocity) {
        View dependentView = getHeadcard();
        float translateY = dependentView.getTranslationY();
        float minHeaderTranslate = -(dependentView.getHeight() - getDependentViewCollapsedHeight());

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

        scroller.startScroll(0, (int) translateY, 0, (int) (targetTranslateY - translateY), (int) (1000000 / Math.abs(velocity)));
        handler.post(flingRunnable);
        isScrolling = true;
        return true;
    }

    private float getDependentViewCollapsedHeight() {
        return getHeadcard().getResources().getDimension(R.dimen.topsite_height);
    }

    private View getHeadcard() {
        return headcard.get();
    }

    private View getTopSite() {
        return topsite.get();
    }

    private Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                getHeadcard().setTranslationY(scroller.getCurrY());
                Log.e("LWQ", "getHeadcard().setTranslationY(scroller.getCurrY()");
                handler.post(this);
            } else {
                isExpanded = getHeadcard().getTranslationY() != 0;
                isScrolling = false;
            }
        }
    };

}
