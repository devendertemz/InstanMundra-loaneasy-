package com.loaneasy.Drawer;

/**
 * Created by Ravindra on 22-07-2017.
 */

public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private String icons;

    public NavDrawerItem() {
    }

    public NavDrawerItem(boolean showNotify, String title, String icons) {
        this.showNotify = showNotify;
        this.title = title;
        this.icons = icons;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcons() {
        return icons;
    }

    public void setIcons(String icons) {
        this.icons = icons;
    }
}
