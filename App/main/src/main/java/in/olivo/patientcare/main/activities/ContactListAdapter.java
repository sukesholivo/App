package in.olivo.patientcare.main.activities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.UserProfile;
import in.olivo.patientcare.main.services.DownloadImageTask;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.ImageUtils;
import in.olivo.patientcare.main.utility.StringUtils;

/**
 * Created by satya on 19/1/16.
 */
public class ContactListAdapter extends ArrayAdapter<UserProfile> {

    private static final String TAG = ContactListAdapter.class.getSimpleName();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private LayoutInflater li;
    private List<UserProfile> items;
    private String filter;

    private Map<Integer, String> categoryPositions;

    public ContactListAdapter(Context context, List<UserProfile> userProfiles, Map<Integer, String> categoryPositions) {

        super(context, 0, userProfiles);
        this.categoryPositions = categoryPositions;
        this.items = userProfiles;
        li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getItemViewType(int position) {
        return categoryPositions.containsKey(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        int rowType = getItemViewType(position);

        ListItemViewHolder holder = null;

        // should create new one if convert view is other type
        if (convertView != null) {
            holder = (ListItemViewHolder) convertView.getTag();
        } else {
            convertView = li.inflate(R.layout.contact_list_item, null);
            holder = ListItemViewHolder.createView(convertView, getContext());
            convertView.setTag(holder);
        }

        UserProfile userProfile = getItem(position);
        //add regular item
        if (rowType == TYPE_ITEM) {
            holder.categoryName.setVisibility(View.GONE);
        } else if (rowType == TYPE_SEPARATOR) {
            holder.categoryName.setVisibility(View.VISIBLE);
        }

        holder.loadData(userProfile, filter);

        return convertView;
    }

    public void updateData(List<UserProfile> items, Map<Integer, String> categoryPositions, String filter) {

        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }

        this.categoryPositions.clear();
        if (categoryPositions != null) {
            this.categoryPositions.putAll(categoryPositions);
        }
        this.filter = filter;
    }

    private static class ListItemViewHolder {
        public TextView categoryName;
        public ImageView profilePic;
        public TextView name;
        public TextView about;
        public Context context;

        public static ListItemViewHolder createView(View view, Context context) {

            ListItemViewHolder holder = new ListItemViewHolder();
            holder.context = context;
            return createView(view, holder);
        }

        public static ListItemViewHolder createView(View view, ListItemViewHolder holder) {
            holder.profilePic = (ImageView) view.findViewById(R.id.contact_profile_pic);
            holder.name = (TextView) view.findViewById(R.id.contact_name);
            holder.about = (TextView) view.findViewById(R.id.contact_about);
            holder.categoryName = (TextView) view.findViewById(R.id.textSeparator);
            return holder;
        }

        public void loadData(UserProfile userProfile, String filter) {

            if (userProfile.getProfilePicUrl() != null && !userProfile.getProfilePicUrl().isEmpty()) {
                new ImageUtils.DownloadFileAndDisplay(this.profilePic, Constants.SERVER_URL + userProfile.getProfilePicUrl(), false, context).execute();
                new DownloadImageTask(this.profilePic, null).execute(Constants.SERVER_URL + userProfile.getProfilePicUrl());
            }

            if (userProfile.getDisplayName() != null && !userProfile.getDisplayName().isEmpty()) {

                if (filter != null && userProfile.getDisplayName().toLowerCase().contains(filter.toLowerCase())) {
                    int startPos = userProfile.getDisplayName().toLowerCase().indexOf(filter.toLowerCase());
                    int endPos = startPos + filter.length();
                    Spannable spannable = new SpannableString(userProfile.getDisplayName());
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.rgb(112, 165, 196)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    this.name.setText(spannable);
                } else {
                    this.name.setText(userProfile.getDisplayName());
                }
            }

            if (userProfile.getPhone() != null && !userProfile.getPhone().isEmpty()) {
                if (filter != null && userProfile.getPhone().toLowerCase().contains(filter.toLowerCase())) {
                    int startPos = userProfile.getPhone().toLowerCase().indexOf(filter.toLowerCase());
                    int endPos = startPos + filter.length();
                    Spannable spannable = new SpannableString(userProfile.getPhone());
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.rgb(112, 165, 196)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    this.about.setText(spannable);
                } else {
                    this.about.setText(userProfile.getPhone());
                }
            }

            if (userProfile.getRole() != null && !userProfile.getRole().isEmpty()) {
                this.categoryName.setText(StringUtils.toDisplayCase(userProfile.getRole()) + "s");
            }
        }
    }

    /*public static class ListCategoryViewHolder extends  ListItemViewHolder{
        public TextView categoryName;
        public static ListCategoryViewHolder createView(View view){
            ListCategoryViewHolder holder = new ListCategoryViewHolder();
            holder =(ListCategoryViewHolder) ListItemViewHolder.createView(view, holder);
            holder.categoryName = (TextView) view.findViewById(R.id.textSeparator);
            return holder;
        }

        public static void loadData(ListCategoryViewHolder holder, UserProfile userProfile, String categoryName, String filter){
            ListItemViewHolder.loadData(holder, userProfile, filter);
            holder.categoryName.setText(categoryName);
        }
    }*/
}
