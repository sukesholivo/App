package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import java.io.File;
import java.util.Random;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.utils.BitmapUtils;

/**
 * Created by Administrator on 6/18/2014.
 */
public class WalkCard extends BaseCard {
    private static final String TAG = WalkCard.class.getSimpleName();
    private int walkTargetKms = 21;
    private int walkCompletedKms = 15;

    public WalkCard(Context context) {
        this(context, R.layout.walk_card_inner_content);
    }

    public WalkCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public WalkCard(Context context, int innerLayout, CardHeader cardHeader){
        super(context, innerLayout, cardHeader);
    }
    public WalkCard(Context context, CardHeader cardHeader) {
        this(context, R.layout.walk_card_inner_content, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView walkMain = (TextView)view.findViewById(R.id.walkMain);
        walkMain.setText("WALK 45 MINS (3 KM)");

        TextView walkCompleted = (TextView)view.findViewById(R.id.walkCompleted);
        walkCompleted.setText("COMPLETED THIS WEEK ("+ walkCompletedKms +" KM)");

        ProgressBar walkProgress = (ProgressBar)view.findViewById(R.id.walkProgress);
        walkProgress.setMax(walkTargetKms);
        walkProgress.setProgress(walkCompletedKms);

        TextView walkTarget = (TextView)view.findViewById(R.id.walkTarget);
        walkTarget.setText("TARGET : "+ walkTargetKms +" KM");

        Random r = new Random();
        int n = r.nextInt() % 100;
        n = n>0?n:-n;
        setupCardFooter(view, "" + n);
        setListnerToCard();
    }

    private void setListnerToCard() {
        this.setOnLongClickListener(new OnLongCardClickListener() {
            @Override
            public boolean onLongClick(Card card, View view) {
                Bitmap bitmap = card.getCardView().createBitmap();
                File photofile= BitmapUtils.createFileFromBitmap(bitmap);
                Intent shareIntent = BitmapUtils.createIntentFromImage(photofile);

                getContext().startActivity(Intent.createChooser(shareIntent, "Share your Walk progress"));
                return true;
            }
        });
    }
}
