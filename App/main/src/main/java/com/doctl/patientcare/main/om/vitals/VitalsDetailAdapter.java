package com.doctl.patientcare.main.om.vitals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 6/22/2014.
 */
public class VitalsDetailAdapter extends ArrayAdapter<VitalDetailData.VitalDetailValue> {
    public VitalsDetailAdapter(Context context, List<VitalDetailData.VitalDetailValue> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VitalDetailData.VitalDetailValue item = getItem(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = li.inflate(R.layout.vital_entry_list_item, parent, false);
        }

        Double vitalEntryValue1 = item.getValue1();
        TextView vitalEntry1 = (TextView)view.findViewById(R.id.vitalData1);
        vitalEntry1.setText("" + vitalEntryValue1);
        vitalEntry1.setTextColor(vitalEntryValue1 < 100 ?
                getContext().getResources().getColor(R.color.vital_fine)
                :getContext().getResources().getColor(R.color.vital_over));


        Double vitalEntryValue2 = item.getValue2();
        TextView vitalEntry2 = (TextView)view.findViewById(R.id.vitalData2);
        vitalEntry2.setText("" + vitalEntryValue2);
        vitalEntry2.setTextColor(vitalEntryValue1 < 100 ?
                getContext().getResources().getColor(R.color.vital_fine)
                :getContext().getResources().getColor(R.color.vital_over));


        TextView vitalEntryTime1 = (TextView)view.findViewById(R.id.vitalDataCondition1);
        vitalEntryTime1.setText("");

        TextView vitalEntryTime2 = (TextView)view.findViewById(R.id.vitalDataCondition2);
        vitalEntryTime2.setText("");

        Calendar c = Calendar.getInstance();
        c.setTime(item.getTime());

        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

        TextView vitalEntryDay = (TextView)view.findViewById(R.id.vitalEntryDay);
        vitalEntryDay.setText(days[c.get(Calendar.DAY_OF_WEEK)]);

        TextView vitalEntryDate = (TextView)view.findViewById(R.id.vitalEntryDate);
        vitalEntryDate.setText(c.get(Calendar.DAY_OF_MONTH) + " " + months[c.get(Calendar.MONTH)]);
        return view;
    }
}
