package in.olivo.patientcare.main.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import in.olivo.patientcare.main.Cards.BaseCard;
import in.olivo.patientcare.main.Cards.CardHeaderInnerView;
import in.olivo.patientcare.main.Cards.DashboardCard;
import in.olivo.patientcare.main.Cards.EducationCard;
import in.olivo.patientcare.main.Cards.FollowupCard;
import in.olivo.patientcare.main.Cards.MedicineCard;
import in.olivo.patientcare.main.Cards.MessageCard;
import in.olivo.patientcare.main.Cards.VitalCard;
import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.BaseTask;
import in.olivo.patientcare.main.om.education.EducationTask;
import in.olivo.patientcare.main.om.followup.FollowupTask;
import in.olivo.patientcare.main.om.medicines.Medicine;
import in.olivo.patientcare.main.om.medicines.MedicineTask;
import in.olivo.patientcare.main.om.message.MessageTask;
import in.olivo.patientcare.main.om.vitals.VitalTask;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Administrator on 6/13/2014.
 */
public class CardListFragment extends BaseFragment implements OnRefreshListener {
    private static final String TAG = CardListFragment.class.getSimpleName();
    PullToRefreshLayout mPullToRefreshLayout;
    ArrayList<Card> cards;
    CardArrayAdapter mCardArrayAdapter;
    CardListView listView;
    FloatingActionButton fab;
    View.OnClickListener primaryActionListener = null;
    View.OnClickListener secondaryActionListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i(TAG, " OnCreateView: START");
        return inflater.inflate(R.layout.fragment_card_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*fab = (FloatingActionButton) getActivity().findViewById(R.id.buttonTop);
//        fab.attachToListView(listView);
        fab.hide(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.smoothScrollToPosition(0);
            }
        });*/
        initializeCardList();
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private void refresh() {
        if (Utils.isNetworkAvailable(getActivity())) {
            new GetTasks().execute();
        } else {
            Toast.makeText(getActivity(), "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    protected void initializeCardList() {
        cards = new ArrayList<>();
        DashboardCard card0 = new DashboardCard(getActivity(), null);
        card0.setShadow(false);
        card0.setSwipeable(false);
        cards.add(card0);

        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        mCardArrayAdapter.setEnableUndo(true);
        listView = (CardListView) getActivity().findViewById(R.id.card_list_layout);
        /*listView.setOnScrollListener(
                new SwipeOnScrollListener() {
                    int mLastFirstVisibleItem = -1;
                    int mLastVisibleItemCount = -1;
                    boolean isDashboardHidden = false;

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        //It is very important to call the super method here to preserve built-in functions
                        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                            Logger.i("a", "scrolling stopped...");
                            if (isDashboardHidden) {
                                fab.show(true);
                            }
                        }
                        super.onScrollStateChanged(view,scrollState);
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        fab.hide(true);
                        if(mLastFirstVisibleItem >=0 && mLastVisibleItemCount >=0) {
                            if (mLastFirstVisibleItem > firstVisibleItem) {
                                Logger.i(TAG, "scrolling up");
                                DashboardAppear(firstVisibleItem);

                            } else if (mLastFirstVisibleItem < firstVisibleItem) {
                                Logger.i(TAG, "scrolling down");
                                DashboardDisappear();
                            } else if (mLastVisibleItemCount < visibleItemCount) {
                                Logger.i(TAG, "scrolling down");
                                //DashboardDisappear();
                            } else if (mLastVisibleItemCount > visibleItemCount) {
                                Logger.i(TAG, "scrolling up");
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

                    private void DashboardDisappear() {
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
*/
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
//        String data = Utils.parsonJsonFromFile(getActivity(), R.raw.tasks);
        if (data != null && !data.isEmpty()) {
            ArrayList<BaseCard> cards = parseCardData(data);
            resetCardList(cards);
        }
    }

    private String downloadCardData() {
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(getActivity());
        return serviceHandler.makeServiceCall(
                Constants.CARDS_URL, HTTPServiceHandler.HTTPMethod.GET, Utils.getCardsHTTPGetQueryParam(), null);
    }

    private void removeCard(BaseCard card) {
        mCardArrayAdapter.remove(card);
        mCardArrayAdapter.notifyDataSetChanged();
        card.UpdateTask();
    }

    private ArrayList<BaseCard> parseCardData(String jsonStr) {
        ArrayList<BaseCard> cards = new ArrayList<>();
        DashboardCard card0 = new DashboardCard(getActivity(), null);
        card0.setShadow(false);
        card0.setSwipeable(false);
        cards.add(card0);
        Logger.d(TAG, jsonStr);
        JsonParser parser = new JsonParser();
        JsonArray cardsJsonArray = parser.parse(jsonStr).getAsJsonArray();
        for (JsonElement cardJson : cardsJsonArray) {
            JsonObject cardJsonObj = cardJson.getAsJsonObject();
            switch (BaseTask.CardType.lookup(cardJsonObj.get("type").getAsString())) {
                case MEDICINE:
                    final MedicineTask medicineTask = new Gson().fromJson(cardJson, MedicineTask.class);
                    CardHeader medicineHeader = Utils.getCardHeader(getActivity(), medicineTask);
                    final MedicineCard medicineCard = new MedicineCard(getActivity(), medicineHeader, medicineTask);

                    primaryActionListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            medicineCard.setMedicineState(Medicine.MedicineTakenState.TAKEN);
                            removeCard(medicineCard);
                        }
                    };
                    secondaryActionListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            medicineCard.setMedicineState(Medicine.MedicineTakenState.DISMISSED);
                            removeCard(medicineCard);
                        }
                    };
                    medicineCard.setListenerToActionButtons("Taken", primaryActionListener, "Missed", secondaryActionListener);
                    cards.add(medicineCard);
                    break;
                case VITAL:
                    VitalTask vitalTask = new Gson().fromJson(cardJson, VitalTask.class);
                    CardHeader vitalHeader = Utils.getCardHeader(getActivity(), vitalTask);
                    final VitalCard vitalCard = new VitalCard(getActivity(), vitalHeader, vitalTask);

                    primaryActionListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            removeCard(vitalCard);
                        }
                    };
                    vitalCard.setListenerToActionButtons("Submit", primaryActionListener);
                    cards.add(vitalCard);
                    break;
                case FOLLOWUP:
                    FollowupTask followupTask = new Gson().fromJson(cardJson, FollowupTask.class);
                    final String title = followupTask.getPayload().getTitle();
                    CardHeader followupHeader = new CardHeaderInnerView(getActivity()) {
                        @Override
                        public void setupInnerViewElements(ViewGroup parent, View view) {
                            TextView textView1 = (TextView) view.findViewById(R.id.timeWhen);
                            textView1.setText(title);
                            textView1.setTextSize(12);
                        }
                    };
                    final FollowupCard followupCard = new FollowupCard(getActivity(), followupHeader, followupTask);

                    primaryActionListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Logger.d(TAG, "Yes CLICKED");
                            removeCard(followupCard);
                        }
                    };
                    followupCard.setListenerToActionButtons("Send", primaryActionListener);

                    cards.add(followupCard);
                    break;
                case SIMPLEREMINDER:
                case GENERICREMINDER:
                    MessageTask messageTask = new Gson().fromJson(cardJson, MessageTask.class);
                    final String messageTitle = messageTask.getPayload().getTitle();
                    CardHeader messageHeader = new CardHeaderInnerView(getActivity()) {
                        @Override
                        public void setupInnerViewElements(ViewGroup parent, View view) {
                            TextView textView1 = (TextView) view.findViewById(R.id.timeWhen);
                            textView1.setText(messageTitle);
                            textView1.setTextSize(12);
                        }
                    };
                    MessageCard messageCard = new MessageCard(getActivity(), messageHeader, messageTask);
                    cards.add(messageCard);
                    break;
                case EDUCATION:
                    EducationTask educationTask = new Gson().fromJson(cardJson, EducationTask.class);
                    final String eduTitle = educationTask.getPayload().getTitle();
                    CardHeader educationHeader = new CardHeaderInnerView(getActivity()) {
                        @Override
                        public void setupInnerViewElements(ViewGroup parent, View view) {
                            TextView textView1 = (TextView) view.findViewById(R.id.timeWhen);
                            textView1.setText(eduTitle);
                            textView1.setTextSize(12);
                        }
                    };
                    EducationCard educationCard = new EducationCard(getActivity(), educationHeader, educationTask);
                    cards.add(educationCard);
                    break;
            }
        }

