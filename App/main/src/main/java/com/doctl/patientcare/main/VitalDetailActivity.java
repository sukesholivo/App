package com.doctl.patientcare.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.doctl.patientcare.main.om.vitals.VitalsDetailAdapter;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;


import java.util.Arrays;

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
        new GetVitals().execute(vitalId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vital_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String downloadVitalsData(String vitalId) {
        return Utils.parsonJsonFromFile(this, R.raw.vitals);
    }

    private String downloadVitalsData1() {
        return Utils.parsonJsonFromFile(this, R.raw.vitals1);
    }

    private VitalTask.VitalData parseVitalsData(String jsonStr){
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
        return new Gson().fromJson(jsonObject, VitalTask.VitalData.class);
    }

    private VitalDetailData[] parseVitalsData1(String jsonStr){
//        JsonParser parser = new JsonParser();
//        JsonArray jsonArray = parser.parse(jsonStr).getAsJsonArray();
        return new Gson().fromJson(jsonStr, VitalDetailData[].class);
    }

    private void refreshActivity(String vitalId) {
        String jsonStr = downloadVitalsData(vitalId);
        final VitalTask.VitalData vitalData =  parseVitalsData(jsonStr);

        String jsonStr1 = downloadVitalsData1();
        final VitalDetailData[] vitalData1 = parseVitalsData1(jsonStr1);
        runOnUiThread(new Runnable() {
            public void run() {
                populateVitalGraphData(vitalData);
                populateVitalListData(vitalData1);
            }
        });
    }

    private class GetVitals extends AsyncTask<String, Void, Void> {

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
//        TODO: FIX this when new UI comes using new OM definition
//        ArrayList<Vitals> vitals = vitalData.getVitals();
//        ArrayList<GraphData> graphList = new ArrayList<GraphData>();
//        for (Vitals vital : vitals) {
//            GraphData graph = new GraphData(vital.getName(),
//                    vital.getPast().getTimeStamps(),
//                    vital.getPast().getValues(),
//                    Color.RED,3);
//            graphList.add(graph);
//        }
//        GraphicalView graphicalView = Utils.getGraph(this, graphList);
//        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.vitalDetailLineGraph);
//        chartContainer.addView(graphicalView);
    }

    private void populateVitalListData(VitalDetailData[] vitalData) {
        VitalsDetailAdapter vitals = new VitalsDetailAdapter(this, Arrays.asList(vitalData));
        ListView list = (ListView)findViewById(R.id.vitalEntryList);
        list.setAdapter(vitals);
    }

    public class VitalDetailData{
        @SerializedName("date")
        private String date;

        @SerializedName("time1")
        private String time1;

        @SerializedName("value1")
        private int value1;

        @SerializedName("time2")
        private String time2;

        @SerializedName("value2")
        private int value2;

        public String getDate() {
            return date;
        }

        public String getTime1() {
            return time1;
        }

        public int getValue1() {
            return value1;
        }

        public String getTime2() {
            return time2;
        }

        public int getValue2() {
            return value2;
        }
    }
}
