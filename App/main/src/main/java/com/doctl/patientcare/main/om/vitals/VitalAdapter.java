package com.doctl.patientcare.main.om.vitals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.doctl.patientcare.main.R;

import java.util.List;

/**
 * Created by Administrator on 8/8/2014.
 */
public class VitalAdapter extends ArrayAdapter<Vitals> {
    private static final String TAG = VitalAdapter.class.getSimpleName();
    public VitalAdapter(Context context, List<Vitals> objects) {
        super(context, 0, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.vital_card_list_item, parent, false);
        }
//        TODO: We don't need this adapter anymore. Remove this file later
//        TextView vitalTitle = (TextView)view.findViewById(R.id.vitalTitle);
//        vitalTitle.setText(item.getName());
//
//        TextView vitalCondition = (TextView)view.findViewById(R.id.vitalCondition);
//        vitalCondition.setText(item.getCondition());
//
//        TextView vitalUnit = (TextView)view.findViewById(R.id.vitalUnit);
//        vitalUnit.setText(item.getUnit());

        return view;
    }
}
