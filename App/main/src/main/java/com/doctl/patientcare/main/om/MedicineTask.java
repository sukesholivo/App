package com.doctl.patientcare.main.om;

import android.util.Log;

import com.doctl.patientcare.main.medicines.Medicine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 6/14/2014.
 */
public class MedicineTask extends BaseTasks {
    private static final String TAG = MedicineTask.class.getSimpleName();

    private static final String TAG_MEDICINE_OBJECT = "medicines";
    private static final String TAG_MEDICINE_NAME = "name";
    private static final String TAG_MEDICINE_QUANTITY = "quantity";
    private static final String TAG_MEDICINE_TYPE = "type";
    private static final String TAG_MEDICINE_UNIT = "unit";
    private static final String TAG_MEDICINE_INSTRUCTION = "instruction";
    private static final String TAG_MEDICINE_NOTES = "notes";
    private ArrayList<Medicine> Medicines;

    public ArrayList<Medicine> getMedicines() {
        return Medicines;
    }

    public static MedicineTask parseJson(JSONObject jsonObj) {
        MedicineTask medicineTask = new MedicineTask();
        if (jsonObj != null) {
            try {
                medicineTask.CardId = jsonObj.getString(TAG_TASK_ID);
//                medicineTask.ETA = new Date(jsonObj.getString(TAG_TASK_ETA));
//                medicineTask.CreatedOn = new Date(jsonObj.getString(TAG_TASK_CREATED));
//                medicineTask.ModifiedOn = new Date(jsonObj.getString(TAG_TASK_MODIFIED));
                medicineTask.ETA = new Date();
                medicineTask.CreatedOn = new Date();
                medicineTask.ModifiedOn = new Date();
                medicineTask.State = CardState.valueOf(jsonObj.getString(TAG_TASK_STATE));
                medicineTask.Type = CardType.valueOf(jsonObj.getString(TAG_TASK_TYPE));
                medicineTask.Points = jsonObj.getInt(TAG_TASK_POINTS);
                // Getting JSON Array node
                JSONArray medicines = jsonObj.getJSONArray(TAG_MEDICINE_OBJECT);
                medicineTask.Medicines = new ArrayList<Medicine>();
                // looping through All Medicines
                for (int i = 0; i < medicines.length(); i++) {
                    JSONObject m = medicines.getJSONObject(i);
                    Medicine med = new Medicine(
                            m.getString(TAG_MEDICINE_NAME),
                            m.getInt(TAG_MEDICINE_QUANTITY),
                            Medicine.MedicineType.valueOf(m.getString(TAG_MEDICINE_TYPE)),
                            m.getString(TAG_MEDICINE_UNIT),
                            0,
                            null, null, ""
                    );

                    // adding contact to contact list
                    medicineTask.Medicines.add(med);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "************** JSONException Message Start *********************");
                Log.e(TAG, e.getMessage());
                Log.d(TAG, "************** JSONException Message Stop *********************");
            }
        }
        return medicineTask;
    }
}
