package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.doctl.patientcare.main.R;


import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/24/2014.
 */
public class QuestionCard extends BaseCard {
    public QuestionCard(Context context) {
        this(context, R.layout.card_inner_content_question);
    }

    public QuestionCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public QuestionCard(Context context, int innerLayout, CardHeader cardHeader){
        super(context, innerLayout, cardHeader);
    }

    public QuestionCard(Context context, CardHeader cardHeader){
        this(context, R.layout.card_inner_content_question, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
//        this.setBackgroundResourceId(R.drawable.card_background_blue);
    }

    @Override
    public int getType() {
        return CardType.DUMMY_CARD_TYPE.ordinal();
    }
}
