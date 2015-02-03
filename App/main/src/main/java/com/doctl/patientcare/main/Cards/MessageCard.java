package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

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
    private MessageTask messageTask;

    public MessageCard(Context context) {
        this(context, R.layout.card_inner_content_message);
    }

    public MessageCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public MessageCard(Context context, int innerLayout, CardHeader cardHeader, MessageTask messageTask){
        super(context, innerLayout, cardHeader);
        this.messageTask = messageTask;
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
            viewHolder.webView = (WebView) view.findViewById(R.id.messageTextView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.webView.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        viewHolder.webView.loadDataWithBaseURL(null, messageTask.getPayload().getMessage(), "text/html", "utf-8", null);
    }

    @Override
    public int getType() {
        return CardType.MESSAGE_CARD_TYPE.ordinal();
    }

    @Override
    public void UpdateTask(){
        JSONObject data;

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
        WebView webView;
    }
}
