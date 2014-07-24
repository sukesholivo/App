package com.doctl.patientcare.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doctl.patientcare.main.Cards.MedicineCard;
import com.doctl.patientcare.main.Cards.VitalCard;
import com.doctl.patientcare.main.R;

import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Administrator on 6/12/2014.
 */
public class MedicineCardFragment extends BaseFragment {
    private CardView cardView;

    @Override
    protected void initCard() {
        //Create a Card
        MedicineCard card= new MedicineCard(getActivity());
        postInitCard(card);
    }
}
