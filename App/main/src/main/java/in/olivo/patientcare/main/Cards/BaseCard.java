package in.olivo.patientcare.main.Cards;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.BaseTask;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Utils;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/13/2014.
 */
public abstract class BaseCard extends Card {
    private static final String TAG = BaseCard.class.getSimpleName();
    protected boolean isSwipable = true;
    protected boolean showFooter = true;
    protected BaseTask task;
    String primaryActionButtonText;
    String secondaryActionButtonText;
    View.OnClickListener primaryActionButtonClickListener;
    View.OnClickListener secondaryActionButtonClickListener;

    public BaseCard(Context context, int layout) {
        super(context, layout);
        setupCard(new CardHeaderInnerView(getContext()));
    }

    public BaseCard(Context context, int layout, CardHeader header) {
        super(context, layout);
        setupCard(header);
    }

    private void setupCard(CardHeader header) {
        if (header != null) {
            addCardHeader(header);
        }
        if (isSwipable) {
            setSwipeable(true);
        }
        this.setBackgroundResourceId(R.drawable.card_background_gray);
        this.setupUndoListener();
    }

    public void setupCardFooter(View view, String targetPoint) {
        TextView targetPointTextView = (TextView) view.findViewById(R.id.targetPoint);
        targetPointTextView.setText(targetPoint);

        ImageView imageView = (ImageView) view.findViewById(R.id.influencerImage);
        imageView.setImageResource(R.drawable.profile_dummy);
    }

    public void setupCardFooter(View view, BaseTask task) {
        BaseViewHolder viewHolder;
        if (view.getTag() == null) {
            viewHolder = new BaseViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.targetPoint);
            viewHolder.actionButtonPrimary = (TextView) view.findViewById(R.id.actionButtonPrimary);
            viewHolder.actionButtonSecondary = (TextView) view.findViewById(R.id.actionButtonSecondary);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.influencerImage);
            viewHolder.footerSet = true;
            view.setTag(viewHolder);
        } else {
            viewHolder = (BaseViewHolder) view.getTag();
            if (!viewHolder.footerSet) {
                viewHolder.textView = (TextView) view.findViewById(R.id.targetPoint);
                viewHolder.actionButtonPrimary = (TextView) view.findViewById(R.id.actionButtonPrimary);
                viewHolder.actionButtonSecondary = (TextView) view.findViewById(R.id.actionButtonSecondary);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.influencerImage);
                viewHolder.footerSet = true;
            }
        }

        TextView targetPointTextView = viewHolder.textView;
        TextView actionButtonPrimary = viewHolder.actionButtonPrimary;
        TextView actionButtonSecondary = viewHolder.actionButtonSecondary;
        ImageView influencerImage = viewHolder.imageView;
        if (showFooter) {
            targetPointTextView.setText("" + task.getPoints());
            if (task.getSource() != null) {
                Picasso.with(getContext())
                        .load(Utils.getFullURL(Constants.SERVER_URL, task.getSource()
                                .getProfilePicUrl()))
                                .into(influencerImage);
            }
            if (primaryActionButtonClickListener != null) {
                actionButtonPrimary.setVisibility(View.VISIBLE);
                actionButtonPrimary.setText(this.primaryActionButtonText);
                actionButtonPrimary.setOnClickListener(primaryActionButtonClickListener);
            }
            if (secondaryActionButtonClickListener != null) {
                actionButtonSecondary.setVisibility(View.VISIBLE);
                actionButtonSecondary.setText(this.secondaryActionButtonText);
                actionButtonSecondary.setOnClickListener(secondaryActionButtonClickListener);
            }
        } else {
            targetPointTextView.setVisibility(View.GONE);
            actionButtonPrimary.setVisibility(View.GONE);
            actionButtonSecondary.setVisibility(View.GONE);
            influencerImage.setVisibility(View.GONE);
        }
    }

    public void setupUndoListener() {
        this.setOnUndoHideSwipeListListener(new OnUndoHideSwipeListListener() {
            @Override
            public void onUndoHideSwipe(Card card) {
                dismissCard();
            }
        });
    }

    public void setListenerToActionButtons(String primaryActionText, View.OnClickListener primaryClickListener) {
        this.primaryActionButtonText = primaryActionText;
        this.primaryActionButtonClickListener = primaryClickListener;
        setListenerToActionButtons(primaryActionText, primaryActionButtonClickListener, null, null);
    }

    public void setListenerToActionButtons(String primaryActionText, View.OnClickListener primaryClickListener, String secondaryActionText, View.OnClickListener secondaryClickListener) {
        this.primaryActionButtonText = primaryActionText;
        this.primaryActionButtonClickListener = primaryClickListener;
        this.secondaryActionButtonText = secondaryActionText;
        this.secondaryActionButtonClickListener = secondaryClickListener;
    }

    protected void updateCardAsSeen() {
        if (this.task.getState() != BaseTask.CardState.SEEN) {
            this.task.setState(BaseTask.CardState.SEEN);
            String cardId = task.getCardId();
            try {
                JSONObject data = new JSONObject();
                data.put("state", task.getState().toString());
                UpdateTask(cardId, data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public abstract int getType();

    public void UpdateTask() {

    }

    public void UpdateTask(String cardId, JSONObject data) {
        new UpdateTasks().execute(Constants.CARDS_URL + cardId + "/", data);
    }

    public void dismissCard() {
        task.setState(BaseTask.CardState.DMSD);
        String cardId = task.getCardId();
        try {
            JSONObject data = new JSONObject();
            data.put("state", task.getState().toString());
            UpdateTask(cardId, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Maintain Order. Used in sorting of cards.
     */
    public enum CardType {
        DUMMY_CARD_TYPE,
        MEDICINE_CARD_TYPE,
        VITAL_CARD_TYPE,
        FOLLOWUP_CARD_TYPE,
        MESSAGE_CARD_TYPE,
        EDUCATION_CARD_TYPE,
    }

    protected static class BaseViewHolder {
        TextView textView;
        TextView actionButtonPrimary;
        TextView actionButtonSecondary;
        ImageView imageView;
        boolean footerSet = false;
    }

    private class UpdateTasks extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... arg0) {
            String url = (String) arg0[0];
            JSONObject data = (JSONObject) arg0[1];

            HTTPServiceHandler serviceHandler = new HTTPServiceHandler(getContext());
            serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.PATCH, null, data);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
