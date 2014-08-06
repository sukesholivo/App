package com.doctl.patientcare.main.om.medicines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import java.util.List;

/**
 * Created by Administrator on 6/27/2014.
 */
public class MedicineDetailAdapter extends ArrayAdapter<PrescriptionMedicine> {
    private static final String TAG = MedicineDetailAdapter.class.getSimpleName();

    public MedicineDetailAdapter(Context context, List<PrescriptionMedicine> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PrescriptionMedicine item = getItem(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.medicine_detail_list_item, parent, false);
        }

        ImageView medicineImage = (ImageView)view.findViewById(R.id.medicineIcon);
        switch(item.getType()){
            case CAPSULE:
                medicineImage.setImageResource(R.drawable.capsule_green);
                break;
            case TABLET:
                medicineImage.setImageResource(R.drawable.tablet_green);
                break;
            case INJECTION:
                medicineImage.setImageResource(R.drawable.injection_green);
                break;
            default:
                medicineImage.setImageResource(R.drawable.capsule_green);
                break;
        }

        TextView medicineName = (TextView)view.findViewById(R.id.medicineName);
        medicineName.setText(item.getBrand());

        TextView medicineType = (TextView)view.findViewById(R.id.medicineType);
        medicineType.setText(item.getType().toString());

        String builder = "";

        for (int i = 0; i< item.getDosage().size();i++){
            PrescriptionMedicine.Dosage dosage = item.getDosage().get(i);
            int quantity = dosage.getQuantity();

            if (quantity > 0){
                builder += quantity;
                builder += " ";
                builder += item.getDosage().get(i).getConstraint();
                builder += " ";
            }
        }
        TextView medicineInstruction = (TextView)view.findViewById(R.id.medicineInstruction);
        medicineInstruction.setText(builder);

        TextView medicineNotes = (TextView)view.findViewById(R.id.medicineNotes);
        medicineNotes.setText("Notes: " + item.getNotes());

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
