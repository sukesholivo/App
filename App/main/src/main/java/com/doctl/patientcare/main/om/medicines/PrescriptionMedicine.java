package com.doctl.patientcare.main.om.medicines;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Administrator on 8/5/2014.
 */
public class PrescriptionMedicine {
    @SerializedName("me_id")
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
    private int type;

    @SerializedName("days")
    private int days;

    @SerializedName("beforeMeal")
    private boolean beforeMeal;

    @SerializedName("dosageUnit")
    private String dosageUnit;

    @SerializedName("notes")
    private String notes;

    @SerializedName("frequency")
    private String daysInterval;

    @SerializedName("dosage")
    private Dosage dosage;

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

    public int getType() {
        return type;
    }

    public String getDosageUnit() {
        return dosageUnit;
    }

    public String getNotes() {
        return notes;
    }

    public String getDaysInterval() {
        return daysInterval;
    }

    public Dosage getDosage() {
        return dosage;
    }

    public boolean isBeforeMeal() {
        return beforeMeal;
    }

    public int getDays() {
        return days;
    }

    public String getSideEffect() {
        return sideEffect;
    }

    public class Dosage{
        @SerializedName("night")
        private String night;

        @SerializedName("noon")
        private String noon;

        @SerializedName("evening")
        private String evening;

        @SerializedName("morning")
        private String morning;

        @SerializedName("constraint")
        private String constraint;

        public String getNight() {
            return night;
        }

        public String getNoon() {
            return noon;
        }

        public String getEvening() {
            return evening;
        }

        public String getMorning() {
            return morning;
        }

        public String getConstraint() {
            return constraint;
        }
    }
}
