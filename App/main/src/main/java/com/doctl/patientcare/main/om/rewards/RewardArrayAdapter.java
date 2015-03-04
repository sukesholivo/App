package com.doctl.patientcare.main.om.rewards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import java.util.List;

/**
 * Created by Administrator on 2/26/2015.
 */
public class RewardArrayAdapter extends ArrayAdapter<Rewards.Reward> {
    private int totalPoints;
    public RewardArrayAdapter(Context context, List<Rewards.Reward> objects, int totalPoints) {
        super(context, 0, objects);
        this.totalPoints = totalPoints;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        Rewards.Reward item = getItem(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.reward_list_item, parent, false);
        }
        TextView rewardTitle = (TextView)view.findViewById(R.id.reward_title);
        rewardTitle.setText(item.getTitle());
        TextView rewardDescription = (TextView)view.findViewById(R.id.reward_description);
        rewardDescription.setText(item.getDescription());
        TextView rewardPoints = (TextView)view.findViewById(R.id.reward_points);
        rewardPoints.setText("" + item.getPoints());
        ImageView rewardLockImage = (ImageView) view.findViewById(R.id.reward_lock_icon);
        if (item.getPoints() <= totalPoints){
            rewardPoints.setTextColor(getContext().getResources().getColor(R.color.points_green));
            rewardTitle.setTextColor(getContext().getResources().getColor(R.color.points_primary));
            rewardDescription.setTextColor(getContext().getResources().getColor(R.color.points_secondary));
            rewardLockImage.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
