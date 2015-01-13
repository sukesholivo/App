package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.VitalDetailActivity;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.GraphData;
import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.doctl.patientcare.main.om.vitals.Vitals;
import com.doctl.patientcare.main.utility.Logger;

import org.achartengine.GraphicalView;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/10/2014.
 */
public class VitalCard extends BaseCard {
    private static final String TAG = VitalCard.class.getSimpleName();

    private VitalTask vitalTask;
    private boolean addListener = true;

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

    public VitalCard(Context context, CardHeader cardHeader, VitalTask medicineTask,
                        boolean showFooter, boolean addListener, boolean isSwipable) {
        this(context, R.layout.card_inner_content_vital, cardHeader, medicineTask);
        this.showFooter = showFooter;
        this.addListener = addListener;
        this.isSwipable = isSwipable;
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

        final Vitals vitalData = vitalTask.getPayload().getVitals();
        LinearLayout list =  viewHolder.vitalLinearLayout;
        list.removeAllViews();

        View view1 = getView(list, vitalData.getName1(),
                vitalData.getCondition1(),
                vitalData.getUnit1(),
                vitalData.getValue1(),
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        Logger.d(TAG, charSequence.toString());
                        try {
                            Double val = Double.parseDouble(charSequence.toString());
                            vitalData.setValue1(val);
                        } catch (NumberFormatException e){
                            // Ignore
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
        list.addView(view1);
        View view2;
        if (vitalData.getName2() != null && !vitalData.getName2().isEmpty()){
            view2 = getView(list,
                    vitalData.getName2(),
                    vitalData.getCondition2(),
                    vitalData.getUnit2(),
                    vitalData.getValue2(),
                    new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            Logger.d(TAG, charSequence.toString());
                            try {
                                Double val = Double.parseDouble(charSequence.toString());
                                vitalData.setValue2(val);
                            } catch (NumberFormatException e){
                                // Ignore
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
            list.addView(view2);
        }

//        TODO: Fix graph.
        ArrayList<GraphData> graphList = new ArrayList<GraphData>();
//        for (Vitals vital : vitalData) {
//            GraphData graph = new GraphData(vital.getName(),
//                    vital.getPast().getTimeStamps(),
//                    vital.getPast().getValues(),
//                    Color.RED,3);
//            graphList.add(graph);
//        }

        GraphicalView graphicalView = null; //Utils.getGraph(getContext(), graphList);

        if (graphicalView != null) {
            LinearLayout chartContainer = viewHolder.graphLinearLayout;
            chartContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50));
            chartContainer.addView(graphicalView);
        }

        setupCardFooter(view, vitalTask);
        if (addListener) {
            setListnerToCard();
        }
    }

    private View getView(ViewGroup parent, String name, String condition, String unit, Double value, TextWatcher textWatcher){
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.vital_card_list_item, parent, false);
        TextView vitalTitle = (TextView)view.findViewById(R.id.vitalTitle);
        vitalTitle.setText(name);
        TextView vitalCondition = (TextView)view.findViewById(R.id.vitalCondition);
        vitalCondition.setText(condition);
        TextView vitalUnit = (TextView)view.findViewById(R.id.vitalUnit);
        vitalUnit.setText(unit);
        EditText vitalValue = (EditText)view.findViewById(R.id.vitalValue);
        if (value != null) {
            DecimalFormat df = new DecimalFormat("###.#");
            vitalValue.setText(df.format(value));
        }
        vitalValue.addTextChangedListener(textWatcher);
        return view;
    }

    private void setListnerToCard(){
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Logger.d("postInitCard", "Card clicked " + card.getId());
                Context context = getContext();
                Intent intent = new Intent(context, VitalDetailActivity.class);
                intent.putExtra("vitalId", vitalTask.getPayload().getVitalId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getType() {
        return CardType.VITAL_CARD_TYPE.ordinal();
    }

    @Override
    public void UpdateTask(){
        JSONObject data;
        vitalTask.setState(BaseTask.CardState.DONE);
        String cardId = vitalTask.getCardId();
        try {
            data = vitalTask.getDataToPatch();
            Logger.d(TAG, data.toString());
            UpdateTask(cardId, data);
        }catch (JSONException e){
            Logger.e(TAG, e.toString());
        }
    }

    private static class ViewHolder extends BaseViewHolder {
        LinearLayout vitalLinearLayout;
        LinearLayout graphLinearLayout;
    }
}
