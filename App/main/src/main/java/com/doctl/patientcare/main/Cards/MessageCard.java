package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.message.MessageTask;
import com.doctl.patientcare.main.utility.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 12/19/2014.
 */
public class MessageCard extends BaseCard {

    private static final String TAG = MedicineCard.class.getSimpleName();

    public MessageCard(Context context) {
        this(context, R.layout.card_inner_content_message);
    }

    public MessageCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public MessageCard(Context context, int innerLayout, CardHeader cardHeader, MessageTask messageTask){
        super(context, innerLayout, cardHeader);
        this.task = messageTask;
        this.setId(messageTask.getCardId());
    }

    public MessageCard(Context context, CardHeader cardHeader, MessageTask messageTask) {
        this(context, R.layout.card_inner_content_message, cardHeader, messageTask);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ViewHolder viewHolder;
        if(view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.messageTextView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        MessageTask messageTask = (MessageTask)task;
        viewHolder.textView.setText(Html.fromHtml(messageTask.getPayload().getMessage()));
        viewHolder.textView.setMovementMethod(LinkMovementMethod.getInstance());
        setupCardFooter(view, messageTask);
        updateCardAsSeen();
    }

    @Override
    public int getType() {
        return CardType.MESSAGE_CARD_TYPE.ordinal();
    }

    @Override
    public void UpdateTask(){
        JSONObject data;
        MessageTask messageTask = (MessageTask)task;
        messageTask.setState(BaseTask.CardState.DONE);
        String cardId = messageTask.getCardId();
        try {
            data = messageTask.getDataToPatch();
            Logger.d(TAG, data.toString());
            UpdateTask(cardId, data);
        }catch (JSONException e){
            Logger.e(TAG, e.toString());
        }
    }

    private static class ViewHolder extends BaseViewHolder {
        TextView textView;
    }
}
