package in.olivo.patientcare.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.achartengine.GraphicalView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.olivo.patientcare.main.om.GraphData;
import in.olivo.patientcare.main.om.vitals.VitalDetailData;
import in.olivo.patientcare.main.om.vitals.VitalsDetailAdapter;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;

public class VitalDetailActivity extends BaseActivityWithNavigation {
    private static final String TAG = VitalDetailActivity.class.getSimpleName();
    String vitalId;
    String vitalType;
    FloatingActionButton fab;
    private VitalDetailData vitalData;
    private boolean addVital;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        this.setupNavigationDrawer();
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        vitalId = bundle.getString("vitalId");
        vitalType = bundle.getString("vitalType");
        addVital = bundle.getBoolean(Constants.ADD_VITAL, false);
        fab = (FloatingActionButton) this.findViewById(R.id.button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddVitalDialog();
            }
        });
        refresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vital_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddVitalDialog() {
        if (vitalData != null) {
            new AlertDialog.Builder(this)
                    .setTitle(this.vitalData.getTitle())
                    .setIcon(0)
                    .setView(getNewVitalDialogInnerView())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            saveVitalValue(dialog);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    private void refresh() {
        if (Utils.isNetworkAvailable(this)) {
            new GetVitals().execute(vitalId, vitalType);
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    private View getNewVitalDialogInnerView() {
        View view = getLayoutInflater().inflate(R.layout.dialog_inner_content_vital_entry, null);
        LinearLayout list = (LinearLayout) view.findViewById(R.id.vitalList);
        list.addView(getVitalEntryLayoutView(list, vitalData.getName1(), vitalData.getUnit1()));
        if (vitalData.getName2() != null) {
            list.addView(getVitalEntryLayoutView(list, vitalData.getName2(), vitalData.getUnit2()));
        }
        return view;
    }

    private View getVitalEntryLayoutView(ViewGroup parent, String name, String unit) {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.vital_card_list_item, parent, false);
        TextView vitalTitle = (TextView) view.findViewById(R.id.vitalTitle);
        vitalTitle.setText(name);
        TextView vitalUnit = (TextView) view.findViewById(R.id.vitalUnit);
        vitalUnit.setText(unit);
        return view;
    }

    private void saveVitalValue(DialogInterface dialog) {
        LinearLayout ll = (LinearLayout) ((AlertDialog) dialog).findViewById(R.id.vitalList);
        int childCount = ll.getChildCount();
        Double value1 = null, value2 = null;
        for (int i = 0; i < childCount; i++) {
            View v = ll.getChildAt(i);
            EditText editText = (EditText) v.findViewById(R.id.vitalValue);
            if (i == 0) {
                String value1Str = editText.getText().toString();
                if (!value1Str.isEmpty()) {
                    value1 = Double.valueOf(value1Str);
                }
            }
            if (i == 1) {
                String value2Str = editText.getText().toString();
                if (!value2Str.isEmpty()) {
                    value2 = Double.valueOf(value2Str);
                }
            }
        }

        JSONObject data = new JSONObject();
        try {
            if (value1 != null) {
                data.put("value1", value1);
            }
            if (value2 != null) {
                data.put("value2", value2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.e(TAG, e.getMessage());
        }
        Logger.e(TAG, data.toString());
        String url = Constants.VITAL_DETAIL_URL + vitalData.getVitalId() + "/";
        new SaveVital().execute(url, data);
        Toast.makeText(VitalDetailActivity.this, "Value submitted: " + value1, Toast.LENGTH_LONG).show();
    }

    private void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private String downloadVitalsData(String vitalId, String vitalType) {
        String url = Constants.VITAL_DETAIL_URL;
        List<NameValuePair> params = new ArrayList<>();
        if (vitalId != null) {
            params.add(new BasicNameValuePair("vitalId", vitalId));
        } else if (vitalType != null) {
            params.add(new BasicNameValuePair("vitalType", vitalType));
        }
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, params, null);
    }

    private VitalDetailData parseVitalsData(String jsonStr) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
        return new Gson().fromJson(jsonObject, VitalDetailData.class);
    }

    private void refreshActivity(String vitalId, String vitalType) {
        String jsonStr = downloadVitalsData(vitalId, vitalType);
        if (jsonStr != null && !jsonStr.isEmpty()) {
            vitalData = parseVitalsData(jsonStr);

            runOnUiThread(new Runnable() {
                public void run() {
                    setTitle(vitalData.getTitle());
                    populateVitalGraphData(vitalData);
                    populateVitalListData(vitalData);
                    if (addVital) {
                        addVital = false;
                        showAddVitalDialog();
                    }
                }
            });
        }
    }

    private void populateVitalGraphData(VitalDetailData vitalData) {
        ArrayList<VitalDetailData.VitalDetailValue> vitals = vitalData.getData();
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.vitalDetailLineGraph);
        if (vitals.isEmpty()) {
            chartContainer.setVisibility(View.GONE);
            return;
        }
        chartContainer.setVisibility(View.VISIBLE);
        ArrayList<GraphData> graphList = new ArrayList<>();
        ArrayList<Double> X1 = new ArrayList<>();
        ArrayList<Double> Y1 = new ArrayList<>();
        ArrayList<Double> X2 = new ArrayList<>();
        ArrayList<Double> Y2 = new ArrayList<>();
        for (VitalDetailData.VitalDetailValue vital : vitals) {
            X1.add((double) vital.getTime().getTime());
            Y1.add(vital.getValue1());
            if (vital.getValue2() != null) {
                X2.add((double) vital.getTime().getTime());
                Y2.add(vital.getValue2());
            }
        }

        GraphData graph1 = new GraphData(vitalData.getName1(), X1, Y1, Color.WHITE, 5);
        graphList.add(graph1);
        GraphData graph2 = new GraphData(vitalData.getName1(), X2, Y2, Color.WHITE, 5);
        graphList.add(graph2);
        GraphicalView graphicalView = Utils.getGraph(this, graphList);

        chartContainer.addView(graphicalView);
    }

    private void populateVitalListData(VitalDetailData vitalData) {
        TextView textView = (TextView) findViewById(R.id.no_vital_data);
        if (vitalData.getData().isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            return;
        }
        textView.setVisibility(View.GONE);
        VitalsDetailAdapter vitals = new VitalsDetailAdapter(this, vitalData.getData());
        ListView list = (ListView) findViewById(R.id.vitalEntryList);
        list.setAdapter(vitals);
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

    private class SaveVital extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... arg0) {
            String url = (String) arg0[0];
            JSONObject data = (JSONObject) arg0[1];
            HTTPServiceHandler serviceHandler = new HTTPServiceHandler(VitalDetailActivity.this);
            String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data);
            if (response != null && !response.isEmpty()) {
                refresh();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}
