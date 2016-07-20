package in.olivo.patientcare.main.om.medicines;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import in.olivo.patientcare.main.om.BaseTask;

/**
 * Created by Satya Madala on 11/7/16.
 * email : satya.madala@olivo.in
 */
public class RecommendationTask extends BaseTask {
    @SerializedName("data")
    private RecommendationData payload;

    public RecommendationData getPayload() {
        return payload;
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("data", this.getPayload().getDataToPatch());
        data.put("state", this.getState().toString());
        return data;
    }

    public class RecommendationData {

        @SerializedName("message")
        private String message;

        @SerializedName("image_path")
        private String imagePath;

        public String getMessage() {
            return message;
        }

        public String getImagePath() {
            return imagePath;
        }

        public JSONObject getDataToPatch() throws JSONException {
            JSONObject data = new JSONObject();
            data.put("message", this.message);

            data.put("image_path", this.imagePath);
            return data;
        }
    }
}
