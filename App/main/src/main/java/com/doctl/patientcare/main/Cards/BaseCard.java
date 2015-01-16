package com.doctl.patientcare.main.Cards;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/13/2014.
 */
public abstract class BaseCard extends Card {
    private static final String TAG = BaseCard.class.getSimpleName();
    protected boolean isSwipable = true;
    protected boolean showFooter = true;
    public BaseCard(Context context, int layout){
        super(context, layout);
        setupCard(new CardHeaderInnerView(getContext()));
    }
    public BaseCard(Context context, int layout, CardHeader header){
        super(context, layout);
        setupCard(header);
    }

    private void setupCard(CardHeader header){
        if (header != null) {
            addCardHeader(header);
        }
        if (isSwipable) {
            setSwipeable(true);
        }
        this.setBackgroundResourceId(R.drawable.card_background_gray);
    }

    public void setupCardFooter(View view, String targetPoint){
        TextView targetPointTextView = (TextView)view.findViewById(R.id.targetPoint);
        targetPointTextView.setText(targetPoint);

        ImageView imageView = (ImageView)view.findViewById(R.id.influencerImage);
        imageView.setImageResource(R.drawable.profile_dummy);
    }

    public void setupCardFooter(View view, BaseTask task){
        BaseViewHolder viewHolder;
        if(view.getTag() == null) {
            viewHolder = new BaseViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.targetPoint);
            viewHolder.textView1 = (TextView) view.findViewById(R.id.pointText);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.influencerImage);
            viewHolder.footerSet = true;
            view.setTag(viewHolder);
        } else {
            viewHolder = (BaseViewHolder) view.getTag();
            if (!viewHolder.footerSet){
                viewHolder.textView = (TextView) view.findViewById(R.id.targetPoint);
                viewHolder.textView1 = (TextView) view.findViewById(R.id.pointText);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.influencerImage);
                viewHolder.footerSet = true;
            }
        }

        TextView targetPointTextView = viewHolder.textView;
        TextView pointTextView = viewHolder.textView1;
        ImageView influencerImage = viewHolder.imageView;
        if (showFooter) {
            targetPointTextView.setText("" + task.getPoints());
            if (task.getSource() != null) {
                Picasso.with(getContext())
                        .load(Constants.SERVER_URL + task.getSource()
                                .getProfilePicUrl())
                        .into(influencerImage);
            }
        } else {
            targetPointTextView.setVisibility(View.GONE);
            pointTextView.setVisibility(View.GONE);
            influencerImage.setVisibility(View.GONE);
        }
    }

    @Override
    public abstract int getType();

    public void UpdateTask(){

    }

    public void UpdateTask(String cardId, JSONObject data){
        new UpdateTasks().execute(Constants.CARDS_URL + cardId + "/", data);
    }
    public enum CardType{
        DUMMY_CARD_TYPE,
        MEDICINE_CARD_TYPE,
        VITAL_CARD_TYPE,
        EDUCATION_CARD_TYPE,
        FOLLOWUP_CARD_TYPE,
        MESSAGE_CARD_TYPE
    }

    protected static class BaseViewHolder {
        TextView textView, textView1;
        ImageView imageView;
        boolean footerSet = false;
    }

    private class UpdateTasks extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... arg0) {
            String url = (String)arg0[0];
            JSONObject data= (JSONObject)arg0[1];

            HTTPServiceHandler serviceHandler = new HTTPServiceHandler(getContext());
            String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.PATCH, null, data);
            if (response == null || response.isEmpty()){
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getContext(), "Server Error.", Toast.LENGTH_LONG).show();
                    }
                });
            }
//            Logger.d(TAG, response);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
