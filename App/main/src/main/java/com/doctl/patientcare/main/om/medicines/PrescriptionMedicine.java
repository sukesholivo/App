package com.doctl.patientcare.main.om.medicines;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 8/5/2014.
 */
public class PrescriptionMedicine {
    @SerializedName("id")
    private String id;

    @SerializedName("startDate")
    private Date startDate;

    @SerializedName("endDate")
    private Date endDate;

    @SerializedName("brand")
    private String brand;

    @SerializedName("manufacture")
    private String manufacture;

    @SerializedName("type")
    private Medicine.MedicineType type;

    @SerializedName("dosageUnit")
    private String dosageUnit;

    @SerializedName("notes")
    private String notes;

    @SerializedName("daysInterval")
    private int daysInterval;

    @SerializedName("dosage")
    private ArrayList<Dosage> dosage;

    @SerializedName("sideEffect")
    private String sideEffect;

    public String getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getBrand() {
        return brand;
    }

    public String getManufacture() {
        return manufacture;
    }

    public Medicine.MedicineType getType() {
        return type;
    }

    public String getDosageUnit() {
        return dosageUnit;
    }

    public String getNotes() {
        return notes;
    }

    public int getDaysInterval() {
        return daysInterval;
    }

    public ArrayList<Dosage> getDosage() {
        return dosage;
    }

    public String getSideEffect() {
        return sideEffect;
    }

    public class Dosage{
        @SerializedName("when")
        private String when;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("constraint")
        private String constraint;

        public String getWhen() {
            return when;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getConstraint() {
            return constraint;
        }
    }
}
