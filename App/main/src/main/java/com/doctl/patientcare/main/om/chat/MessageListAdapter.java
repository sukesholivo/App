package com.doctl.patientcare.main.om.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */
public class MessageListAdapter  extends ArrayAdapter<Message> {
    public MessageListAdapter(Context context, List<Message> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message item = getItem(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = li.inflate(R.layout.questions_list_items, parent, false);
        }

        TextView questionTextView = (TextView)view.findViewById(R.id.question_text);
        questionTextView.setText(item.getText());
        return view;
    }
}
