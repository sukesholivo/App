package com.doctl.patientcare.main.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.TextView;

import com.doctl.patientcare.main.Cards.BaseCard;
import com.doctl.patientcare.main.Cards.CardHeaderInnerView;
import com.doctl.patientcare.main.Cards.DashboardCard;
import com.doctl.patientcare.main.Cards.EducationCard;
import com.doctl.patientcare.main.Cards.EducationRichtextCard;
import com.doctl.patientcare.main.Cards.FollowupCard;
import com.doctl.patientcare.main.Cards.HowruFeelingCard;
import com.doctl.patientcare.main.Cards.ImageCard;
import com.doctl.patientcare.main.Cards.MedicineCard;
import com.doctl.patientcare.main.Cards.VitalCard;
import com.doctl.patientcare.main.Cards.WalkCard;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.CustomCardArrayAdapter;
import com.doctl.patientcare.main.om.followup.FollowupTask;
import com.doctl.patientcare.main.om.medicines.MedicineTask;
import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Administrator on 6/13/2014.
 */
public class CardListFragment extends BaseFragment implements OnRefreshListener {
    private static final String TAG = CardListFragment.class.getSimpleName();
    private static String ANDROID_DEVELOPER_KEY = "AIzaSyAWocbee6JmNy1KShjdNWy_v8_xEq0-gE0";
    PullToRefreshLayout mPullToRefreshLayout;
    ArrayList<Card> cards;
    CustomCardArrayAdapter mCardArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, " OnCreateView: START");
        return inflater.inflate(R.layout.fragment_card_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeCardList();
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetTasks().execute();
    }

    protected void initializeCardList() {
        cards = new ArrayList<Card>();
        DashboardCard card0 = new DashboardCard(getActivity(), null);
        card0.setShadow(false);
        card0.setSwipeable(false);
        cards.add(card0);

        mCardArrayAdapter = new CustomCardArrayAdapter(getActivity(), cards);

        mCardArrayAdapter.setEnableUndo(true);
        CardListView listView = (CardListView) getActivity().findViewById(R.id.card_list_layout);
        listView.setOnScrollListener(
                new SwipeOnScrollListener() {
                    int mLastFirstVisibleItem = -1;
                    int mLastVisibleItemCount = -1;
                    boolean isDashboardHidden = false;

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        //It is very important to call the super method here to preserve built-in functions
                        super.onScrollStateChanged(view,scrollState);
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if(mLastFirstVisibleItem >=0 && mLastVisibleItemCount >=0) {
                            if (mLastFirstVisibleItem > firstVisibleItem) {
                                Log.i(TAG, "scrolling up");
                                DashboardAppear(firstVisibleItem);
                            } else if (mLastFirstVisibleItem < firstVisibleItem) {
                                Log.i(TAG, "scrolling down");
                                DashboardDiasppear();
                            } else if (mLastVisibleItemCount < visibleItemCount) {
                                Log.i(TAG, "scrolling down");
                                //DashboardDiasppear();
                            } else if (mLastVisibleItemCount > visibleItemCount) {
                                Log.i(TAG, "scrolling up");
                                DashboardAppear(firstVisibleItem);
                            }
                        }
                        mLastFirstVisibleItem = firstVisibleItem;
                        mLastVisibleItemCount = visibleItemCount;
                    }

                    private void DashboardAppear(int firstVisibleItem) {
                        if(!isDashboardHidden)
                            return;
                        if(firstVisibleItem == 0) {
                            Activity activity = getActivity();
                            View dashboard = activity.findViewById(R.id.treatment_dashboard_layout);
                            Animation slide_down = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.dashboard_slide_in);
                            dashboard.startAnimation(slide_down);
                            dashboard.setVisibility(View.VISIBLE);
                            isDashboardHidden = false;
                        }
                    }

                    private void DashboardDiasppear() {
                        if(isDashboardHidden)
                            return;
                        Activity activity = getActivity();
                        View dashboard = activity.findViewById(R.id.treatment_dashboard_layout);
                        Animation slide_up = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.dashboard_slide_out);
                        dashboard.startAnimation(slide_up);
                        dashboard.setVisibility(View.GONE);
                        isDashboardHidden = true;
                    }
                });

        AnimationAdapter animCardArrayAdapter = new ScaleInAnimationAdapter(mCardArrayAdapter);
        animCardArrayAdapter.setAbsListView(listView);
        listView.setExternalAdapter(animCardArrayAdapter, mCardArrayAdapter);

        mPullToRefreshLayout = (PullToRefreshLayout) getActivity().findViewById(R.id.carddemo_extra_ptr_layout);

        ActionBarPullToRefresh.from(this.getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);
    }

    protected void refreshCard() {
        String data = downloadCardData();
        ArrayList<BaseCard> cards = parseCardData(data);
        resetCardList(cards);
    }

    private String downloadCardData(){
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(getActivity());
        return serviceHandler.makeServiceCall(Constants.CARDS_URL, HTTPServiceHandler.HTTPMethod.GET, Utils.getCardsHTTPGetQueryParam(), null);
    }

    private ArrayList<BaseCard> parseCardData(String jsonStr){
        ArrayList<BaseCard> cards = new ArrayList<BaseCard>();
        DashboardCard card0 = new DashboardCard(getActivity(), null);
        card0.setShadow(false);
        card0.setSwipeable(false);
        cards.add(card0);
        Log.d(TAG, jsonStr);
        JsonParser parser = new JsonParser();
        JsonArray cardsJsonArray = parser.parse(jsonStr).getAsJsonArray();
//        JsonArray cardsJsonArray = jsonObject.get("cards").getAsJsonArray();
        for (JsonElement cardJson : cardsJsonArray) {
            JsonObject cardJsonObj = cardJson.getAsJsonObject();
            switch (BaseTask.CardType.valueOf(cardJsonObj.get("type").getAsString().toUpperCase())) {
                case MEDICINE:
                    MedicineTask medicineTask = new Gson().fromJson(cardJson, MedicineTask.class);
                    CardHeader medicineHeader = Utils.getCardHeader(getActivity(), medicineTask);
                    MedicineCard medicineCard = new MedicineCard(getActivity(), medicineHeader, medicineTask);
                    cards.add(medicineCard);
                    break;
                case VITAL:
                    VitalTask vitalTask = new Gson().fromJson(cardJson, VitalTask.class);
                    CardHeader vitalHeader = Utils.getCardHeader(getActivity(), vitalTask);
                    VitalCard vitalCard = new VitalCard(getActivity(), vitalHeader, vitalTask);
                    cards.add(vitalCard);
                    break;
                case FOLLOWUP:
                    FollowupTask followupTask = new Gson().fromJson(cardJson, FollowupTask.class);
                    CardHeader followupHeader = Utils.getCardHeader(getActivity(), followupTask);
                    FollowupCard followupCard = new FollowupCard(getActivity(), followupHeader, followupTask);
                    cards.add(followupCard);
                    break;
            }
        }
        CardHeader educationHeader = new CardHeaderInnerView(getActivity()){
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
                textView1.setText("WHAT IS DIABETES?");
                textView1.setTextSize(28);
            }
        };

        EducationCard card2 = new EducationCard(getActivity(), educationHeader);

        CardHeader walkHeader = new CardHeaderInnerView(getActivity(), "TODAY", "", "");
        WalkCard card4 = new WalkCard(getActivity(), walkHeader);

        CardHeader richEducationHeader = new CardHeaderInnerView(getActivity()){
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
                textView1.setText("HOW TO INJECT INSULIN");
                textView1.setTextSize(28);
            }
        };
        //, "HOW TO INJECT INSULIN", "", "");
        EducationRichtextCard card5 = new EducationRichtextCard(getActivity(), richEducationHeader );

        CardHeader howruFeelingHeader = new CardHeaderInnerView(getActivity(), "HOW ARE YOU FEELING?", "", "");
        HowruFeelingCard card7 = new HowruFeelingCard(getActivity(), howruFeelingHeader);

        CardHeader mythHeader = new CardHeaderInnerView(getActivity()){
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
                textView1.setText("MYTH");
                textView1.setTextSize(25);
                textView1.setTextColor(Color.RED);
            }
        };

        ImageCard card9 = new ImageCard(getActivity(), null);
        card9.setImageResourceId(R.drawable.motivationcard_backgroundpic4_lesssugar);

        ImageCard card10 = new ImageCard(getActivity(), null);
        card10.setImageResourceId(R.drawable.education_myths_sugar_full);

        ImageCard card11 = new ImageCard(getActivity(), null);
        card11.setImageResourceId(R.drawable.education_myths_weightloss_full);

        cards.add(card2);
        cards.add(card4);
        cards.add(card5);
        cards.add(card7);
        cards.add(card9);
        cards.add(card10);
        cards.add(card11);
        return cards;
    }

    private void resetCardList(final ArrayList<BaseCard> cardArrayList){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mCardArrayAdapter == null){
                    Log.d(TAG, "mCardArrayAdapter is null");
                }
                mCardArrayAdapter.clear();
                mCardArrayAdapter.addAll(cardArrayList);
                mCardArrayAdapter.setEnableUndo(true);
                mCardArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRefreshStarted(View view) {
        new GetTasks().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPullToRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            refreshCard();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mPullToRefreshLayout.setRefreshing(false);
        }
    }
}
