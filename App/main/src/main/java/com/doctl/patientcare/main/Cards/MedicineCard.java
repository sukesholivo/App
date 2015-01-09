package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.doctl.patientcare.main.MedicineDetailActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.medicines.Medicine;
import com.doctl.patientcare.main.om.medicines.MedicineAdapter;
import com.doctl.patientcare.main.om.medicines.MedicineTask;
import com.doctl.patientcare.main.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/12/2014.
 */
public class MedicineCard extends BaseCard {
    private static final String TAG = MedicineCard.class.getSimpleName();
    private MedicineTask medicineTask;
    private boolean addListener = true;
    private int state = Medicine.MedicineTakenState.getInteger(Medicine.MedicineTakenState.TAKEN);
    public MedicineCard(Context context) {
        this(context, R.layout.card_inner_content_medication);
    }

    public MedicineCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public MedicineCard(Context context, int innerLayout, CardHeader cardHeader, MedicineTask medicineTask){
        super(context, innerLayout, cardHeader);
        this.medicineTask = medicineTask;
        this.setId(medicineTask.getCardId());
    }

    public MedicineCard(Context context, CardHeader cardHeader, MedicineTask medicineTask) {
        this(context, R.layout.card_inner_content_medication, cardHeader, medicineTask);
    }

    public MedicineCard(Context context, CardHeader cardHeader, MedicineTask medicineTask,
                        boolean showFooter, boolean addListener, boolean isSwipable) {
        this(context, R.layout.card_inner_content_medication, cardHeader, medicineTask);
        this.showFooter = showFooter;
        this.addListener = addListener;
        this.isSwipable = isSwipable;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ViewHolder viewHolder;
        if(view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.medicationList);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        MedicineAdapter medicines = new MedicineAdapter(getContext(),medicineTask.getPayload().getMedicines());

        LinearLayout list =  viewHolder.linearLayout;
        list.removeAllViews();
        for (int i = 0; i < medicines.getCount(); i++) {
            View listView = medicines.getView(i, null, null);
            list.addView(listView);
        }
        setupCardFooter(view, medicineTask);

        if (addListener) {
            setListnerToCard();
        }
    }

    private void setListnerToCard() {
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Log.d("postInitCard", "Card clicked " + card.getId());
                Context context = getContext();
                Intent intent = new Intent(context, MedicineDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("prescriptionId", medicineTask.getPayload().getPrescriptionId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getType() {
        return CardType.MEDICINE_CARD_TYPE.ordinal();
    }

    @Override
    public void UpdateTask(){
        JSONObject data;
        ArrayList<Medicine> meds = medicineTask.getPayload().getMedicines();
        medicineTask.setState(BaseTask.CardState.DONE);
        String cardId = medicineTask.getCardId();
        for (Medicine med : meds){
            med.setTimestamp(Utils.getIsoDateString(new GregorianCalendar()));
            med.setState(state);
        }
        try {
            data = medicineTask.getDataToPatch();
            Log.d(TAG, data.toString());
            UpdateTask(cardId, data);
        }catch (JSONException e){
            Log.e(TAG, e.toString());
        }
    }

    public void setMedicineState (Medicine.MedicineTakenState state ) {
        this.state = Medicine.MedicineTakenState.getInteger(state);
    }

    private static class ViewHolder extends BaseViewHolder {
        LinearLayout linearLayout;
    }
 }
