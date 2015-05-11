package com.doctl.patientcare.main.om.documents;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.activities.FullScreenViewActivity;
import com.doctl.patientcare.main.utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 5/11/2015.
 */
public class DocumentAdapter extends ArrayAdapter<Document> {

    public DocumentAdapter(Context context, List<Document> objects) {
        super(context, 0, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.document_list_item, parent, false);
        }

        TextView title = (TextView)view.findViewById(R.id.document_title);
        TextView artist = (TextView)view.findViewById(R.id.document_description);
        TextView duration = (TextView)view.findViewById(R.id.document_time);
        ImageView thumb_image=(ImageView) view.findViewById(R.id.document_thumbnail);


        Document document = getItem(position);

        title.setText(document.getTitle());
        artist.setText(document.getDescription());
        String url = Constants.SERVER_URL + document.getThumbnailUrl();
        Picasso.with(getContext())
                .load(url)
                .into(thumb_image);

        view.setOnClickListener(new OnImageClickListener(position));
        return view;
    }

    class OnImageClickListener implements View.OnClickListener {
        int _postion;

        public OnImageClickListener(int position) {
            this._postion = position;
        }
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getContext(), FullScreenViewActivity.class);
            Document document = getItem(_postion);
            i.putExtra("url", document.getImageUrl());
            getContext().startActivity(i);
        }
    }
}
