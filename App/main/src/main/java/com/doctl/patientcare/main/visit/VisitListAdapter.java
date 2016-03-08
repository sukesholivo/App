package com.doctl.patientcare.main.visit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.utility.DateUtils;
import com.doctl.patientcare.main.visit.om.Visit;

import java.util.List;

public class VisitListAdapter extends ArrayAdapter<Visit> {

    public VisitListAdapter(Context context, List<Visit> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

       ViewHolder holder;

        if( view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.visit_list_item, null, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        holder.updateData(getItem(position));
        return view;
    }

    private class ViewHolder{

        TextView doctorName;
        TextView clinicName;
        TextView date;

        public ViewHolder(View view){
            doctorName = (TextView) view.findViewById(R.id.doctor_name);
            clinicName = (TextView) view.findViewById(R.id.clinic_name);
            date = (TextView) view.findViewById(R.id.visit_date);
        }

        public void updateData(Visit visit){

            if(visit == null) return;

            if(visit.getDoctorName() != null && !visit.getDoctorName().isEmpty()){
                doctorName.setText(visit.getDoctorName());
            }

            if(visit.getClinicName() != null && ! visit.getClinicName().isEmpty()){
                clinicName.setText(visit.getClinicName());
            }

            if(visit.getDate() != null ){
                date.setText(DateUtils.messageTimeInThread(visit.getDate()));
            }
        }

    }


}