package com.doctl.patientcare.main.om.vitals;

import com.doctl.patientcare.main.om.BaseTask;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 8/8/2014.
 */
public class VitalTask extends BaseTask {
    @SerializedName("data")
    private VitalData payload;

    public VitalData getPayload() {
        return payload;
    }

    public class VitalData{
        @SerializedName("vitalPlanId")
        private String planId;

        @SerializedName("vitalId")
        private String vitalId;

        @SerializedName("title")
        private String title;

        @SerializedName("vitals")
        private Vitals vitals;

        public String getPlanId() {
            return planId;
        }

        public String getVitalId() {
            return vitalId;
        }

        public String getTitle() {
            return title;
        }

        public Vitals getVitals() {
            return vitals;
        }

        public JSONObject getDataToPatch() throws JSONException{
            JSONObject data = new JSONObject();
            data.put("vitalPlanId", this.getPlanId());
            data.put("vitalId", this.getVitalId());
            data.put("vitals", this.getVitals().getDataToPatch());
            return data;
        }
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("data", this.getPayload().getDataToPatch());
        data.put("state", this.getState().toString());
        return data;
    }
}
