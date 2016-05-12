package in.olivo.patientcare.main.measure.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.measure.om.MeasureSchema;

/**
 * Created by Satya Madala on 11/5/16.
 * email : satya.madala@olivo.in
 */
public class MeasureSchemaListAdapter extends ArrayAdapter<MeasureSchema> {

    Context context;
    public MeasureSchemaListAdapter(Context context, List<MeasureSchema> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView != null){
            viewHolder = (ViewHolder)convertView.getTag();
        }else {
            LayoutInflater li = LayoutInflater.from(context);
            convertView = li.inflate(R.layout.measure_schema_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.textView.setText(getItem(position).getName());
        return convertView;
    }

    private class ViewHolder{
        TextView textView;
        public ViewHolder(View view){
            textView = (TextView)view.findViewById(R.id.name);
        }
    }
}
