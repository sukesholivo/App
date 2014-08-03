package com.doctl.patientcare.main.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;

import com.doctl.patientcare.main.Cards.BaseCard;
import com.doctl.patientcare.main.Cards.DashboardCard;
import com.doctl.patientcare.main.Cards.MedicineCard;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.constants.Utils;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.medicines.MedicineTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;
import it.gmariotti.cardslib.library.view.listener.UndoBarController;
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
    public static final int SIMULATED_REFRESH_LENGTH = 1;
    ArrayList<Card> cards;
    CardArrayAdapter mCardArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, " OnCreateView: START");
        return inflater.inflate(R.layout.fragment_card_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeCardList();
        new GetTasks().execute();
    }

    protected void initCard(){
    }

    protected void initializeCardList() {
        cards = new ArrayList<Card>();
        DashboardCard card0 = new DashboardCard(getActivity(), null);
        card0.setShadow(false);
        card0.setSwipeable(false);
        cards.add(card0);

        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        mCardArrayAdapter.setUndoBarUIElements(new UndoBarController.DefaultUndoBarUIElements(){
            @Override
            public String getMessageUndo(CardArrayAdapter cardArrayAdapter, String[] itemIds, int[] itemPositions) {
                StringBuffer message=new StringBuffer();
                for (int id:itemPositions){
                    Card card = cards.get(id);
                    switch(card.getType()){
                        case BaseCard.MEDICINE_CARD_TYPE:
                            message.append(getResources().getString(R.string.medicine_card_remove_meassage));
                            break;
                        default:
                            message.append("one card removed");
                            break;
                    }
                }
                return message.toString();
            }
        });
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
        InputStream inputStream = getResources().openRawResource(R.raw.tasks);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
        //convert to object

//        cards = new ArrayList<Card>();
//
//        DashboardCard card0 = new DashboardCard(getActivity(), null);
//        setListnerToCard(card0);
//        card0.setShadow(false);
//        card0.setSwipeable(false);
//
//        CardHeader medicineHeader = new CardHeaderInnerView(getActivity(), "30", "MINS FROM NOW", "BEFORE MEALS");
//        MedicineCard card1 = new MedicineCard(getActivity(), medicineHeader, null);
//        setListnerToCard(card1);
//
//        CardHeader educationHeader = new CardHeaderInnerView(getActivity()){
//            @Override
//            public void setupInnerViewElements(ViewGroup parent, View view) {
//                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
//                textView1.setText("WHAT IS DIABETES?");
//                textView1.setTextSize(28);
//            }
//        };
//
//        EducationCard card2 = new EducationCard(getActivity(), educationHeader);
//        setListnerToEducationCard(card2);
//
//        CardHeader vitalHeader = new CardHeaderInnerView(getActivity(), "NOW", "", "");
//        VitalCard card3 = new VitalCard(getActivity(), vitalHeader);
//        setListnerToCard(card3);
//
//        CardHeader walkHeader = new CardHeaderInnerView(getActivity(), "TODAY", "", "");
//        WalkCard card4 = new WalkCard(getActivity(), walkHeader);
//        setListnerToCard(card4);
//
//        CardHeader richEducationHeader = new CardHeaderInnerView(getActivity()){
//            @Override
//            public void setupInnerViewElements(ViewGroup parent, View view) {
//                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
//                textView1.setText("HOW TO INJECT INSULIN");
//                textView1.setTextSize(28);
//            }
//        };
//        //, "HOW TO INJECT INSULIN", "", "");
//        EducationRichtextCard card5 = new EducationRichtextCard(getActivity(), richEducationHeader );
//        setListnerToCard(card5);
//
//        CardHeader questionHeader = new CardHeaderInnerView(getActivity()){
//            @Override
//            public void setupInnerViewElements(ViewGroup parent, View view) {
//                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
//                textView1.setText("DO YOU HAVE ANY OF THESE?");
//                textView1.setTextSize(28);
//            }
//        };
//        QuestionCard card6 = new QuestionCard(getActivity(), questionHeader);
//        setListnerToCard(card6);
//
//        CardHeader howruFeelingHeader = new CardHeaderInnerView(getActivity(), "HOW ARE YOU FEELING?", "", "");
//        HowruFeelingCard card7 = new HowruFeelingCard(getActivity(), howruFeelingHeader);
//        setListnerToCard(card7);
//
//        CardHeader mythHeader = new CardHeaderInnerView(getActivity()){
//            @Override
//            public void setupInnerViewElements(ViewGroup parent, View view) {
//                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
//                textView1.setText("MYTH");
//                textView1.setTextSize(25);
//                textView1.setTextColor(Color.RED);
//            }
//        };
//        MythCard card8 = new MythCard(getActivity(), mythHeader);
//        setListnerToCard(card8);
//
//        ImageCard card9 = new ImageCard(getActivity(), null);
//        card9.setImageResourceId(R.drawable.motivationcard_backgroundpic4_lesssugar);
//        setListnerToCard(card9);
//
//        ImageCard card10 = new ImageCard(getActivity(), null);
//        card10.setImageResourceId(R.drawable.education_myths_sugar_full);
//        setListnerToCard(card10);
//
//        ImageCard card11 = new ImageCard(getActivity(), null);
//        card11.setImageResourceId(R.drawable.education_myths_weightloss_full);
//        setListnerToCard(card11);
//
//        cards.add(card0);
//        cards.add(card1);
//        cards.add(card2);
//        cards.add(card3);
//        cards.add(card4);
//        cards.add(card5);
//        cards.add(card6);
//        cards.add(card7);
//        cards.add(card8);
//        cards.add(card9);
//        cards.add(card10);
//        cards.add(card11);
//        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        //Create card
    }

    private ArrayList<BaseCard> parseCardData(String jsonStr){
        ArrayList<BaseCard> cards = new ArrayList<BaseCard>();
        DashboardCard card0 = new DashboardCard(getActivity(), null);
        card0.setShadow(false);
        card0.setSwipeable(false);
        cards.add(card0);

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
        JsonArray cardsJsonArray = jsonObject.get("cards").getAsJsonArray();
        for (JsonElement cardJson : cardsJsonArray) {
            JsonObject cardJsonObj = cardJson.getAsJsonObject();
            switch (BaseTask.CardType.valueOf(cardJsonObj.get("type").getAsString())) {
                case MEDICINE:
                    MedicineTask medicineTask = new Gson().fromJson(cardJson, MedicineTask.class);
                    CardHeader medicineHeader = Utils.getCardHeader(getActivity(), medicineTask);
                    MedicineCard medicineCard = new MedicineCard(getActivity(), medicineHeader, medicineTask); // pass medicine object
                    cards.add(medicineCard);
                    Log.d(TAG, medicineTask.getCardId());
            }
        }
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
            try {
                Thread.sleep(SIMULATED_REFRESH_LENGTH);
                refreshCard();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mPullToRefreshLayout.setRefreshing(false);
        }
    }
}
