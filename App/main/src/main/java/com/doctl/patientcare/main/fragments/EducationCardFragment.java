package com.doctl.patientcare.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doctl.patientcare.main.Cards.EducationCard;
import com.doctl.patientcare.main.Cards.MedicineCard;
import com.doctl.patientcare.main.R;

import it.gmariotti.cardslib.library.view.CardView;

public class EducationCardFragment extends BaseFragment{
    private CardView cardView;

    @Override
    protected void initCard() {
        //Create a Card
        EducationCard card= new EducationCard(getActivity());
        postInitCard(card);
    }
}
