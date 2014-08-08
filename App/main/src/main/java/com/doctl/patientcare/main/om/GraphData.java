package com.doctl.patientcare.main.om;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Administrator on 8/8/2014.
 */
public class GraphData {
    private String title;
    private ArrayList<Double> X;
    private ArrayList<Double> Y;
    private int lineColor;
    private int lineWidth;

    public GraphData(String title, ArrayList<Double> X, ArrayList<Double> Y, int lineColor, int lineWidth){
        this.title = title;
        this.X = X;
        this.Y = Y;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Double> getX() {
        return X;
    }

    public ArrayList<Double> getY() {
        return Y;
    }

    public int getLineColor() {
        return lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }
}
