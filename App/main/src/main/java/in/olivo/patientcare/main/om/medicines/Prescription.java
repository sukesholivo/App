package in.olivo.patientcare.main.om.medicines;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import in.olivo.patientcare.main.om.UserProfile;

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
    private Date modifiedOn;

    @SerializedName("notes")
    private String notes;

    @SerializedName("medicines")
    private ArrayList<PrescriptionMedicine> medicines;

    @SerializedName("doctor")
    private UserProfile doctor;

    public Prescription(String id, Date startDate, Date endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

    public Date getModifiedOn() {
        return modifiedOn;
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
