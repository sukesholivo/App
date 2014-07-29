package com.doctl.patientcare.main.vitals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import java.util.List;

/**
 * Created by Administrator on 6/22/2014.
 */
public class VitalsAdapter extends ArrayAdapter<Vitals> {
    public VitalsAdapter(Context context, List<Vitals> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Vitals item = getItem(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = li.inflate(R.layout.vital_entry_list_item, parent, false);
        }

        int vitalEntryValue1 = item.getReading1();
        TextView vitalEntry1 = (TextView)view.findViewById(R.id.vitalEntry1);
        vitalEntry1.setText("" + vitalEntryValue1);
        vitalEntry1.setTextColor(vitalEntryValue1 < 100 ?
                getContext().getResources().getColor(R.color.vital_fine)
                :getContext().getResources().getColor(R.color.vital_over));


        int vitalEntryValue2 = item.getReading2();
        TextView vitalEntry2 = (TextView)view.findViewById(R.id.vitalEntry2);
        vitalEntry2.setText("" + vitalEntryValue2);
        vitalEntry2.setTextColor(vitalEntryValue1 < 100 ?
                getContext().getResources().getColor(R.color.vital_fine)
                :getContext().getResources().getColor(R.color.vital_over));

        TextView vitalEntryTime1 = (TextView)view.findViewById(R.id.vitalEntryTime1);
        vitalEntryTime1.setText("" + item.getReadingTime1());

        TextView vitalEntryTime2 = (TextView)view.findViewById(R.id.vitalEntryTime2);
        vitalEntryTime2.setText("" + item.getReadingTime2());

        TextView vitalEntryDay = (TextView)view.findViewById(R.id.vitalEntryDay);
        vitalEntryDay.setText("" + item.getReadingDay());

        TextView vitalEntryDate = (TextView)view.findViewById(R.id.vitalEntryDate);
        vitalEntryDate.setText("" + item.getReadingDate());
        return view;
    }
}
