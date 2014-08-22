package com.doctl.patientcare.main.om.vitals;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Administrator on 6/22/2014.
 */
public class Vitals {
    @SerializedName("name")
    private String name;

    @SerializedName("condition")
    private String condition;

    @SerializedName("value")
    private double value;

    @SerializedName("unit")
    private String unit;

    @SerializedName("past")
    private GraphSeries past;

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public GraphSeries getPast() {
        return past;
    }

    public class GraphSeries {
        @SerializedName("timeStamps")
        private ArrayList<Double> timeStamps;

        @SerializedName("values")
        private ArrayList<Double> values;

        public ArrayList<Double> getTimeStamps() {
            return timeStamps;
        }

        public ArrayList<Double> getValues() {
            return values;
        }
    }

}
