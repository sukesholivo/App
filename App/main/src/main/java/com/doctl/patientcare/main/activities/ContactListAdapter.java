package com.doctl.patientcare.main.activities;

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

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.services.DownloadImageTask;
import com.doctl.patientcare.main.utility.Constants;

import java.util.List;
import java.util.Map;

/**
 * Created by satya on 19/1/16.
 */
public class ContactListAdapter extends ArrayAdapter<UserProfile> {

    private static final String TAG = ContactListAdapter.class.getSimpleName();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private LayoutInflater li ;
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
        return categoryPositions.containsKey(position)? TYPE_SEPARATOR: TYPE_ITEM;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        int rowType = getItemViewType(position);

        Object holder=null;

        // should create new one if convert view is other type
        if( convertView != null){

            holder = convertView.getTag();
            if( (rowType == TYPE_ITEM && holder instanceof ListCategoryViewHolder) || ( rowType == TYPE_SEPARATOR && !(holder instanceof ListCategoryViewHolder))){
                convertView = null;
            }
        }

        if (convertView == null) {

            switch (rowType) {
                case TYPE_ITEM:
                    convertView = li.inflate(R.layout.contact_list_item, null);
                    holder = ListItemViewHolder.createView(convertView);
                    convertView.setTag(holder);
                    break;
                case TYPE_SEPARATOR:
                    convertView = li.inflate(R.layout.contact_list_category, null);
                    holder = ListCategoryViewHolder.createView(convertView);
                    convertView.setTag(holder);
                    break;
            }
        }

        UserProfile userProfile = getItem(position);
        //add regular item
        if( rowType == TYPE_ITEM && holder instanceof ListItemViewHolder){
            ListItemViewHolder listItemViewHolder = (ListItemViewHolder) holder;
            ListItemViewHolder.loadData(listItemViewHolder, userProfile, filter);
        //add item with heading
        }else if(rowType == TYPE_SEPARATOR && holder != null){
            ListCategoryViewHolder listCategoryViewHolder = (ListCategoryViewHolder) holder;
            ListCategoryViewHolder.loadData(listCategoryViewHolder, userProfile, categoryPositions.get(position), filter);
        }

        return convertView;
    }

    public void updateData(List<UserProfile> items, Map<Integer, String> categoryPositions, String filter){

        this.items.clear();
        if( items != null ){
            this.items.addAll(items);
        }

        this.categoryPositions.clear();
        if( categoryPositions != null ){
            this.categoryPositions.putAll(categoryPositions);
        }
        this.filter = filter;
    }

    private static class ListItemViewHolder {
        public ImageView profilePic;
        public TextView name;
        public TextView about;

        public static ListItemViewHolder createView(View view){

            ListItemViewHolder holder = new ListItemViewHolder();
            return createView(view, holder);
        }

        public static ListItemViewHolder createView(View view, ListItemViewHolder holder){
            holder.profilePic = (ImageView) view.findViewById(R.id.contact_profile_pic);
            holder.name = (TextView) view.findViewById(R.id.contact_name);
            holder.about = (TextView) view.findViewById(R.id.contact_about);
            return holder;
        }

        public static void loadData(ListItemViewHolder holder, UserProfile userProfile, String filter){

            if( userProfile.getProfilePicUrl() != null && !userProfile.getProfilePicUrl().isEmpty()){
                new DownloadImageTask(holder.profilePic, null).execute(Constants.SERVER_URL + userProfile.getProfilePicUrl());
            }

            if(userProfile.getDisplayName() != null && !userProfile.getDisplayName().isEmpty()){

                if( filter != null && userProfile.getDisplayName().toLowerCase().contains(filter.toLowerCase()) ) {
                    int startPos = userProfile.getDisplayName().toLowerCase().indexOf(filter.toLowerCase());
                    int endPos = startPos + filter.length();
                    Spannable spannable = new SpannableString(userProfile.getDisplayName());
                    ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.rgb(112, 165, 196) });
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.name.setText(spannable);
                }else{
                    holder.name.setText(userProfile.getDisplayName());
                }
            }
            if( userProfile.getAddress() != null && userProfile.getAddress().getCity() != null){
                holder.about.setText(userProfile.getAddress().getCity());
            }else{
                holder.about.setVisibility(View.GONE);
            }
        }
    }

    private static class ListCategoryViewHolder extends  ListItemViewHolder{
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
    }
}
