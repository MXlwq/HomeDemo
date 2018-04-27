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

    public boolean isChangePage() {
        return isChangePage;
    }

    public void setChangePage(boolean changePage) {
        isChangePage = changePage;
    }
}
