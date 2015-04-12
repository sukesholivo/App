package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import java.io.File;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.utils.BitmapUtils;

/**
 * Created by Administrator on 6/18/2014.
 */
public class WalkCard extends BaseCard {
    private static final String TAG = WalkCard.class.getSimpleName();

    public WalkCard(Context context) {
        this(context, R.layout.card_inner_content_walk);
    }

    public WalkCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public WalkCard(Context context, int innerLayout, CardHeader cardHeader){
        super(context, innerLayout, cardHeader);
    }
    public WalkCard(Context context, CardHeader cardHeader) {
        this(context, R.layout.card_inner_content_walk, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        final TextView progressText = (TextView)view.findViewById(R.id.progressText);
        Button button = (Button)view.findViewById(R.id.walkStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setupCardFooter(view, "" + 10);
        setListnerToCard();
    }

    private void setListnerToCard() {
//        this.setOnLongClickListener(new OnLongCardClickListener() {
//            @Override
//            public boolean onLongClick(Card card, View view) {
//                Bitmap bitmap = card.getCardView().createBitmap();
//                File photofile= BitmapUtils.createFileFromBitmap(bitmap);
//                Intent shareIntent = BitmapUtils.createIntentFromImage(photofile);
//
//                getContext().startActivity(Intent.createChooser(shareIntent, "Share your Walk progress"));
//                return true;
//            }
//        });
    }

    @Override
    public int getType() {
        return CardType.DUMMY_CARD_TYPE.ordinal();
    }
}
