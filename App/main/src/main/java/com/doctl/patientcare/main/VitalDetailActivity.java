package com.doctl.patientcare.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.doctl.patientcare.main.om.GraphData;
import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.doctl.patientcare.main.om.vitals.Vitals;
import com.doctl.patientcare.main.om.vitals.VitalsDetailAdapter;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.achartengine.GraphicalView;

import java.util.ArrayList;

public class VitalDetailActivity extends Activity {
    private static final String TAG = VitalDetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_detail);

        Bundle bundle = getIntent().getExtras();
        String vitalId = bundle.getString("vitalId");
        new GetVitals().execute(vitalId);
    }

    private String downloadVitalsData(String vitalId) {
        return Utils.parsonJsonFromFile(this, R.raw.vitals);
    }

    private VitalTask.VitalData parseVitalsData(String jsonStr){
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
        return new Gson().fromJson(jsonObject, VitalTask.VitalData.class);
    }

    private void refreshActivity(String vitalId) {
        String jsonStr = downloadVitalsData(vitalId);
        final VitalTask.VitalData vitalData =  parseVitalsData(jsonStr);
        runOnUiThread(new Runnable() {
            public void run() {
                populateVitalGraphData(vitalData);
//                populateVitalListData()
            }
        });
    }

    private class GetVitals extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            refreshActivity(arg0[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void populateVitalGraphData(VitalTask.VitalData vitalData) {
        ArrayList<Vitals> vitals = vitalData.getVitals();
        ArrayList<GraphData> graphList = new ArrayList<GraphData>();
        for (Vitals vital : vitals) {
            GraphData graph = new GraphData(vital.getName(),
                    vital.getPast().getTimeStamps(),
                    vital.getPast().getValues(),
                    Color.RED,3);
            graphList.add(graph);
        }
        GraphicalView graphicalView = Utils.getGraph(this, graphList);
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.vitalDetailLineGraph);
        chartContainer.addView(graphicalView);
    }

    private void populateVitalListData() {
        VitalsDetailAdapter vitals = new VitalsDetailAdapter(this, null);
        ListView list = (ListView)findViewById(R.id.vitalEntryList);
        list.setAdapter(vitals);
    }
}
