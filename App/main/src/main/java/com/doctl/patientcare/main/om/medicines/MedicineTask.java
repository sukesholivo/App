package com.doctl.patientcare.main.om.medicines;

import com.doctl.patientcare.main.om.BaseTask;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 6/14/2014.
 */
public class MedicineTask extends BaseTask {
    @SerializedName("data")
    private MedicineData payload;

    public MedicineData getPayload() {
        return payload;
    }

    public class MedicineData{
        @SerializedName("prescriptionId")
        private String prescriptionId;

        @SerializedName("medicines")
        private ArrayList<Medicine> Medicines;

        public String getPrescriptionId() {
            return prescriptionId;
        }

        public ArrayList<Medicine> getMedicines() {
            return Medicines;
        }

        public JSONObject getDataToPatch() throws JSONException{
            JSONObject data = new JSONObject();
            data.put("prescriptionId", this.getPrescriptionId());
            JSONArray medArray = new JSONArray();
            for (Medicine medicine: this.getMedicines()){
                medArray.put(medicine.getDataToPatch());
            }
            data.put("medicines", medArray);
            return data;
        }
    }

    public JSONObject getDataToPatch() throws JSONException{
        JSONObject data = new JSONObject();
        data.put("data", this.getPayload().getDataToPatch());
        data.put("state", this.getState().toString());
        return data;
    }
}
