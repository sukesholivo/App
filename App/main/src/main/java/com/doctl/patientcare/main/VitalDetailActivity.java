package com.doctl.patientcare.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.doctl.patientcare.main.om.vitals.VitalsDetailAdapter;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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
        VitalsDetailAdapter vitals = new VitalsDetailAdapter(this, null);
        ListView list = (ListView)findViewById(R.id.vitalEntryList);
        list.setAdapter(vitals);
    }
}
