package in.olivo.patientcare.main.om.documents;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Set;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.activities.FullScreenViewActivity;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.DateUtils;

/**
 * Created by Administrator on 5/11/2015.
 */
public class DocumentAdapter extends ArrayAdapter<Document> {

    private static final String TAG = DocumentAdapter.class.getSimpleName();
    private Set<Integer> categoriesStartingIndex;
    private List<Document> items;

    public DocumentAdapter(Context context, List<Document> items) {
        super(context, 0, items);
        this.items = items;
        categoriesStartingIndex = Document.getCategoriesStartingIndex(items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.document_list_item, parent, false);
        }

        TextView title = (TextView) view.findViewById(R.id.document_title);
        TextView artist = (TextView) view.findViewById(R.id.document_description);
        TextView createdTime = (TextView) view.findViewById(R.id.document_time);
        ImageView thumb_image = (ImageView) view.findViewById(R.id.document_thumbnail);
        TextView category = (TextView) view.findViewById(R.id.category);

        Document document = getItem(position);
        String currCategory = document.getCategory();

        if (categoriesStartingIndex.contains(position)) {
            category.setText(document.getCategory() == null ? "No category" : document.getCategory() + "s");
            category.setVisibility(View.VISIBLE);
        } else {
            category.setVisibility(View.GONE);
        }

        if (document.getTimeStamp() != null) {
            createdTime.setText(DateUtils.messageTimeInThread(document.getTimeStamp()));
        } else {
            createdTime.setText("");
        }
        Log.d(TAG, "position: " + position + "category: " + currCategory);

        title.setText(document.getTitle());
        artist.setText(document.getDescription());
        String url = Constants.SERVER_URL + document.getThumbnailUrl();
        Picasso.with(getContext())
                .load(url)
                .into(thumb_image);

        view.setOnClickListener(new OnImageClickListener(position));
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        categoriesStartingIndex = Document.getCategoriesStartingIndex(items);
        super.notifyDataSetChanged();
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
