package com.doctl.patientcare.main.om.vitals;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 6/22/2014.
 */
public class Vitals {
    @SerializedName("name1")
    private String name1;

    @SerializedName("condition1")
    private String condition1;

    @SerializedName("value1")
    private Double value1;

    @SerializedName("unit1")
    private String unit1;

    @SerializedName("valueMin1")
    private String valueMin1;

    @SerializedName("valueMax1")
    private String valueMax1;

    @SerializedName("name2")
    private String name2;

    @SerializedName("condition2")
    private String condition2;

    @SerializedName("value2")
    private Double value2;

    @SerializedName("unit2")
    private String unit2;

    @SerializedName("valueMin2")
    private String valueMin2;

    @SerializedName("valueMax2")
    private String valueMax2;

    @SerializedName("past")
    private GraphSeries past;

    private String timestamp;

    public String getName1() {
        return name1;
    }

    public String getCondition1() {
        return condition1;
    }

    public Double getValue1() {
        return value1;
    }

    public String getUnit1() {
        return unit1;
    }

    public String getValueMin1() {
        return valueMin1;
    }

    public String getValueMax1() {
        return valueMax1;
    }

    public String getName2() {
        return name2;
    }

    public String getCondition2() {
        return condition2;
    }

    public Double getValue2() {
        return value2;
    }

    public String getUnit2() {
        return unit2;
    }

    public String getValueMin2() {
        return valueMin2;
    }

    public String getValueMax2() {
        return valueMax2;
    }

    public void setValue1(Double value1) {
        this.value1 = value1;
    }

    public void setValue2(Double value2) {
        this.value2 = value2;
    }

    public GraphSeries getPast() {
        return past;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("timeStamp", this.getTimestamp());
        data.put("value1", this.getValue1());
        data.put("value2", this.getValue1());
        return data;
    }
}
