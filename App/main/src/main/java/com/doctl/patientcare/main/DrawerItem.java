package com.doctl.patientcare.main;

/**
 * Created by Administrator on 11/26/2014.
 */
public class DrawerItem {
    private String title;
    private String ItemName;
    private int imgResID;
    private boolean isProfile;

    public DrawerItem(String itemName, int imgResID) {
        ItemName = itemName;
        this.imgResID = imgResID;
    }

    public DrawerItem(boolean isProfile) {
        this(null, 0);
        this.isProfile = isProfile;
    }

    public DrawerItem(String title) {
        this(null, 0);
        this.title = title;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getImgResID() {
        return imgResID;
    }

    public void setImgResID(int imgResID) {
        this.imgResID = imgResID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isProfile() {
        return isProfile;
    }
}
