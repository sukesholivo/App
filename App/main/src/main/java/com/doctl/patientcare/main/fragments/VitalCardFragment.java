package com.doctl.patientcare.main.fragments;

import com.doctl.patientcare.main.Cards.VitalCard;
import com.doctl.patientcare.main.R;

import java.io.File;

import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Administrator on 6/10/2014.
 */
public class VitalCardFragment extends BaseFragment {
    private CardView cardView;

    @Override
    protected void initCard() {
        //Create a Card
        VitalCard card= new VitalCard(getActivity());
        postInitCard(card);
    }
}
