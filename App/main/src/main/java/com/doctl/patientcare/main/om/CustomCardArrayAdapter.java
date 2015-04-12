package com.doctl.patientcare.main.om;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.AbsListView;
import android.widget.AbsListView;
import android.widget.ListView;

import com.doctl.patientcare.main.Cards.BaseCard;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.utility.Logger;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.listener.SwipeDismissListViewTouchListener;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;
import it.gmariotti.cardslib.library.view.listener.UndoCard;

/**
 * Created by Administrator on 8/5/2014.
 */
public class CustomCardArrayAdapter extends CardArrayAdapter {

    private Card dismissedCard;
    public CustomCardArrayAdapter(Context context, List<Card> cards) {
        super(context, cards);
    }
}
