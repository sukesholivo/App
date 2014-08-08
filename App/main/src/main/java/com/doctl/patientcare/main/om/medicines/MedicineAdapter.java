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
 * Created by Administrator on 6/12/2014.
 */
public class MedicineAdapter extends ArrayAdapter<Medicine>{
    public MedicineAdapter(Context context, List<Medicine> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Medicine item = getItem(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.medicine_list_item, parent, false);
        }

        TextView medicineQuantity = (TextView)view.findViewById(R.id.medicineQuantity);
        medicineQuantity.setText("" + item.getQuantity());

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
        medicineName.setText(item.getName());

        TextView medicineUnit = (TextView)view.findViewById(R.id.medicineUnit);
        medicineUnit.setText(item.getUnit());

        TextView medicineType = (TextView)view.findViewById(R.id.medicineType);
        medicineType.setText(item.getType().toString());

        return view;
    }
}
