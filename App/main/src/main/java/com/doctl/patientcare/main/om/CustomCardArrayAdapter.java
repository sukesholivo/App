package com.doctl.patientcare.main.om;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import com.doctl.patientcare.main.Cards.BaseCard;
import com.doctl.patientcare.main.R;

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

    @Override
    protected void setupSwipeableAnimation(final Card card, CardView cardView) {
        if (card.isSwipeable()){
            if (mOnTouchListener == null){
                mOnTouchListener = new SwipeDismissListViewTouchListener(mCardListView, mCallback);
                if (mCardListView.getOnScrollListener() == null){
                    SwipeOnScrollListener scrollListener = new SwipeOnScrollListener();
                    scrollListener.setTouchListener(mOnTouchListener);
                    mCardListView.setOnScrollListener(scrollListener);
                }else{
                    AbsListView.OnScrollListener onScrollListener=mCardListView.getOnScrollListener();
                    if (onScrollListener instanceof SwipeOnScrollListener)
                        ((SwipeOnScrollListener) onScrollListener).setTouchListener(mOnTouchListener);

                }
                mCardListView.setOnTouchListener(mOnTouchListener);
            }
            cardView.setOnTouchListener(mOnTouchListener);
        }else{
            cardView.setOnTouchListener(null);
        }
    }

    private final SwipeDismissListViewTouchListener.DismissCallbacks mCallback = new SwipeDismissListViewTouchListener.DismissCallbacks() {

        @Override
        public boolean canDismiss(int position, Card card) {
            return card.isSwipeable();
        }

        @Override
        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
            int[] itemPositions=new int[reverseSortedPositions.length];
            String[] itemIds=new String[reverseSortedPositions.length];
            int i=0;
            for (int position : reverseSortedPositions) {
                Card card = getItem(position);
                dismissedCard = card;
                itemPositions[i]=position;
                itemIds[i]=card.getId();
                i++;

                remove(card);
                if (card.getOnSwipeListener() != null){
                    card.getOnSwipeListener().onSwipe(card);
                }
            }
            notifyDataSetChanged();

            UndoCard itemUndo=new UndoCard(itemPositions,itemIds);
            if (isEnableUndo() && mUndoBarController!=null){
                getAlertDialog(dismissedCard, itemUndo).show();
            }
        }
    };

    private AlertDialog.Builder getAlertDialog(final Card card, final UndoCard undoCard){
        String dismissDialogTitle = "CARD DISMISSED";
        String dismissMessage;
        String undoMessage = "UNDO";
        String sendMessage = "SEND";
        BaseCard.CardType type = BaseCard.CardType.values()[card.getType()];
        switch(type){
            case MEDICINE_CARD_TYPE:
                dismissMessage = getContext().getResources().getString(R.string.medicine_card_remove_message);
                break;
            case VITAL_CARD_TYPE:
                dismissMessage = getContext().getResources().getString(R.string.vital_card_remove_message);
                break;
            case FOLLOWUP_CARD_TYPE:
                dismissMessage = getContext().getResources().getString(R.string.followup_card_remove_message);
                break;
            default:
                dismissMessage = getContext().getResources().getString(R.string.default_card_remove_message);
                break;
        }

        return new AlertDialog.Builder(getContext())
                .setTitle(dismissDialogTitle)
                .setIcon(0)
                .setCancelable(false)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        onUndo(undoCard);
                    }
                })
                .setMessage(dismissMessage)
                .setPositiveButton(undoMessage, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onUndo(undoCard);
                        Log.d(TAG, "UNDO CLICKED");
                    }
                })
                .setNegativeButton(sendMessage, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "OK CLICKED");
                    }
                });
    }

}
