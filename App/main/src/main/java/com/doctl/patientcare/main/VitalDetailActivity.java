package com.doctl.patientcare.main;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.doctl.patientcare.main.om.GraphData;
import com.doctl.patientcare.main.om.vitals.VitalDetailData;
import com.doctl.patientcare.main.om.vitals.VitalsDetailAdapter;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.achartengine.GraphicalView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class VitalDetailActivity extends BaseActivity {
    private static final String TAG = VitalDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null){
            return;
        }
        String vitalId = bundle.getString("vitalId");
        String vitalType = bundle.getString("vitalType");
        String vitalName = bundle.getString("vitalName");
        if (vitalName != null) {
            getActionBar().setTitle(vitalName);
        }
        new GetVitals().execute(vitalId, vitalType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vital_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String downloadVitalsData(String vitalId, String vitalType) {
        String url = Constants.VITAL_DETAIL_URL;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (vitalId != null) {
            params.add(new BasicNameValuePair("vitalId", vitalId));
        } else if (vitalType != null) {
            params.add(new BasicNameValuePair("vitalType", vitalType));
        }
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, params, null);
        Log.d(TAG, response);
        return response;
    }

    private VitalDetailData parseVitalsData(String jsonStr){
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
        return new Gson().fromJson(jsonObject, VitalDetailData.class);
    }

    private void refreshActivity(String vitalId, String vitalType) {
        String jsonStr = downloadVitalsData(vitalId, vitalType);
        final VitalDetailData vitalData =  parseVitalsData(jsonStr);


        runOnUiThread(new Runnable() {
            public void run() {
                populateVitalGraphData(vitalData);
                populateVitalListData(vitalData);
            }
        });
    }

    private class GetVitals extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... arg0) {
            refreshActivity(arg0[0], arg0[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void populateVitalGraphData(VitalDetailData vitalData) {
        ArrayList<VitalDetailData.VitalDetailValue> vitals = vitalData.getData();
        ArrayList<GraphData> graphList = new ArrayList<GraphData>();
        ArrayList<Double> X1 = new ArrayList<Double>();
        ArrayList<Double> Y1 = new ArrayList<Double>();
        ArrayList<Double> X2 = new ArrayList<Double>();
        ArrayList<Double> Y2 = new ArrayList<Double>();
        for (VitalDetailData.VitalDetailValue vital : vitals) {
            X1.add(Double.valueOf(vital.getTime().getTime()));
            Y1.add(vital.getValue1());
            if (vital.getValue2() != null) {
                X2.add(Double.valueOf(vital.getTime().getTime()));
                Y2.add(vital.getValue2());
            }
        }

        GraphData graph1 = new GraphData(vitalData.getName1(), X1, Y1, Color.WHITE, 5);
        graphList.add(graph1);
        GraphData graph2 = new GraphData(vitalData.getName1(), X2, Y2, Color.WHITE, 5);
        graphList.add(graph2);
        GraphicalView graphicalView = Utils.getGraph(this, graphList);
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.vitalDetailLineGraph);
        chartContainer.addView(graphicalView);
    }

    private void populateVitalListData(VitalDetailData vitalData) {
        VitalsDetailAdapter vitals = new VitalsDetailAdapter(this, vitalData.getData());
        ListView list = (ListView)findViewById(R.id.vitalEntryList);
        list.setAdapter(vitals);
    }
}
