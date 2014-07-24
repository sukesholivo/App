package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/13/2014.
 */
public abstract class BaseCard extends Card {
    public final static int VITAL_CARD_TYPE = 1;
    public final static int MEDICINE_CARD_TYPE = 2;
    public final static int EDUCATION_CARD_TYPE = 3;

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
        imageView.setImageResource(R.drawable.me);
    }
}
