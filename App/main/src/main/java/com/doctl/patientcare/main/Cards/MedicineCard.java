package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.doctl.patientcare.main.MedicineDetailActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.medicines.Medicine;
import com.doctl.patientcare.main.om.medicines.MedicineAdapter;
import com.doctl.patientcare.main.om.medicines.MedicineTask;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/12/2014.
 */
public class MedicineCard extends BaseCard {

    private static final String TAG = MedicineCard.class.getSimpleName();
    private MedicineTask medicineTask;

    public MedicineCard(Context context) {
        this(context, R.layout.medication_card_inner_content);
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
        this(context, R.layout.medication_card_inner_content, cardHeader, medicineTask);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        MedicineAdapter medicines = new MedicineAdapter(getContext(), medicineTask.getPayload().getMedicines());
        ListView list = (ListView)view.findViewById(R.id.medicationList);
        list.setAdapter(medicines);

        setupCardFooter(view, medicineTask);
        setListnerToCard();
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

    public ArrayList<Medicine> buildArrayHelper() {
        Medicine m1 = new Medicine("", "Metformin", 1, Medicine.MedicineType.CAPSULE, "", "500mg", "", false);
        Medicine m2 = new Medicine("", "Sulfonylureas", 2, Medicine.MedicineType.TABLET, "", "200mg", "", false);
        Medicine m3 = new Medicine("", "Humalog", 1, Medicine.MedicineType.INJECTION, "", "20ml", "", false);
        ArrayList<Medicine> list = new ArrayList<Medicine>();
        list.add(m1);
        list.add(m2);
        list.add(m3);
        return list;
    }
 }
