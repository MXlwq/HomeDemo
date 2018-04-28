package com.example.mi.coordinatorlayoutdemo;

public class ScrollOrietationUtils {
    private static ScrollOrietationUtils mInstance;

    private ScrollOrietationUtils() {

    }

    public static ScrollOrietationUtils getInstance() {
        if (mInstance == null) {
            mInstance = new ScrollOrietationUtils();
        }
        return mInstance;
    }

    private boolean isChangePage;
    private boolean canPullDown = true;

    public boolean isChangePage() {
        return isChangePage;
    }

    public void setChangePage(boolean changePage) {
        isChangePage = changePage;
    }

    public boolean isCanPullDown() {
        return canPullDown;
    }

    public void setCanPullDown(boolean canPullDown) {
        this.canPullDown = canPullDown;
    }
}
