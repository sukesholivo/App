package in.olivo.patientcare.main.om.message;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import in.olivo.patientcare.main.om.BaseTask;

/**
 * Created by Administrator on 12/19/2014.
 */
public class MessageTask extends BaseTask {
    @SerializedName("data")
    private MessageData payload;

    public MessageData getPayload() {
        return payload;
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("state", this.getState().toString());
        return data;
    }

    public class MessageData {
        @SerializedName("simplereminder_id")
        private String messageId;

        @SerializedName("title")
        private String title;

        @SerializedName("template")
        private String message;

        public String getMessageId() {
            return messageId;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }
    }
}
