package com.doctl.patientcare.main.om;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.Cards.BaseCard;
import com.doctl.patientcare.main.Cards.EducationCard;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.medicines.Medicine;

import java.util.List;

/**
 * Created by Administrator on 6/14/2014.
 */
public class TasksAdapter extends ArrayAdapter<BaseCard> {
    public TasksAdapter(Context context, List<BaseCard> objects) {
        super(context, 0, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseCard item = getItem(position);
        BaseCard card;
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = li.inflate(R.layout.medicine_list_item, parent, false);
        }

//        switch (item.Type){
//            case EDUCATION:
//                card = new EducationCard(getContext());
//                break;
//            case MEDICINE:
//                break;
//            case VITAL:
//                break;
//            case WALK:
//                break;
//        }
        return view;
    }
}
