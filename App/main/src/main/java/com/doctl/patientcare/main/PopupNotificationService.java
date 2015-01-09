package com.doctl.patientcare.main;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctl.patientcare.main.Cards.CardHeaderInnerView;
import com.doctl.patientcare.main.Cards.MedicineCard;
import com.doctl.patientcare.main.Cards.VitalCard;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.education.EducationTask;
import com.doctl.patientcare.main.om.followup.FollowupTask;
import com.doctl.patientcare.main.om.medicines.Medicine;
import com.doctl.patientcare.main.om.medicines.MedicineTask;
import com.doctl.patientcare.main.om.message.MessageTask;
import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;

import static android.view.View.OnClickListener;

/**
 * Created by mailtovishal.r on 6/27/2014.
 */
public class PopupNotificationService extends Service {
    private static final String TAG = PopupNotificationService.class.getSimpleName();
    private WindowManager mWindowManager;
    private LinearLayout mRootLayout;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String card = intent.getStringExtra("card");
            BaseTask task = parseCardData(card);
            setContentViewForActivity(task);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRootLayout != null) mWindowManager.removeView(mRootLayout);
    }



    private BaseTask parseCardData(String jsonStr) {
        Log.d(TAG, jsonStr);
        JsonParser parser = new JsonParser();
        JsonObject cardJsonObj = parser.parse(jsonStr).getAsJsonObject();
        switch (BaseTask.CardType.valueOf(cardJsonObj.get("type").getAsString().toUpperCase())) {
            case MEDICINE:
                return new Gson().fromJson(cardJsonObj, MedicineTask.class);
            case VITAL:
                return new Gson().fromJson(cardJsonObj, VitalTask.class);
            case FOLLOWUP:
                return new Gson().fromJson(cardJsonObj, FollowupTask.class);
            case SIMPLEREMINDER:
                return new Gson().fromJson(cardJsonObj, MessageTask.class);
            case EDUCATION:
                return new Gson().fromJson(cardJsonObj, EducationTask.class);
        }
        return null;
    }

    private void setContentViewForActivity(BaseTask task){
        Log.e(TAG, "Setting inner content");
        switch (task.getType()){
            case MEDICINE:
                mRootLayout = (LinearLayout) LayoutInflater.from(this).
                        inflate(R.layout.fragment_card, null);
                setInnerContentForMedicinePopup((MedicineTask) task);
                break;
            case VITAL:
                mRootLayout = (LinearLayout) LayoutInflater.from(this).
                        inflate(R.layout.fragment_card, null);
                setInnerContentForVitalPopup((VitalTask) task);
                break;
            default:
                stopSelf();
                return;
        }
        WindowManager.LayoutParams mRootLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        mRootLayoutParams.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
        mRootLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        mRootLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e(TAG, "Key Pressed");
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    stopSelf();
                }
                return false;
            }
        });
        mWindowManager.addView(mRootLayout, mRootLayoutParams);
    }

    private void setInnerContentForMedicinePopup(MedicineTask task){
        CardView cardView = (CardView) mRootLayout.findViewById(R.id.card_layout);
        CardHeader cardHeader = new CardHeaderInnerView(this){
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
                textView1.setText("Its time for your medicine. Have you taken?");
                textView1.setTextSize(20);
            }
        };
        final MedicineCard card = new MedicineCard(this, cardHeader,task, false, false, false);
        card.setSwipeable(false);
        cardView.setCard(card);
        card.setOnSwipeListener(new Card.OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                stopSelf();
            }
        });

        OnClickListener yesClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setMedicineState(Medicine.MedicineTakenState.TAKEN);
                card.UpdateTask();
                stopSelf();
            }
        };
        OnClickListener noClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setMedicineState(Medicine.MedicineTakenState.DISMISSED);
                card.UpdateTask();
                stopSelf();
            }
        };
        OnClickListener laterClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setMedicineState(Medicine.MedicineTakenState.SNOOZED);
                card.UpdateTask();
                stopSelf();
            }
        };

        Button yesButton = (Button) mRootLayout.findViewById(R.id.card_yes_button);
        yesButton.setText("Yes");
        yesButton.setVisibility(View.VISIBLE);
        yesButton.setOnClickListener(yesClickListener);

        Button noButton = (Button) mRootLayout.findViewById(R.id.card_no_button);
        noButton.setText("No");
        noButton.setVisibility(View.VISIBLE);
        noButton.setOnClickListener(noClickListener);

        Button laterButton = (Button) mRootLayout.findViewById(R.id.card_snooze_button);
        laterButton.setText("Later");
        laterButton.setVisibility(View.VISIBLE);
        laterButton.setOnClickListener(laterClickListener);
    }

    private void setInnerContentForVitalPopup(final VitalTask task){
        CardView cardView = (CardView) mRootLayout.findViewById(R.id.card_layout);
        CardHeader cardHeader = new CardHeaderInnerView(this){
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                TextView textView1 = (TextView)view.findViewById(R.id.timeWhen);
                textView1.setText("Please enter vital values");
                textView1.setTextSize(20);
            }
        };
        final VitalCard card = new VitalCard(this, cardHeader,task, false, false, false);
        card.setSwipeable(false);
        cardView.setCard(card);
        card.setOnSwipeListener(new Card.OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                stopSelf();
            }
        });

        OnClickListener yesClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                card.UpdateTask();
                stopSelf();
            }
        };

        OnClickListener laterClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        };

        Button yesButton = (Button) mRootLayout.findViewById(R.id.card_yes_button);
        yesButton.setText("Done");
        yesButton.setVisibility(View.VISIBLE);
        yesButton.setOnClickListener(yesClickListener);

        Button noButton = (Button) mRootLayout.findViewById(R.id.card_no_button);
        noButton.setVisibility(View.GONE);

        Button laterButton = (Button) mRootLayout.findViewById(R.id.card_snooze_button);
        laterButton.setText("Later");
        laterButton.setVisibility(View.VISIBLE);
        laterButton.setOnClickListener(laterClickListener);
    }
}