//        CardHeader walkHeader = new CardHeaderInnerView(getActivity(), "TODAY", "", "");
//        WalkCard card4 = new WalkCard(getActivity(), walkHeader);
//
//        CardHeader richEducationHeader = new CardHeaderInnerView(getActivity()){
//            @Override
//            public void setupInnerViewElements(ViewGroup parent, View view) {
//                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
//                textView1.setText("HOW TO INJECT INSULIN");
//                textView1.setTextSize(28);
//            }
//        };
        //, "HOW TO INJECT INSULIN", "", "");
//        EducationRichtextCard card5 = new EducationRichtextCard(getActivity(), richEducationHeader );
//
//        CardHeader howruFeelingHeader = new CardHeaderInnerView(getActivity(), "HOW ARE YOU FEELING?", "", "");
//        HowruFeelingCard card7 = new HowruFeelingCard(getActivity(), howruFeelingHeader);
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

//        ImageCard card9 = new ImageCard(getActivity(), null);
//        card9.setImageResourceId(R.drawable.motivationcard_backgroundpic4_lesssugar);
//
//        ImageCard card10 = new ImageCard(getActivity(), null);
//        card10.setImageResourceId(R.drawable.education_myths_sugar_full);
//
//        ImageCard card11 = new ImageCard(getActivity(), null);
//        card11.setImageResourceId(R.drawable.education_myths_weightloss_full);

//        cards.add(card4);
//        cards.add(card5);
//        cards.add(card7);
//        cards.add(card9);
//        cards.add(card10);
//        cards.add(card11);
        Collections.sort(cards, new Comparator<BaseCard>() {
            @Override
            public int compare(BaseCard card1, BaseCard card2) {
                return card1.getType() - card2.getType();
            }
        });
        return cards;
    }

    private void resetCardList(final ArrayList<BaseCard> cardArrayList) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    View dashboard = getActivity().findViewById(R.id.treatment_dashboard_layout);
                    dashboard.setVisibility(View.VISIBLE);
                    if (mCardArrayAdapter == null) {
                        Logger.d(TAG, "mCardArrayAdapter is null");
                    }
                    mCardArrayAdapter.clear();
                    mCardArrayAdapter.addAll(cardArrayList);
                    mCardArrayAdapter.setEnableUndo(true);
                    mCardArrayAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        refresh();
    }

    /**
     * Async task class to get json by making HTTP call
     */
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
