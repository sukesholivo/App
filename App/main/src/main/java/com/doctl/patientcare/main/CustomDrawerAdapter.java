package com.doctl.patientcare.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 11/26/2014.
 */
public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {
    List<DrawerItem> drawerItemList;

    public CustomDrawerAdapter(Context context, List<DrawerItem> items){
        super(context, 0, items);
        this.drawerItemList = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
//        DrawerItem item = getItem(position);
        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(R.layout.drawer_list_item, parent, false);
            drawerHolder.ItemName = (TextView) view
                    .findViewById(R.id.drawer_itemName);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);

            drawerHolder.profilePic = (ImageView) view
                    .findViewById(R.id.profile_pic);
            drawerHolder.profileName = (TextView) view
                    .findViewById(R.id.profile_name);
            drawerHolder.profileEmail = (TextView) view
                    .findViewById(R.id.profile_email);

            drawerHolder.title = (TextView) view.findViewById(R.id.drawerTitle);

            drawerHolder.headerLayout = (LinearLayout) view
                    .findViewById(R.id.headerLayout);
            drawerHolder.itemLayout = (LinearLayout) view
                    .findViewById(R.id.itemLayout);
            drawerHolder.profileLayout = (LinearLayout) view
                    .findViewById(R.id.profileLayout);

            view.setTag(drawerHolder);
        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();
        }

        DrawerItem dItem = this.drawerItemList.get(position);
        if (dItem.isProfile()) {
            drawerHolder.profileLayout.setVisibility(LinearLayout.VISIBLE);
            drawerHolder.headerLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.itemLayout.setVisibility(LinearLayout.GONE);
            UserProfile userProfile = Utils.getUserDataFromSharedPreference(getContext());
            drawerHolder.profileName.setText(userProfile.getDisplayName());
            drawerHolder.profileEmail.setText(userProfile.getEmail());

            if (!userProfile.getProfilePicUrl().isEmpty()) {
                Picasso.with(getContext())
                        .load(Constants.SERVER_URL + userProfile.getProfilePicUrl())
                        .into(drawerHolder.profilePic);
            } else {
                drawerHolder.profilePic.setImageResource(R.drawable.profile_dummy);
            }
        } else if (dItem.getTitle() != null) {
            drawerHolder.profileLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.headerLayout.setVisibility(LinearLayout.VISIBLE);
            drawerHolder.itemLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.title.setText(dItem.getTitle());
        } else {
            drawerHolder.profileLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.headerLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.itemLayout.setVisibility(LinearLayout.VISIBLE);
            drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
                    dItem.getImgResID()));
            drawerHolder.ItemName.setText(dItem.getItemName());
        }
        return view;
    }

    private static class DrawerItemHolder {
        TextView ItemName, title, profileName, profileEmail;
        ImageView icon, profilePic;
        LinearLayout headerLayout, itemLayout, profileLayout;
    }
}
