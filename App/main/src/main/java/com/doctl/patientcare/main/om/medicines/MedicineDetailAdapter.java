package com.doctl.patientcare.main.om.medicines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        Medicine.MedicineType a = Medicine.MedicineType.fromInteger(item.getType());
        switch(a){
            case CAPSULE:
                medicineImage.setImageResource(R.drawable.capsule_green);
                break;
            case TABLET:
                medicineImage.setImageResource(R.drawable.capsule_green);
                break;
            case INJECTION:
                medicineImage.setImageResource(R.drawable.injection_green);
                break;
            case SYRUP:
//                TODO: get syrup green icon
                medicineImage.setImageResource(R.drawable.tablet_green);
                break;
            case DROPS:
//                TODO: get drops green icon
                medicineImage.setImageResource(R.drawable.tablet_green);
                break;
            default:
                medicineImage.setImageResource(R.drawable.tablet_green);
                break;
        }

        TextView medicineName = (TextView)view.findViewById(R.id.medicineName);
        medicineName.setText(item.getBrand());

        TextView medicineType = (TextView)view.findViewById(R.id.medicineType);
        medicineType.setText(a.toString());

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.medicineFrequencyLayout);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 20, 0);
        ll.removeAllViews();
        PrescriptionMedicine.Dosage dosage = item.getDosage();
        int morning = dosage.getMorning() != null? Integer.parseInt(dosage.getMorning()): 0;
        int noon = dosage.getNoon() != null? Integer.parseInt(dosage.getNoon()): 0;
        int evening = dosage.getEvening() != null? Integer.parseInt(dosage.getEvening()): 0;
        int night = dosage.getNight() != null? Integer.parseInt(dosage.getNight()): 0;
        String builder = "";
        boolean first = true;
        RelativeLayout rl1 = new RelativeLayout(getContext());
        if (morning > 0){
            builder += morning;
            builder += " in morning ";
            builder += item.isBeforeMeal() ? "before breakfast": "";
            first = false;
            rl1.setBackgroundResource(R.drawable.circular_green);
        } else {
            rl1.setBackgroundResource(R.drawable.circular_green_empty);
        }
        ll.addView(rl1, layoutParams);

        RelativeLayout rl2 = new RelativeLayout(getContext());
        if (noon > 0){
            builder += first ? "" : ", ";
            builder += noon;
            builder += " in afternoon ";
            builder += item.isBeforeMeal() ? "before meal": "";
            first = false;
            rl2.setBackgroundResource(R.drawable.circular_green);
        } else {
            rl2.setBackgroundResource(R.drawable.circular_green_empty);
        }
        ll.addView(rl2, layoutParams);

        RelativeLayout rl3 = new RelativeLayout(getContext());
        if (evening > 0){
            builder += first ? "" : ", ";
            builder += evening;
            builder += " in evening ";
            builder += item.isBeforeMeal() ? "before meal": "";
            first = false;
            rl3.setBackgroundResource(R.drawable.circular_green);
        } else {
            rl3.setBackgroundResource(R.drawable.circular_green_empty);
        }
        ll.addView(rl3, layoutParams);

        RelativeLayout rl4 = new RelativeLayout(getContext());
        if (night > 0){
            builder += first ? "" : ", ";
            builder += night;
            builder += " in night ";
            builder += item.isBeforeMeal() ? "before meal": "";
            rl4.setBackgroundResource(R.drawable.circular_green);
        } else {
            rl4.setBackgroundResource(R.drawable.circular_green_empty);
        }
        ll.addView(rl4, layoutParams);

        TextView medicineInstruction = (TextView)view.findViewById(R.id.medicineInstruction);
        medicineInstruction.setText(builder);

        if (item.getNotes() != null) {
            TextView medicineNotes = (TextView) view.findViewById(R.id.medicineNotes);
            medicineNotes.setText(getContext().getString(R.string.notes) + ": " + item.getNotes());
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
