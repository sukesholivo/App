package com.doctl.patientcare.main.om.dashboard;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Administrator on 11/28/2014.
 */
public class Dashboard {
    @SerializedName("adherence")
    private Double adherence;

    @SerializedName("points")
    private int points;

    @SerializedName("progress")
    private Progress progress;

    @SerializedName("vital")
    private Vital vital;

    public Double getAdherence() {
        return adherence;
    }

    public int getPoints() {
        return points;
    }

    public Progress getProgress() {
        return progress;
    }

    public Vital getVital() {
        return vital;
    }

    public class Progress {

        @SerializedName("startTime")
        private Date startTime;

        @SerializedName("endTime")
        private Date endTime;

        public Date getStartTime() {
            return startTime;
        }

        public Date getEndTime() {
            return endTime;
        }
    }

    public class Vital {
        @SerializedName("type")
        private String type;

        @SerializedName("timestamp")
        private Date timeStamp;

        @SerializedName("value1")
        private Double value1;

        @SerializedName("value2")
        private Double value2;

        @SerializedName("unit1")
        private String unit1;

        @SerializedName("unit2")
        private String unit2;

        public String getType() {
            return type;
        }

        public Date getTimeStamp() {
            return timeStamp;
        }

        public Double getValue1() {
            return value1;
        }

        public Double getValue2() {
            return value2;
        }

        public String getUnit1() {
            return unit1;
        }

        public String getUnit2() {
            return unit2;
        }
    }
}
