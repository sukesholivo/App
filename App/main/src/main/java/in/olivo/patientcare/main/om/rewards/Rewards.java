package in.olivo.patientcare.main.om.rewards;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2/26/2015.
 */
public class Rewards {
    @SerializedName("by_clinic")
    private List<Reward> byClinic;

    @SerializedName("by_doctl")
    private List<Reward> byDoctl;

    @SerializedName("totalPoints")
    private int totalPoints;

    public List<Reward> getByClinic() {
        return byClinic;
    }

    public List<Reward> getByDoctl() {
        return byDoctl;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public class Reward {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("points")
        private int points;

        @SerializedName("isActive")
        private boolean isActive;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getPoints() {
            return points;
        }

        public boolean isActive() {
            return isActive;
        }
    }
}
