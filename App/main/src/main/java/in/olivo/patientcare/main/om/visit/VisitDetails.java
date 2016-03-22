package in.olivo.patientcare.main.om.visit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Satya Madala on 4/3/16.
 * email : satya.madala@olivo.in
 */
public class VisitDetails {

    List<VisitResponse> visitResponses;

    public static void main(String args[]) {
        StringBuilder s = new StringBuilder("w");


        List<VisitResponse> visitResponseList = new ArrayList<>();
        String response = "[{\"followup\": {\"followup_purpose\": \"Routine checkup\", \"followup_date\": \"Sat Mar 12 00:00:00 IST 2016\"}, \"visit_id\": \"ad96d688-3f9d-477b-82dd-3306a42b504b\", \"investigations\": [], \"measures\": [{\"measure_group\": \"Vital\", \"unit\": \"Degees F\", \"value\": \"98\", \"measure\": \"Temperature\"}, {\"measure_group\": \"Vital\", \"value\": \"45\", \"measure\": \"Respiration\"}], \"patient_id\": \"10566971251207\", \"visit_date\": \"2016-03-02T06:59:28.511000\", \"diagnosis\": {\"cancer_organ\": \"Ovary\", \"cancer_state\": \"2nd Ca\", \"diagnosis_date\": \"2016-03-02 00:00:00.0\", \"cancer_stage\": \"Metastatic IIIB\", \"diagnosis\": \"NHL NOS\", \"cancer_second_organ\": \"Ovary\", \"patient_status\": \"Alive with disease\", \"provisional_final\": \"Final\"}, \"complaints\": [{\"duration_unit\": \"Days\", \"complaint\": \"Fever\", \"intensity\": \"Moderate\"}, {\"duration_unit\": \"Days\", \"complaint\": \"Cardiac Arrythmia\", \"intensity\": \"Moderate\"}], \"doctor_id\": \"10566917183900\", \"special_cresp\": {\"Hb\": \"10\", \"transfusion\": \"No\", \"frequency\": \"WEEKLY\", \"dosage\": \"25 mcg\", \"transfusion_count\": \"0.0\"}, \"order_investigations\": [{\"test\": \"TLC\", \"test_group\": \"BioChemistry\", \"test_date\": \"2016-03-02 00:00:00.0\"}]}, {\"complaints\": {\"51\": {\"duration_unit\": \"Days\", \"complaint\": \"Cardiac Arrythmia\", \"intensity\": \"Moderate\", \"duration_num\": \"8.0\"}, \"50\": {\"duration_unit\": \"Days\", \"complaint\": \"bad neck pain\", \"intensity\": \"Moderate\", \"duration_num\": \"10.0\"}}, \"order_investigations\": [{\"test\": \"TLC\", \"test_group\": \"BioChemistry\", \"test_date\": \"2016-03-04 00:00:00.0\"}, {\"test\": \"S Alk Phosphatase\", \"test_group\": \"BioChemistry\", \"test_date\": \"2016-03-04 00:00:00.0\"}], \"investigations\": []}]";
        try {
            JSONArray root = new JSONArray(response);

            for (int i = 0; i < root.length(); i++) {
                JSONObject visits = root.getJSONObject(i);
                Iterator<String> keys = visits.keys();
                String key;

                while (keys.hasNext()) {
                    key = keys.next();
                    Object val = visits.get(key);

                    if (val instanceof JSONObject) {


                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void printKeys(Object obj) {

        if (obj instanceof JSONObject) {

        }
    }

    public static class JSONData {
        String key;
        JSONObject jsonObject; // either one of JSONObject or JSONArray has value, other will be null
        JSONArray jsonArray;
    }

    public static class VisitResponse {
        String doctorId;
        String visitDate;
        List<String> visitDetailsInHTML;
    }
}
