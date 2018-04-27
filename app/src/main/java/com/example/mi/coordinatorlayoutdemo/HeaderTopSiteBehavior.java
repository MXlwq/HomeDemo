package com.example.mi.coordinatorlayoutdemo;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import java.lang.ref.WeakReference;

/**
 * Created by cyandev on 2016/12/14.
 */
public class HeaderTopSiteBehavior extends CoordinatorLayout.Behavior<View> {

    private boolean isExpanded = false;
    private boolean isScrolling = false;

    private WeakReference<View> dependentView;
    private Scroller scroller;
    private Handler handler;

    public HeaderTopSiteBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
        handler = new Handler();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (dependency != null && dependency.getId() == R.id.scrolling_header) {
            dependentView = new WeakReference<>(dependency);
            return true;
        }
        return false;
    }

//    @Override
//    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
//        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
//        if (lp.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
//            child.layout(0, 0, parent.getWidth(), (int) (parent.getHeight() - getDependentViewCollapsedHeight()));
//            return true;
//        }
//        return super.onLayoutChild(parent, child, layoutDirection);
//    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        Resources resources = getDependentView().getResources();
        final float progress = 1.f -
                Math.abs(dependency.getTranslationY() / (dependency.getHeight() - resources.getDimension(R.dimen.collapsed_header_height)));

        Log.e("LWQ", "dependency.getTranslationY():" + dependency.getTranslationY());
        float translate = dependency.getHeight() + dependency.getTranslationY();
        Log.e("LWQ", "translate:" + translate);
        Log.e("LWQ", "progress:" + progress);
        child.setTranslationY(translate - 50 * (1 - progress));

//        float scale = 1 - 0.4f * (1.f - progress);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        // params.setMargins(dip2px(MainActivity.this, 1), 0, 0, 0); // 可以实现设置位置信息，如居左距离，其它类推
        // params.leftMargin = dip2px(MainActivity.this, 1);
//        child.setLayoutParams(params);

//        dependency.setAlpha(progress);

        return true;
    }

    private float getDependentViewCollapsedHeight() {
        return getDependentView().getResources().getDimension(R.dimen.topsite_height);
    }

    private View getDependentView() {
        return dependentView.get();
    }


}
