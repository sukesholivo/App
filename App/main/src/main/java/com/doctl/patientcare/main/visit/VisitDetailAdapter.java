package com.doctl.patientcare.main.visit;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

/**
 * Created by abc on 2/19/2016.
 */
public class VisitDetailAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] name;
    private final String[] data;

    //private final Integer[] imageId;
    public VisitDetailAdapter(Activity context,
                              String[] name, String[] data) {
        super(context, R.layout.custom_list_item2, name);
        this.context = context;
        this.name = name;
        this.data = data;
        //this.imageId = imageId;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_list_item2, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.contact_name);
        TextView txtdata = (TextView) rowView.findViewById(R.id.contact_about);
        // ImageView imageView = (ImageView) rowView.findViewById(R.id.contact_profile_pic);
        txtTitle.setText(name[position]);
        txtdata.setText(data[position]);

        // imageView.setImageResource(imageId[position]);
        return rowView;

    }
}