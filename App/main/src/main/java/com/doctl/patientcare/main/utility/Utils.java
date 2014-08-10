package com.doctl.patientcare.main.utility;

import android.content.Context;
import android.graphics.Color;

import com.doctl.patientcare.main.Cards.CardHeaderInnerView;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.GraphData;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 7/28/2014.
 */
public final class Utils {
    public static Date parseFromString(String iso8601string) {
        try {
            String s = iso8601string.replace("Z", "+00:00");
            try {
                s = s.substring(0, 22) + s.substring(23);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Invalid length", 0);
            }
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateString(Date date) {
        return new SimpleDateFormat("dd MMM yyyy").format(date);
    }

    public static CardHeader getCardHeader(Context context, BaseTask task){
        CardHeader cardHeader;
        boolean pastTask = false;
        int seconds  = (int)( (task.getETA().getTime() - new Date().getTime())/1000);
        if (seconds < 0) { // future tasks
            seconds = 0 - seconds;
            pastTask = true;
        }
        if (task.getEtaType() == BaseTask.TypeOfEta.DAYLONG) {
            cardHeader = new CardHeaderInnerView(context, "" + "TODAY", "", "");
        } else {
            if (seconds < 60) { // if time is less than 1 min
                cardHeader = new CardHeaderInnerView(context, "NOW", "", "");
            }else if (seconds < 60 * 60) { // if time is less than 1 hour
                cardHeader = new CardHeaderInnerView(context, "" + seconds / 60, "MINS " + (pastTask ? "AGO" : "FROM NOW"), "");
            } else if (seconds < 60 * 60 * 24) { // if time is less than 1 day
                cardHeader = new CardHeaderInnerView(context, "" + seconds / (60 * 60), "HRS " + (pastTask ? "AGO" : "FROM NOW"), "");
            } else { // if time is more than 1 day
                cardHeader = new CardHeaderInnerView(context, "" + seconds / (60 * 60 * 24), "DAYS " + (pastTask ? "AGO" : ""), "");
            }
        }
        return cardHeader;
    }

    public static String parsonJsonFromFile(Context context,int resourceId){
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    public static GraphicalView getGraph(Context context, ArrayList<GraphData>graph){
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.parseColor("#00010101"));
        multiRenderer.setMarginsColor(Color.parseColor("#00010101"));
        multiRenderer.setMargins(new int[]{5,2,-15,0});
        multiRenderer.setShowLegend(false);
        multiRenderer.setShowAxes(false);
        multiRenderer.setXLabels(0);
        multiRenderer.setYLabels(0);
        multiRenderer.setZoomEnabled(false, false);
        multiRenderer.setPanEnabled(false, false);

        for (GraphData g : graph) {
            XYSeries series = new XYSeries(g.getTitle());

            for (int j = 0; j < g.getX().size(); j++) {
                series.add(g.getX().get(j), g.getY().get(j));
            }
            dataset.addSeries(series);
            XYSeriesRenderer graphRenderer = new XYSeriesRenderer();
            graphRenderer.setColor(g.getLineColor());
            graphRenderer.setLineWidth(g.getLineWidth());
            multiRenderer.addSeriesRenderer(graphRenderer);
        }
        return ChartFactory.getLineChartView(context, dataset, multiRenderer);
    }
}
