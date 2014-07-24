package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.doctl.patientcare.main.R;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

import static android.support.v4.app.ActivityCompat.startActivity;

public class EducationCard extends BaseCard{
    private static final String TAG = EducationCard.class.getSimpleName();

    public EducationCard(Context context)
    {
        this(context, R.layout.education_video_card_inner_content);
    }

    public EducationCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public EducationCard(Context context, int innerLayout, CardHeader cardHeader){
        super(context, innerLayout, cardHeader);
    }

    public EducationCard(Context context, CardHeader cardHeader)
    {
        this(context, R.layout.education_video_card_inner_content, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
//        Log.d(TAG, "Setting up inner view element of card.");
        Random r = new Random();
        int n = r.nextInt() % 100;
        n = n>0?n:-n;
        setupCardFooter(view, "" + n);
    }

    @Override
    public int getType() {
        return EDUCATION_CARD_TYPE;
    }

}
