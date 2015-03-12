package com.doctl.patientcare.main.om;

import com.doctl.patientcare.main.om.medicines.Prescription;
import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 3/12/2015.
 */
public class TreatmentInfo {
    @SerializedName("personal")
    private UserProfile personal;

    @SerializedName("clinic")
    private UserProfile clinic;

    @SerializedName("prescription")
    private Prescription prescription;

    @SerializedName("vitals")
    private List<VitalTask.VitalData> vitals;

    public UserProfile getPersonal() {
        return personal;
    }

    public UserProfile getClinic() {
        return clinic;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public List<VitalTask.VitalData> getVitals() {
        return vitals;
    }
}
