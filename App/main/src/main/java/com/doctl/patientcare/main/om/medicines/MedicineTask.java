package com.doctl.patientcare.main.om.medicines;

import com.doctl.patientcare.main.om.BaseTask;
import com.google.gson.annotations.SerializedName;

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
    }
}
