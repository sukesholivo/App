package in.olivo.patientcare.main;

/**
 * Created by Administrator on 11/26/2014.
 */
public class DrawerItem {
    private int id;
    private String title;
    private String ItemName;
    private int imgResID;
    private boolean isProfile;

    public DrawerItem(int id, String itemName, int imgResID) {
        this.id = id;
        ItemName = itemName;
        this.imgResID = imgResID;
    }

    public DrawerItem(int id, boolean isProfile) {
        this(id, null, 0);
        this.isProfile = isProfile;
    }

    public DrawerItem(int id, String title) {
        this(id, null, 0);
        this.title = title;
    }

    public int getId() {
        return id;
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
