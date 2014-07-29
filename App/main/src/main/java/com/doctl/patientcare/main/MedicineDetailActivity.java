package com.doctl.patientcare.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.doctl.patientcare.main.om.medicines.Medicine;
import com.doctl.patientcare.main.om.medicines.MedicineDetailAdapter;

import java.util.ArrayList;
import java.util.List;

public class MedicineDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        MedicineDetailAdapter medicines = new MedicineDetailAdapter(this, buildArrayHelper());
        ListView list = (ListView)this.findViewById(R.id.medicineDetailList);
        list.setAdapter(medicines);
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
