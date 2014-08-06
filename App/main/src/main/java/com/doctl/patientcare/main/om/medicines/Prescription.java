package com.doctl.patientcare.main.om.medicines;

import com.doctl.patientcare.main.om.UserProfile;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 8/5/2014.
 */
public class Prescription {
    @SerializedName("id")
    private String id;

    @SerializedName("startDate")
    private Date startDate;

    @SerializedName("endDate")
    private Date endDate;

    @SerializedName("createdOn")
    private Date createdOn;

    @SerializedName("modifiedOn")
    private Date modiefiedOn;

    @SerializedName("notes")
    private String notes;

    @SerializedName("medicines")
    private ArrayList<PrescriptionMedicine> medicines;

    @SerializedName("doctor")
    private UserProfile doctor;

    public String getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getModiefiedOn() {
        return modiefiedOn;
    }

    public String getNotes() {
        return notes;
    }

    public ArrayList<PrescriptionMedicine> getMedicines() {
        return medicines;
    }

    public UserProfile getDoctor() {
        return doctor;
    }
}
