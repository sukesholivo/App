package com.doctl.patientcare.main.fragments;

import android.app.Activity;
import android.content.Intent;
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
import com.doctl.patientcare.main.Cards.CardHeaderInnerView;
import com.doctl.patientcare.main.Cards.DashboardCard;
import com.doctl.patientcare.main.Cards.EducationCard;
import com.doctl.patientcare.main.Cards.EducationRichtextCard;
import com.doctl.patientcare.main.Cards.HowruFeelingCard;
import com.doctl.patientcare.main.Cards.ImageCard;
import com.doctl.patientcare.main.Cards.MedicineCard;
import com.doctl.patientcare.main.Cards.MythCard;
import com.doctl.patientcare.main.Cards.QuestionCard;
import com.doctl.patientcare.main.Cards.VitalCard;
import com.doctl.patientcare.main.Cards.WalkCard;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.constants.Constants;
import com.doctl.patientcare.main.om.BaseTasks;
import com.doctl.patientcare.main.om.MedicineTask;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.google.android.youtube.player.YouTubeStandalonePlayer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Administrator on 6/13/2014.
 */
public class CardListFragment extends BaseFragment implements OnRefreshListener {
    private static final String TAG_TAKS = "tasks";
    private static final String TAG = CardListFragment.class.getSimpleName();
    private static String ANDROID_DEVELOPER_KEY = "AIzaSyAWocbee6JmNy1KShjdNWy_v8_xEq0-gE0";
    private CardView cardView;
    PullToRefreshLayout mPullToRefreshLayout;
    public static final int SIMULATED_REFRESH_LENGTH = 5000;
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
        setListnerToCard(card0);
        card0.setShadow(false);
        card0.setSwipeable(false);
        cards.add(card0);

        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
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
        setListnerToCard(card0);
        card0.setShadow(false);
        card0.setSwipeable(false);
        cards.add(card0);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray tasks = jsonObj.getJSONArray(TAG_TAKS);
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject task = tasks.getJSONObject(i);
                Log.d(TAG, "************** Task Start *********************");
                Log.e(TAG, task.toString());
                Log.d(TAG, "************** Task Stop *********************");
                Log.d(TAG, "************** Task Type Start *********************");
                Log.e(TAG, task.getString(BaseTasks.TAG_TASK_TYPE));
                Log.d(TAG, "************** Task Type Stop *********************");
                switch (BaseTasks.CardType.valueOf(task.getString(BaseTasks.TAG_TASK_TYPE))){
                    case MEDICINE:
                        MedicineTask medicineTask = MedicineTask.parseJson(task);
                        CardHeader medicineHeader = new CardHeaderInnerView(getActivity(), "30", "MINS FROM NOW", "BEFORE MEALS");
                        MedicineCard medicineCard = new MedicineCard(getActivity(), medicineHeader, medicineTask); // pass medicine object
                        medicineCard.setId(medicineTask.getCardId());
                        medicineCard.getId();
//                        setListnerToCard(medicineCard);
                        cards.add(medicineCard);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "************** JSONException Message Start *********************");
            Log.e(TAG, e.getMessage());
            Log.d(TAG, "************** JSONException Message Stop *********************");
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
                mCardArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setListnerToCard(BaseCard card){
        card.setId(UUID.randomUUID().toString());
    }

    private void setListnerToEducationCard(BaseCard card){
        card.setId(UUID.randomUUID().toString());
        boolean clickable = card.isClickable();
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), ANDROID_DEVELOPER_KEY, "MGL6km1NBWE");
                startActivity(intent);
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
