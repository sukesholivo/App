package com.doctl.patientcare.main.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doctl.patientcare.main.Cards.BaseCard;
import com.doctl.patientcare.main.R;

import java.util.UUID;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Administrator on 6/10/2014.
 */
public abstract class BaseFragment extends Fragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    protected abstract void initCard();

    public void postInitCard(BaseCard card){
        card.setSwipeable(true);
        card.setId(UUID.randomUUID().toString());

        boolean clickable =  card.isClickable();

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
//                Toast.makeText(getActivity(), "Card clicked: " + card.getId(), Toast.LENGTH_LONG).show();
                Log.d("postInitCard","Card clicked " + card.getId());
            }
        });

        CardView cardView = (CardView) getActivity().findViewById(R.id.card_layout);
        cardView.setCard(card);
    }

}
