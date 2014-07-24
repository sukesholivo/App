package com.doctl.patientcare.main.medicines;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import java.util.List;

/**
 * Created by Administrator on 6/27/2014.
 */
public class MedicineDetailAdapter extends ArrayAdapter<Medicine> {
    private static final String TAG = MedicineDetailAdapter.class.getSimpleName();

    public MedicineDetailAdapter(Context context, List<Medicine> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Medicine item = getItem(position);
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
        medicineName.setText(item.getName());

        TextView medicineUnit = (TextView)view.findViewById(R.id.medicineUnit);
        medicineUnit.setText(item.getUnit());

        TextView medicineType = (TextView)view.findViewById(R.id.medicineType);
        medicineType.setText(item.getType().toString());

        LinearLayout frequencyLayout = (LinearLayout)view.findViewById(R.id.medicineFrequencyLayout);
        frequencyLayout.removeAllViews();
        for (int i = 0; i <item.getQuantityToTake().length; i++){
            int quantity = item.getQuantityToTake()[i];
            LinearLayout taken = new LinearLayout(getContext());
            taken.setBackgroundResource(quantity > 0 ? R.drawable.circular_green : R.drawable.circular_green_empty);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 0);
            taken.setLayoutParams(lp);
            frequencyLayout.addView(taken);
        }

        String builder = "";
        for (int i = 0; i< item.getQuantityToTake().length;i++){
            int quantity = item.getQuantityToTake()[i];
            if (quantity > 0){
                builder += quantity;
                builder += " ";
                builder += item.getMedicineInstruction()[i];
                builder += " ";
            }
        }
        TextView medicineInstruction = (TextView)view.findViewById(R.id.medicineInstruction);
        medicineInstruction.setText(builder);

        TextView medicineNotes = (TextView)view.findViewById(R.id.medicineNotes);
        medicineNotes.setText("Notes: " + item.getNotes().toString());

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
