package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.VitalDetailActivity;
import com.doctl.patientcare.main.om.GraphData;
import com.doctl.patientcare.main.om.vitals.VitalAdapter;
import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.doctl.patientcare.main.om.vitals.Vitals;
import com.doctl.patientcare.main.utility.Utils;

import org.achartengine.GraphicalView;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/10/2014.
 */
public class VitalCard extends BaseCard {
    private static final String TAG = VitalCard.class.getSimpleName();

    private VitalTask vitalTask;
    public VitalCard(Context context) {
        this(context, R.layout.card_inner_content_vital);
    }

    public VitalCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public VitalCard(Context context, int innerLayout, CardHeader cardHeader, VitalTask vitalTask){
        super(context, innerLayout, cardHeader);
        this.vitalTask = vitalTask;
        this.setId(vitalTask.getCardId());
    }

    public VitalCard(Context context, CardHeader cardHeader, VitalTask vitalTask) {
        this(context, R.layout.card_inner_content_vital, cardHeader, vitalTask);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ViewHolder viewHolder;
        if(view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.vitalLinearLayout = (LinearLayout) view.findViewById(R.id.vitalList);
            viewHolder.graphLinearLayout = (LinearLayout) view.findViewById(R.id.vitalLineGraph);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ArrayList<Vitals> vitalData = vitalTask.getPayload().getVitals();
        VitalAdapter vitals = new VitalAdapter(getContext(), vitalData);
        LinearLayout list =  viewHolder.vitalLinearLayout;

        list.removeAllViews();
        for (int i = 0; i < vitals.getCount(); i++) {
            View listView = vitals.getView(i, null, null);
            list.addView(listView);
        }

        ArrayList<GraphData> graphList = new ArrayList<GraphData>();
        for (Vitals vital : vitalData) {
            GraphData graph = new GraphData(vital.getName(),
                    vital.getPast().getTimeStamps(),
                    vital.getPast().getValues(),
                    Color.RED,3);
            graphList.add(graph);
        }

        GraphicalView graphicalView = Utils.getGraph(getContext(), graphList);
        LinearLayout chartContainer = viewHolder.graphLinearLayout;
        chartContainer.addView(graphicalView);

        setupCardFooter(view, vitalTask);
        setListnerToCard();
    }

    private void setListnerToCard(){
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Log.d("postInitCard", "Card clicked " + card.getId());
                Context context = getContext();
                Intent intent = new Intent(context, VitalDetailActivity.class);
                intent.putExtra("vitalId", vitalTask.getPayload().getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getType() {
        return CardType.VITAL_CARD_TYPE.ordinal();
    }

    private static class ViewHolder extends BaseViewHolder {
        LinearLayout vitalLinearLayout;
        LinearLayout graphLinearLayout;
    }
}
