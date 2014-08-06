package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.VitalDetailActivity;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Random;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/10/2014.
 */
public class VitalCard extends BaseCard {
    private static final String TAG = VitalCard.class.getSimpleName();
    private View mChart;

    public VitalCard(Context context) {
        this(context, R.layout.vital_card_inner_content);
    }

    public VitalCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public VitalCard(Context context, int innerLayout, CardHeader cardHeader){
        super(context, innerLayout, cardHeader);
    }

    public VitalCard(Context context, CardHeader cardHeader) {
        this(context, R.layout.vital_card_inner_content, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        EditText editText1 = (EditText)view.findViewById(R.id.editText1);
        EditText editText2 = (EditText)view.findViewById(R.id.editText2);
        openChart(view);
        Random r = new Random();
        int n = r.nextInt() % 100;
        n = n>0?n:-n;
        setupCardFooter(view, "" + n);
        setListnerToCard();
    }

    private void setListnerToCard(){
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
//                Toast.makeText(getContext(), "Card clicked: " + card.getId(), Toast.LENGTH_LONG).show();
                Log.d("postInitCard", "Card clicked " + card.getId());
                Context context = getContext();
                Intent intent = new Intent(context, VitalDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getType() {
        return CardType.DUMMY_CARD_TYPE.ordinal();
    }

    private void openChart(View view){
        int[] x = { 1,2,3,4,5,6,7,8,9,10 };
        int[] vitals = { 130,150,110,170,170,140,140,155,155,145};

        XYSeries vitalSeries = new XYSeries("Vital");
        for(int i=0;i<x.length;i++){
            vitalSeries.add(x[i], vitals[i]);
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(vitalSeries);

        XYSeriesRenderer vitalRenderer = new XYSeriesRenderer();
        vitalRenderer.setColor(Color.WHITE);
        vitalRenderer.setLineWidth(3);

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.parseColor("#00010101"));
        multiRenderer.setMarginsColor(Color.parseColor("#00010101"));
        multiRenderer.setMargins(new int[]{5,2,-15,0});
        multiRenderer.setShowLegend(false);
        multiRenderer.setShowAxes(false);
        multiRenderer.setXLabels(0);
        multiRenderer.setYLabels(0);
        multiRenderer.setZoomEnabled(false, false);
        multiRenderer.setPanEnabled(false, false);
        multiRenderer.addSeriesRenderer(vitalRenderer);

        LinearLayout chartContainer = (LinearLayout) view.findViewById(R.id.vitalLineGraph);
        mChart = ChartFactory.getLineChartView(getContext(), dataset, multiRenderer);
        chartContainer.addView(mChart);
    }
}
