package in.olivo.patientcare.main.om.followup;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.olivo.patientcare.main.om.BaseTask;

/**
 * Created by Administrator on 8/9/2014.
 */
public class FollowupTask extends BaseTask {
    @SerializedName("data")
    private FollowupData payload;

    public FollowupData getPayload() {
        return payload;
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("data", this.getPayload().getDataToPatch());
        data.put("state", this.getState().toString());
        return data;
    }

    public class FollowupData {
        @SerializedName("followupId")
        private String followupId;

        @SerializedName("planId")
        private String planId;

        @SerializedName("type")
        private String type;

        @SerializedName("title")
        private String title;

        @SerializedName("notes")
        private String notes;

        @SerializedName("multipleChoice")
        private boolean multipleChoice;

        @SerializedName("options")
        private ArrayList<String> options;

        @SerializedName("selected")
        private ArrayList<Integer> selected;

        @SerializedName("getComments")
        private boolean getComments;

        @SerializedName("comment")
        private String comment;

        private String timestamp;

        public String getFollowupId() {
            return followupId;
        }

        public String getPlanId() {
            return planId;
        }

        public String getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public String getNotes() {
            return notes;
        }

        public boolean isMultipleChoice() {
            return multipleChoice;
        }

        public ArrayList<String> getOptions() {
            return options;
        }

        public ArrayList<Integer> getSelected() {
            return selected;
        }

        public void setSelected(ArrayList<Integer> selected) {
            this.selected = selected;
        }

        public boolean isGetComments() {
            return getComments;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public JSONObject getDataToPatch() throws JSONException {
            JSONObject data = new JSONObject();
            data.put("followupPlanId", this.getPlanId());
            data.put("followupId", this.getFollowupId());
            data.put("timestamp", this.getTimestamp());
            ArrayList<String> selected = new ArrayList<>();
            if (this.getType().toLowerCase().equals("objective")) {
                for (int i : this.getSelected()) {
                    selected.add("" + (this.getOptions().get(i)));
                }
            } else if (!this.getType().toLowerCase().equals("subjective")) {
                for (int i : this.getSelected()) {
                    selected.add("" + (i + 1));
                }
            }
            data.put("selected", new JSONArray(selected));
            data.put("notes", this.getComment());
            return data;
        }
    }
}
