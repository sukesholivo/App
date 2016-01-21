package com.doctl.patientcare.main.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.contact.User;
import com.doctl.patientcare.main.services.DownloadImageTask;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by satya on 19/1/16.
 */
public class ContactListAdapter extends ArrayAdapter<User> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private LayoutInflater li ;

    private Map<Integer, String> categoryPositions;

    public ContactListAdapter(Context context, List<User> users, Map<Integer, String> categoryPositions) {
        super(context, 0, users);
        this.categoryPositions = categoryPositions;
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

        User user = getItem(position);
        //add regular item
        if( rowType == TYPE_ITEM && holder instanceof ListItemViewHolder){
            ListItemViewHolder listItemViewHolder = (ListItemViewHolder) holder;
            ListItemViewHolder.loadData(listItemViewHolder, user);
        //add item with heading
        }else if(rowType == TYPE_SEPARATOR && holder != null){
            ListCategoryViewHolder listCategoryViewHolder = (ListCategoryViewHolder) holder;
            ListCategoryViewHolder.loadData(listCategoryViewHolder, user, categoryPositions.get(position));
        }

        return convertView;
    }

    public static class ListItemViewHolder {
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

        public static void loadData(ListItemViewHolder holder, User user){

            if( user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()){
                new DownloadImageTask(holder.profilePic).execute(user.getProfilePicUrl());
            }

            if(user.getName() != null && !user.getName().isEmpty()){
                holder.name.setText(user.getName());
            }
        }
    }

    public static class ListCategoryViewHolder extends  ListItemViewHolder{
        public TextView categoryName;
        public static ListCategoryViewHolder createView(View view){
            ListCategoryViewHolder holder = new ListCategoryViewHolder();
            holder =(ListCategoryViewHolder) ListItemViewHolder.createView(view, holder);
            holder.categoryName = (TextView) view.findViewById(R.id.textSeparator);
            return holder;
        }

        public static void loadData(ListCategoryViewHolder holder, User user, String categoryName){
            ListItemViewHolder.loadData(holder, user);
            holder.categoryName.setText(categoryName);
        }
    }
}
