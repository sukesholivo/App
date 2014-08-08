package com.doctl.patientcare.main.om.vitals;

import com.doctl.patientcare.main.om.BaseTask;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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
        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("vitals")
        private ArrayList<Vitals> vitals;

        @SerializedName("pastVitals")
        private ArrayList<Vitals> pastVitals;

        public String getTitle() {
            return title;
        }

        public ArrayList<Vitals> getVitals() {
            return vitals;
        }

        public ArrayList<Vitals> getPastVitals() {
            return pastVitals;
        }
    }
}
