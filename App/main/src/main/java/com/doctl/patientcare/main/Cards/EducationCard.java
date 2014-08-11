package com.doctl.patientcare.main.Cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.doctl.patientcare.main.R;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;


public class EducationCard extends BaseCard{
    private static final String TAG = EducationCard.class.getSimpleName();
    private static final String ANDROID_DEVELOPER_KEY = "AIzaSyAWocbee6JmNy1KShjdNWy_v8_xEq0-gE0";

    public EducationCard(Context context) {
        this(context, R.layout.card_inner_content_education_video);
    }

    public EducationCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public EducationCard(Context context, int innerLayout, CardHeader cardHeader){
        super(context, innerLayout, cardHeader);
    }

    public EducationCard(Context context, CardHeader cardHeader) {
        this(context, R.layout.card_inner_content_education_video, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        setListnerToCard();
    }

    private void setListnerToCard(){
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Context context = getContext();
                Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity)context, ANDROID_DEVELOPER_KEY, "MGL6km1NBWE");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getType() {
        return CardType.EDUCATION_CARD_TYPE.ordinal();
    }
}
