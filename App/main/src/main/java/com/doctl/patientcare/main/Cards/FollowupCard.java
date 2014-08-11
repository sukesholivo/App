package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.followup.FollowupTask;

import java.util.ArrayList;
import java.util.Collections;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/12/2014.
 */
public class FollowupCard extends BaseCard {
    private static final String TAG = MedicineCard.class.getSimpleName();
    private FollowupTask followupTask;

    public FollowupCard(Context context) {
        this(context, R.layout.card_inner_content_followup);
    }

    public FollowupCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public FollowupCard(Context context, int innerLayout, CardHeader cardHeader, FollowupTask followupTask){
        super(context, innerLayout, cardHeader);
        this.followupTask = followupTask;
        this.setId(followupTask.getCardId());
    }

    public FollowupCard(Context context, CardHeader cardHeader, FollowupTask followupTask) {
        this(context, R.layout.card_inner_content_followup, cardHeader, followupTask);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ViewHolder viewHolder;
        if(view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.followupQuestion);
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.followupOptions);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final FollowupTask.FollowupData followupData = followupTask.getPayload();
        LinearLayout list =  viewHolder.linearLayout;
        list.removeAllViews();
        if (followupData.isMultipleChoice()){
            for (int i = 0; i< followupData.getOptions().size();i++){
                String str = followupData.getOptions().get(i);
                CheckBox cb = new CheckBox(getContext());
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        ArrayList<Integer> selected = followupTask.getPayload().getSelected();
                        if (selected == null){
                            selected = new ArrayList<Integer>();
                        }
                        int index = ((LinearLayout) compoundButton.getParent()).indexOfChild(compoundButton);
                        if(checked){
                            selected.add(index);
                        } else {
                            selected.remove(selected.indexOf(index));
                        }
                        Collections.sort(selected);
                        followupTask.getPayload().setSelected(selected);
                    }
                });
                cb.setText(str);
                list.addView(cb);
            }
        } else {
            RadioGroup rg = new RadioGroup(getContext());
            for (int i = 0; i< followupData.getOptions().size();i++){
                String str = followupData.getOptions().get(i);
                RadioButton rb = new RadioButton(getContext());
                rb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Integer> selected = followupTask.getPayload().getSelected();
                        if (selected == null) {
                            selected = new ArrayList<Integer>();
                        }
                        int index = ((LinearLayout) view.getParent()).indexOfChild(view);
                        selected.clear();
                        selected.add(index);
                        followupTask.getPayload().setSelected(selected);
                    }
                });
                rb.setText(str);
                rg.addView(rb);
            }
            list.addView(rg);
        }

        setupCardFooter(view, followupTask);
    }

    @Override
    public int getType() {
        return CardType.FOLLOWUP_CARD_TYPE.ordinal();
    }

    private static class ViewHolder extends BaseViewHolder {
        TextView textView;
        LinearLayout linearLayout;
    }
}
