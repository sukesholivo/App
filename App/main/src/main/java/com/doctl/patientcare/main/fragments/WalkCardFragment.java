package com.doctl.patientcare.main.fragments;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.doctl.patientcare.main.Cards.WalkCard;
import com.doctl.patientcare.main.R;

import java.util.UUID;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Administrator on 6/18/2014.
 */
public class WalkCardFragment extends BaseFragment {
    private static final String TAG = WalkCardFragment.class.getSimpleName();
    private CardView cardView;

    @Override
    protected void initCard() {
        WalkCard card= new WalkCard(getActivity());
        postInitCard(card);
    }
}
