package in.olivo.patientcare.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.util.Date;

import in.olivo.patientcare.main.controls.ProgressBarAnimation;
import in.olivo.patientcare.main.om.UserProfile;
import in.olivo.patientcare.main.om.medicines.MedicineDetailAdapter;
import in.olivo.patientcare.main.om.medicines.Prescription;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Utils;

public class MedicineDetailActivity extends BaseActivityWithNavigation {
    public final static String TAG = MedicineDetailActivity.class.getSimpleName();
    static boolean active = false;
    private String prescriptionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);
        this.setupNavigationDrawer();
    }


    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.nav_item_medicine);
        }
        prescriptionId = "";
        active = true;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            prescriptionId = bundle.getString("prescriptionId");
        }
        refresh();
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private void refresh() {
        if (Utils.isNetworkAvailable(this)) {
            new GetPrescription().execute(prescriptionId);
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    private String downloadPrescriptionData(String prescriptionId) {
        if (prescriptionId == null) {
            prescriptionId = "";
        }
        String url = Constants.PRESCRIPTION_URL + prescriptionId;
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private Prescription parsePrescriptionData(String jsonStr) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();

        return new Gson().fromJson(jsonObject, Prescription.class);
    }

    private void updateUI(final Prescription prescription) {
        RelativeLayout doctorLayout = (RelativeLayout) findViewById(R.id.doctorLayout);
        if (prescription.getDoctor() != null) {
            doctorLayout.setVisibility(View.VISIBLE);
            TextView doctorName = (TextView) findViewById(R.id.doctorName);
            doctorName.setText(prescription.getDoctor().getDisplayName());

            TextView doctorAddress = (TextView) findViewById(R.id.doctorHospital);
            UserProfile.Address address = prescription.getDoctor().getAddress();
            if (address != null) {
                doctorAddress.setText(address.getPrintableAddress());
            }

            Picasso.with(this)
                    .load(Utils.getFullURL(Constants.SERVER_URL, prescription.getDoctor().getProfilePicUrl()))
                            .into((ImageView) findViewById(R.id.doctorPic));
        } else {
            doctorLayout.setVisibility(View.GONE);
        }
        RelativeLayout progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.VISIBLE);

        TextView prescriptionDate = (TextView) findViewById(R.id.dateText);
        prescriptionDate.setText(Utils.getDateString(prescription.getStartDate()));

        long milliSecondsInDay = 24 * 60 * 60 * 1000;
        long treatmentDays = (prescription.getEndDate().getTime() - prescription.getStartDate().getTime()) / milliSecondsInDay;
        long pendingDays = (prescription.getEndDate().getTime() - new Date().getTime()) / milliSecondsInDay;
        TextView prescriptionPlanText = (TextView) findViewById(R.id.planText);
        String message = pendingDays > 0 ?
                String.format(getString(R.string.treatment_plan_remaining), treatmentDays, pendingDays) :
                String.format(getString(R.string.treatment_plan_completed), treatmentDays);
        prescriptionPlanText.setText(message);

        pendingDays = pendingDays < 0 ? 0 : pendingDays;
        ProgressBar treatmentProgress = (ProgressBar) findViewById(R.id.treatmentProgress);
        ProgressBarAnimation anim = new ProgressBarAnimation(treatmentProgress, 0, (treatmentDays - pendingDays) * 100 / treatmentDays);
        anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        treatmentProgress.startAnimation(anim);

        MedicineDetailAdapter medicines = new MedicineDetailAdapter(this, prescription.getMedicines());
        ListView list = (ListView) this.findViewById(R.id.medicineDetailList);
        list.setAdapter(medicines);
    }

    private void refreshActivity(String prescriptionId) {
        String jsonStr = downloadPrescriptionData(prescriptionId);
        if (jsonStr != null && !jsonStr.isEmpty()) {
            final Prescription prescription = parsePrescriptionData(jsonStr);

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUI(prescription);
                }
            });
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetPrescription extends AsyncTask<String, Void, Void> {

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
}
