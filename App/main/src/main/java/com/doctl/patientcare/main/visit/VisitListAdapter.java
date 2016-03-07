package com.doctl.patientcare.main.visit;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

public class VisitListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;
    private final String[] docname;
    private final String[] spec;
    private final String[] hospname;
        private final Integer[] imageId;
    private String contactId = "";

    public VisitListAdapter(Activity context,
                            String[] web, Integer[] imageId, String[] docname, String[] hospname, String[] spec) {
        super(context, R.layout.custom_list_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.docname = docname;
        this.hospname = hospname;
        this.spec = spec;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.contact_name);
        TextView txtdocname = (TextView) rowView.findViewById(R.id.doctorname);
        TextView txthospname = (TextView) rowView.findViewById(R.id.doctorhospital);
        TextView txtspec = (TextView) rowView.findViewById(R.id.doctordegree);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.doctorpic);
        txtTitle.setText(web[position]);
        txtdocname.setText(docname[position]);
        txthospname.setText(hospname[position]);
        txtspec.setText(spec[position]);

        return rowView;
    }

}