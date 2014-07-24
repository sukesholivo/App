package com.doctl.patientcare.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.vitals.Vitals;
import com.doctl.patientcare.main.vitals.VitalsAdapter;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class VitalDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_detail);
        populateVitalGraphData();
        populateVitalListData();
    }

    private void populateVitalGraphData() {
        int[] x = { 1,2,3,4,5,6,7};
        int[] vital1 = { 90,94,107,114,110,91,97};
        int[] vital2 = { 160,165,189,197,194,162,171};

        TimeSeries vitalSeries1 = new TimeSeries("Line1");
        TimeSeries vitalSeries2 = new TimeSeries("Line1");

        for(int i=0;i<x.length;i++){
            vitalSeries1.add(x[i], vital1[i]);
            vitalSeries2.add(x[i], vital2[i]);
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(vitalSeries1);
        dataset.addSeries(vitalSeries2);


        XYSeriesRenderer vital1Renderer = new XYSeriesRenderer();
        vital1Renderer.setColor(Color.RED);
        vital1Renderer.setLineWidth(3);
        vital1Renderer.setPointStyle(PointStyle.SQUARE);
        vital1Renderer.setFillPoints(true);

        XYSeriesRenderer vital2Renderer = new XYSeriesRenderer();
        vital2Renderer.setColor(Color.BLACK);
        vital2Renderer.setLineWidth(3);
        vital2Renderer.setPointStyle(PointStyle.SQUARE);
        vital2Renderer.setFillPoints(true);

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.parseColor("#00010101"));
        multiRenderer.setMarginsColor(Color.parseColor("#00010101"));
        multiRenderer.setMargins(new int[]{5, 2, -15, 0});
        multiRenderer.setShowLegend(false);
        multiRenderer.setShowAxes(false);
        multiRenderer.setXLabels(0);
        multiRenderer.setYLabels(0);
        multiRenderer.setZoomEnabled(false, false);
        multiRenderer.setPanEnabled(false, false);
        multiRenderer.addSeriesRenderer(vital1Renderer);
        multiRenderer.addSeriesRenderer(vital2Renderer);
        multiRenderer.setYAxisMin(50);
        multiRenderer.setYAxisMax(250);

        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.vitalDetailLineGraph);
        GraphicalView mChart = ChartFactory.getLineChartView(this, dataset, multiRenderer);
        chartContainer.addView(mChart);
    }

    private void populateVitalListData() {
        VitalsAdapter vitals = new VitalsAdapter(this, buildArrayHelper());
        ListView list = (ListView)findViewById(R.id.vitalEntryList);
        list.setAdapter(vitals);
    }

    private List<Vitals> buildArrayHelper() {
        Vitals v1 = new Vitals(90, "8:13 AM", 160, "2:33 PM", "TUE", "8 APR");
        Vitals v2 = new Vitals(94, "8:16 AM", 165, "2:09 PM", "WED", "9 APR");
        Vitals v3 = new Vitals(107, "8:36 AM", 189, "2:45 PM", "THU", "10 APR");
        Vitals v4 = new Vitals(114, "7:55 AM", 197, "2:56 PM", "FRI", "11 APR");
        Vitals v5 = new Vitals(110, "8:12 AM", 194, "2:29 PM", "SAT", "12 APR");
        Vitals v6 = new Vitals(91, "7:59 AM", 162, "2:16 PM", "SUN", "13 APR");
        Vitals v7 = new Vitals(97, "8:23 AM", 171, "2:21 PM", "MON", "14 APR");
        ArrayList<Vitals> list = new ArrayList<Vitals>();
        list.add(v1);
        list.add(v2);
        list.add(v3);
        list.add(v4);
        list.add(v5);
        list.add(v6);
        list.add(v7);
        return list;
    }
}
