package in.olivo.patientcare.main.om.medicines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.education.ObjectiveTask;

/**
 * Created by Satya Madala on 14/7/16.
 * email : satya.madala@olivo.in
 */
public class ObjectiveAdapter extends ArrayAdapter<ObjectiveTask.ObjectiveData.Option> {
    ObjectiveTask task;
    public ObjectiveAdapter(Context context, List<ObjectiveTask.ObjectiveData.Option> objects, ObjectiveTask task) {
        super(context, 0, objects);
        this.task = task;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ObjectiveTask.ObjectiveData.Option item = getItem(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.objective_list_item, parent, false);
        }

        Button value = (Button) view.findViewById(R.id.value);
        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.getPayload().setAnswerId(item.getId());
                Toast.makeText(getContext(), item.getValue(), Toast.LENGTH_SHORT).show();
                v.setBackgroundColor(0xFFFFFF);
            }
        });
        /*TextView id = (TextView) view.findViewById(R.id.id);
        id.setText(item.getId());*/
        value.setText(item.getValue());

        if(item.getId().equals(task.getPayload().getAnswerId())){
            value.setBackgroundColor(0xFFFFFF);
        }


        return view;
    }
}

