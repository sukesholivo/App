package com.doctl.patientcare.main.om.vitals;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 12/17/2014.
 */
public class VitalDetailData {

    @SerializedName("vitalId")
    private String vitalId;

    @SerializedName("type")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("notes")
    private String notes;

    @SerializedName("name1")
    private String name1;

    @SerializedName("valueMin1")
    private Double valueMin1;

    @SerializedName("valueMax1")
    private Double valueMax1;

    @SerializedName("unit1")
    private String unit1;

    @SerializedName("name2")
    private String name2;

    @SerializedName("valueMin2")
    private Double valueMin2;

    @SerializedName("valueMax2")
    private Double valueMax2;

    @SerializedName("unit2")
    private String unit2;

    @SerializedName("data")
    private ArrayList<VitalDetailValue> data;

    public String getVitalId() {
        return vitalId;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public String getName1() {
        return name1;
    }

    public Double getValueMin1() {
        return valueMin1;
    }

    public Double getValueMax1() {
        return valueMax1;
    }

    public String getUnit1() {
        return unit1;
    }

    public String getName2() {
        return name2;
    }

    public Double getValueMin2() {
        return valueMin2;
    }

    public Double getValueMax2() {
        return valueMax2;
    }

    public String getUnit2() {
        return unit2;
    }

    public ArrayList<VitalDetailValue> getData() {
        return data;
    }

    public class VitalDetailValue {
        @SerializedName("time")
        private Date time;

        @SerializedName("value1")
        private Double value1;

        @SerializedName("value2")
        private Double value2;

        public Date getTime() {
            return time;
        }

        public Double getValue1() {
            return value1;
        }

        public Double getValue2() {
            return value2;
        }
    }
}
