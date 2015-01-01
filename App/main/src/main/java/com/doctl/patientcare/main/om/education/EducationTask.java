package com.doctl.patientcare.main.om.education;

import com.doctl.patientcare.main.om.BaseTask;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leak on 12/17/2014.
 */
public class EducationTask extends BaseTask{
    @SerializedName("data")
    private EducationData payload;

    public EducationData getPayload() {
        return payload;
    }

    public class EducationData{
        @SerializedName("educationId")
        private String educationId;

        @SerializedName("planId")
        private String planId;

        @SerializedName("index")
        private String index;

        @SerializedName("title")
        private String title;

        @SerializedName("url")
        private String url;

        @SerializedName("summary")
        private String summary;

        @SerializedName("thumbnail")
        private String thumbnail;

        private String timestamp;

        public String getEducationId() {
            return educationId;
        }

        public String getPlanId() {
            return planId;
        }

        public String getIndex() {
            return index;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getSummary() { return summary; }

        public  String getThumbnail() { return thumbnail; }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public JSONObject getDataToPatch() throws JSONException{
            return null; //No education card specific data to patch
        }
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("data", this.getPayload().getDataToPatch());
        data.put("state", this.getState().toString());
        return data;
    }
}
