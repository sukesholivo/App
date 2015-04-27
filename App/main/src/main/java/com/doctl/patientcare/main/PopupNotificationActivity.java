package com.doctl.patientcare.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctl.patientcare.main.Cards.CardHeaderInnerView;
import com.doctl.patientcare.main.Cards.MedicineCard;
import com.doctl.patientcare.main.Cards.VitalCard;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.medicines.Medicine;
import com.doctl.patientcare.main.om.medicines.MedicineTask;
import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.doctl.patientcare.main.utility.Utils;

import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Administrator on 1/29/2015.
 */
public class PopupNotificationActivity extends Activity {
    private static final String TAG = PopupNotificationActivity.class.getSimpleName();
    private LinearLayout mRootLayout;
    String mYesString = "YES";
    DialogInterface.OnClickListener mYesClickListener;
    String mNoString = "NO";
    DialogInterface.OnClickListener mNoClickListener;
    String mLaterString = "LATER";
    DialogInterface.OnClickListener mLaterClickListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null){
            return;
        }
        String card = bundle.getString("card");
        BaseTask task = Utils.parseCardData(card);
        setContentViewForActivity(task);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setIcon(0)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        PopupNotificationActivity.this.finish();
                    }
                })
                .setCancelable(true)
                .setView(mRootLayout);

        if (mYesClickListener != null){
            alertDialogBuilder.setPositiveButton(mYesString, mYesClickListener);
        }
        if (mNoClickListener != null){
            alertDialogBuilder.setNegativeButton(mNoString, mNoClickListener);
        }
        if (mLaterClickListener != null){
            alertDialogBuilder.setNeutralButton(mLaterString, mLaterClickListener);
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
//        wmlp.gravity = Gravity.BOTTOM;

        alertDialog.show();
    }

    private void setContentViewForActivity(BaseTask task){
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
                break;
        }
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

        mYesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.setMedicineState(Medicine.MedicineTakenState.TAKEN);
                card.UpdateTask();
                finish();
            }
        };
        mNoClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.setMedicineState(Medicine.MedicineTakenState.DISMISSED);
                card.UpdateTask();
                finish();
            }
        };
        mLaterClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.setMedicineState(Medicine.MedicineTakenState.SNOOZED);
                card.UpdateTask();
                finish();
            }
        };
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

        mYesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.UpdateTask();
                finish();
            }
        };

        mLaterClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
    }
}
