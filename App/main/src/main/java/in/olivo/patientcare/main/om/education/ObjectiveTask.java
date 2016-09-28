package in.olivo.patientcare.main.om.education;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.olivo.patientcare.main.om.BaseTask;

/**
 * Created by Satya Madala on 14/7/16.
 * email : satya.madala@olivo.in
 */
public class ObjectiveTask  extends BaseTask{
    @SerializedName("data")
    private ObjectiveData payload;

    public ObjectiveData getPayload() {
        return payload;
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("data", this.getPayload().getDataToPatch());
        data.put("state", this.getState().toString());
        return data;
    }


    public class ObjectiveData{

        /**
         * when : 2016-07-13T20:22:19+00:00
         * question : Do you have headache?
         * id : 34d9591e515141ea9b17e27c6ce86e91
         * back_ground_image : null
         * options : [{"id":"f40e7075c71e4a48abe6b5fd05f7bacd","value":"Maybe"},{"id":"0f8cca2fbae9425ba7ab44ffebee7b85","value":"No"},{"id":"8b0bbcd6764340039473c7fa989dc366","value":"Yes"}]
         */

        private String when;
        private String question;
        private String id;
        @SerializedName("back_ground_image")
        private String backGroundImageURL;

        private String answerId;
        /**
         * id : f40e7075c71e4a48abe6b5fd05f7bacd
         * value : Maybe
         */

        private List<Option> options;

        public void setWhen(String when) {
            this.when = when;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setBackGroundImageURL(String backGroundImageURL) {
            this.backGroundImageURL = backGroundImageURL;
        }

        public void setOptions(List<Option> options) {
            this.options = options;
        }

        public String getWhen() {
            return when;
        }

        public String getQuestion() {
            return question;
        }

        public String getId() {
            return id;
        }

        public Object getBackGroundImageURL() {
            return backGroundImageURL;
        }

        public List<Option> getOptions() {
            return options;
        }

        public void setAnswerId(String answerId) {
            this.answerId = answerId;
        }

        public String getAnswerId() {
            return answerId;
        }

        public class Option {
            private String id;
            private String value;

            public void setId(String id) {
                this.id = id;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getId() {
                return id;
            }

            public String getValue() {
                return value;
            }
        }
        public JSONObject getDataToPatch() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", this.getId());
            jsonObject.put("answer_id", this.getAnswerId());
            return jsonObject; //No education card specific data to patch
        }
    }
    public class ObjectiveData1 {
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

        public String getSummary() {
            return summary;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public JSONObject getDataToPatch() throws JSONException {
            return null; //No education card specific data to patch
        }
    }
}
