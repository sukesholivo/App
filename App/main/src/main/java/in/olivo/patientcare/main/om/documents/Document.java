package in.olivo.patientcare.main.om.documents;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.olivo.patientcare.main.om.UserProfile;

/**
 * Created by Administrator on 4/27/2015.
 */
public class Document {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    @SerializedName("description")
    private String description;

    @SerializedName("timeStamp")
    private Date timeStamp;

    @SerializedName("uploadedBy")
    private UserProfile uploadedBy;

    @SerializedName("isFavorite")
    private boolean isFavorite;

    private String category;

    public static Set<Integer> getCategoriesStartingIndex(List<Document> documentsOrderByCategory) {


        Set<Integer> categoriesStartingIndex = new HashSet<>();
        if (documentsOrderByCategory == null || documentsOrderByCategory.isEmpty())
            return categoriesStartingIndex;

        String currCategory = documentsOrderByCategory.get(0).category;

        categoriesStartingIndex.add(0);
        for (int i = 1; i < documentsOrderByCategory.size(); i++) {
            if ((currCategory == null && documentsOrderByCategory.get(i).category != null) || (currCategory != null && !currCategory.equalsIgnoreCase(documentsOrderByCategory.get(i).getCategory()))) {
                categoriesStartingIndex.add(i);
                currCategory = documentsOrderByCategory.get(i).getCategory();
            }
        }
        return categoriesStartingIndex;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public UserProfile getUploadedBy() {
        return uploadedBy;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getCategory() {
        return category;
    }
}
