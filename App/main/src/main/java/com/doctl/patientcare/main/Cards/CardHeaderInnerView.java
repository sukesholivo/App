package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doctl.patientcare.main.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/10/2014.
 */
public class CardHeaderInnerView extends CardHeader{
    private final String timeWhen;
    private final String timeUnit;
    private final String extraInfo;

    public CardHeaderInnerView(Context context) {
        this(context, R.layout.card_header_inner_content);
    }

    public CardHeaderInnerView(Context context, int headerResource) {
        super(context, headerResource);
        this.timeWhen= "NOW";
        this.timeUnit = "";
        this.extraInfo = "";
    }

    public CardHeaderInnerView(Context context, String timeWhen, String timeUnit, String extraInfo) {
        super(context, R.layout.card_header_inner_content);
        this.timeWhen= timeWhen;
        this.timeUnit = timeUnit;
        this.extraInfo = extraInfo;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
        textView1.setText(timeWhen);

        TextView textView2 = (TextView)view.findViewById(R.id.timeUnit);
        textView2.setText(timeUnit);

        TextView textView3 = (TextView)view.findViewById(R.id.extraInfo);
        textView3.setText(extraInfo);
    }
}
