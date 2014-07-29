package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.doctl.patientcare.main.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

public class DashboardCard extends BaseCard{
    private static final String TAG = EducationCard.class.getSimpleName();

    public DashboardCard(Context context)
    {
        this(context, R.layout.dummy_dashboard_stub_card);
    }

    public DashboardCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public DashboardCard(Context context, int innerLayout, CardHeader cardHeader){
        super(context, innerLayout, cardHeader);
    }

    public DashboardCard(Context context, CardHeader cardHeader)
    {
        this(context, R.layout.dummy_dashboard_stub_card, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

    }

}
