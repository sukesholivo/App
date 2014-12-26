package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.followup.FollowupTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/12/2014.
 */
public class FollowupCard extends BaseCard {
    private static final String TAG = FollowupCard.class.getSimpleName();
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
        if (view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.followupNotes);
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.followupOptions);
            viewHolder.editText = (EditText) view.findViewById(R.id.followupComments);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final FollowupTask.FollowupData followupData = followupTask.getPayload();

        if (followupData.getNotes() != null && !followupData.getNotes().isEmpty()){
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.textView.setText(followupData.getNotes());
        } else {
            viewHolder.textView.setVisibility(View.GONE);
        }
        LinearLayout list = viewHolder.linearLayout;
        list.removeAllViews();

        if (followupData.getType().toLowerCase().equals("feedback")) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View ratingContainer = li.inflate(R.layout.rating_bar, parent, false);

            RatingBar ratingBar = (RatingBar)ratingContainer.findViewById(R.id.rating_bar);
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(0xff42BD41, PorterDuff.Mode.SRC_ATOP);
            ratingBar.setNumStars(followupData.getOptions().size());
            ratingBar.setRating(0);
            ratingBar.setStepSize(1);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                    ArrayList<Integer> selected = followupTask.getPayload().getSelected();
                    if (selected == null) {
                        selected = new ArrayList<Integer>();
                    }
                    selected.clear();
                    selected.add((int) (rating - 1));
                    followupTask.getPayload().setSelected(selected);
                }
            });
            list.addView(ratingContainer);
        }
        else {
            if (followupData.isMultipleChoice()) {
                for (int i = 0; i < followupData.getOptions().size(); i++) {
                    String str = followupData.getOptions().get(i);
                    CheckBox cb = new CheckBox(getContext());
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            ArrayList<Integer> selected = followupTask.getPayload().getSelected();
                            if (selected == null) {
                                selected = new ArrayList<Integer>();
                            }
                            int index = ((LinearLayout) compoundButton.getParent()).indexOfChild(compoundButton);
                            if (checked) {
                                selected.add(index);
                            } else {
                                selected.remove(selected.indexOf(index));
                            }
                            Collections.sort(selected);
                            followupTask.getPayload().setSelected(selected);
                        }
                    });
                    cb.setTextSize(20);
                    cb.setText(str);
                    list.addView(cb);
                    cb.setChecked(followupTask.getPayload().getSelected() != null && followupTask.getPayload().getSelected().contains(i));
                }
            } else {
                RadioGroup rg = new RadioGroup(getContext());
                for (int i = 0; i < followupData.getOptions().size(); i++) {
                    String str = followupData.getOptions().get(i);
                    LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    RadioButton rb = (RadioButton) li.inflate(R.layout.radio_button, null);
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
                    rb.setTextSize(20);
                    rb.setText(str);
                    rb.setChecked(followupTask.getPayload().getSelected() != null && followupTask.getPayload().getSelected().contains(i));
                    rg.addView(rb);
                }

                list.addView(rg);
            }
        }
        viewHolder.editText.setVisibility(View.VISIBLE);
        viewHolder.editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                followupTask.getPayload().setComment(s.toString());
            }
        });
        viewHolder.editText.setText(followupTask.getPayload().getComment());

        setupCardFooter(view, followupTask);
    }

    @Override
    public int getType() {
        return CardType.FOLLOWUP_CARD_TYPE.ordinal();
    }

    @Override
    public void UpdateTask(){
        JSONObject data;
        followupTask.setState(BaseTask.CardState.DONE);
        String cardId = followupTask.getCardId();
        try {
            data = followupTask.getDataToPatch();
            Log.d(TAG, data.toString());
            UpdateTask(cardId, data);
        }catch (JSONException e){
            Log.e(TAG, e.toString());
        }
    }
    private static class ViewHolder extends BaseViewHolder {
        TextView textView;
        LinearLayout linearLayout;
        EditText editText;
    }
}
