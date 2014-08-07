package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.BaseTask;
import com.squareup.picasso.Picasso;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/13/2014.
 */
public abstract class BaseCard extends Card {
    private static final String TAG = BaseCard.class.getSimpleName();

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
        setSwipeable(true);
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
            viewHolder.imageView = (ImageView) view.findViewById(R.id.influencerImage);
            viewHolder.footerSet = true;
            view.setTag(viewHolder);
        } else {
            viewHolder = (BaseViewHolder) view.getTag();
            if (!viewHolder.footerSet){
                viewHolder.textView = (TextView) view.findViewById(R.id.targetPoint);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.influencerImage);
                viewHolder.footerSet = true;
            }
        }

        TextView targetPointTextView = viewHolder.textView;
        ImageView influencerImage = viewHolder.imageView;

        targetPointTextView.setText("" + task.getPoints());
        Picasso.with(getContext())
                .load(task.getSource()
                .getProfilePicUrl())
                .into(influencerImage);
    }

    @Override
    public abstract int getType();

    public enum CardType{
        DUMMY_CARD_TYPE,
        MEDICINE_CARD_TYPE,
        VITAL_CARD_TYPE,
        EDUCATION_CARD_TYPE
    }

    protected static class BaseViewHolder {
        TextView textView;
        ImageView imageView;
        boolean footerSet = false;
    }
}
