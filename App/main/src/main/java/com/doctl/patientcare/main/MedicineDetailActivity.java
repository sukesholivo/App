package com.doctl.patientcare.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doctl.patientcare.main.controls.ProgressBarAnimation;
import com.doctl.patientcare.main.utility.Utils;
import com.doctl.patientcare.main.om.medicines.Medicine;
import com.doctl.patientcare.main.om.medicines.MedicineDetailAdapter;
import com.doctl.patientcare.main.om.medicines.Prescription;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicineDetailActivity extends Activity {
    public final static String TAG = MedicineDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);
        Bundle bundle = getIntent().getExtras();
        String prescriptionId = bundle.getString("prescriptionId");
        new GetPrescription().execute(prescriptionId);
    }

    private String downloadPrescriptionData(String prescriptionId) {
        return Utils.parsonJsonFromFile(this, R.raw.prescription);
    }

    private Prescription parsePrescriptionData(String jsonStr){
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
        Prescription prescription = new Gson().fromJson(jsonObject, Prescription.class);
        return prescription;
    }

    private void updateUI(final Prescription prescription){
        TextView doctorName = (TextView) findViewById(R.id.doctorName);
        doctorName.setText(prescription.getDoctor().getDisplayName());

        TextView doctorAddress = (TextView) findViewById(R.id.doctorHospital);
        doctorAddress.setText(prescription.getDoctor().getAddress());

        Picasso.with(this)
                .load(prescription.getDoctor().getProfilePicUrl())
                .into((ImageView) findViewById(R.id.doctorPic));

        TextView prescriptionDate = (TextView) findViewById(R.id.dateText);
        prescriptionDate.setText(Utils.getDateString(prescription.getStartDate()));

        long milliSecondsInDay = 24 * 60 * 60 * 1000;
        long treatmentDays = (prescription.getEndDate().getTime() - prescription.getStartDate().getTime())/ milliSecondsInDay;
        long pendingDays = (prescription.getEndDate().getTime() - new Date().getTime())/ milliSecondsInDay;
        TextView prescriptionPlanText = (TextView) findViewById(R.id.planText);
        String message = pendingDays>0?
                String.format(getString(R.string.treatment_plan_remaining), treatmentDays, pendingDays):
                String.format(getString(R.string.treatment_plan_completed), treatmentDays);
        prescriptionPlanText.setText(message);

        pendingDays = pendingDays < 0 ? 0 : pendingDays;
        ProgressBar treatmentProgress = (ProgressBar) findViewById(R.id.treatmentProgress);
        ProgressBarAnimation anim = new ProgressBarAnimation(treatmentProgress, 0, (treatmentDays - pendingDays) * 100 /treatmentDays);
        anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        treatmentProgress.startAnimation(anim);

        MedicineDetailAdapter medicines = new MedicineDetailAdapter(this, prescription.getMedicines());
        ListView list = (ListView)this.findViewById(R.id.medicineDetailList);
        list.setAdapter(medicines);
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetPrescription extends AsyncTask<String, Void, Void> {

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

    private void refreshActivity(String prescriptionId) {
        String jsonStr = downloadPrescriptionData(prescriptionId);
        final Prescription prescription =  parsePrescriptionData(jsonStr);

        runOnUiThread(new Runnable() {
            public void run() {
                updateUI(prescription);
            }
        });
    }

    private List<Medicine> buildArrayHelper() {
        Medicine m1 = new Medicine("","Metformin", 1, Medicine.MedicineType.CAPSULE, "", "500mg",
                "These are for lowering sugar level levels. These need to be taken 15 after taking food", false,
                new int[]{1,0,1}, new String[]{"After Breakfast", "", "After Dinner"});
        Medicine m2 = new Medicine("", "Sulfonylureas", 2, Medicine.MedicineType.TABLET, "", "200mg",
                "These are for lowering sugar level levels. These need to be taken 15 after taking food", false,
                new int[]{1,0,1}, new String[]{"Before Meal", "Before Meal", "After Meal"});
        Medicine m3 = new Medicine("", "Humalog", 1, Medicine.MedicineType.INJECTION, "", "20ml",
                "These are for lowering sugar level levels. These need to be taken 15 after taking food", false,
                new int[]{1,0,1}, new String[]{"After Breakfast", "", "After Dinner"});
        ArrayList<Medicine> list = new ArrayList<Medicine>();
        list.add(m1);
        list.add(m2);
        list.add(m3);
        return list;
    }
}
