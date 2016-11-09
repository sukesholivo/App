package in.olivo.patientcare.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.olivo.patientcare.main.Cards.CardHeaderInnerView;
import in.olivo.patientcare.main.Cards.MedicineCard;
import in.olivo.patientcare.main.Cards.ObjectiveCard;
import in.olivo.patientcare.main.Cards.VitalCard;
import in.olivo.patientcare.main.om.BaseTask;
import in.olivo.patientcare.main.om.education.ObjectiveTask;
import in.olivo.patientcare.main.om.medicines.Medicine;
import in.olivo.patientcare.main.om.medicines.MedicineTask;
import in.olivo.patientcare.main.om.vitals.VitalTask;
import in.olivo.patientcare.main.utility.Utils;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Administrator on 1/29/2015.
 */
public class PopupNotificationActivity extends Activity {
    private static final String TAG = PopupNotificationActivity.class.getSimpleName();
    String mYesString = "YES";
    DialogInterface.OnClickListener mYesClickListener;
    String mNoString = "NO";
    DialogInterface.OnClickListener mNoClickListener;
    String mLaterString = "LATER";
    DialogInterface.OnClickListener mLaterClickListener;
    private LinearLayout mRootLayout;
    AlertDialog alertDialog;

    Ringtone r;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
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
                .setCancelable(false)
                .setView(mRootLayout);

        if (mYesClickListener != null) {
            alertDialogBuilder.setPositiveButton(mYesString, mYesClickListener);
        }
        if (mNoClickListener != null) {
            alertDialogBuilder.setNegativeButton(mNoString, mNoClickListener);
        }
        if (mLaterClickListener != null) {
            alertDialogBuilder.setNeutralButton(mLaterString, mLaterClickListener);
        }
        alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
//        wmlp.gravity = Gravity.BOTTOM;

        alertDialog.show();

        playSound(this, getAlarmUri());
    }

    private void setContentViewForActivity(BaseTask task) {
        switch (task.getType()) {
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
            case OBJECTIVE:
                mRootLayout = (LinearLayout) LayoutInflater.from(this).
                        inflate(R.layout.fragment_card, null);
                setInnerContentForObjectivePopup((ObjectiveTask) task);
            default:
                break;
        }
    }

    private void setInnerContentForMedicinePopup(MedicineTask task) {
        CardView cardView = (CardView) mRootLayout.findViewById(R.id.card_layout);
        CardHeader cardHeader = new CardHeaderInnerView(this) {
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                TextView textView1 = (TextView) view.findViewById(R.id.timeWhen);
                textView1.setText("Its time for your medicine. Have you taken?");
                textView1.setTextSize(12);
            }
        };
        final MedicineCard card = new MedicineCard(this, cardHeader, task, false, false, false);
        card.setSwipeable(false);
        cardView.setCard(card);

        mYesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.setMedicineState(Medicine.MedicineTakenState.TAKEN);
                card.UpdateTask();

                r.stop();
                finish();
            }
        };
        mNoClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.setMedicineState(Medicine.MedicineTakenState.DISMISSED);
                card.UpdateTask();
                r.stop();
                finish();
            }
        };
        mLaterClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.setMedicineState(Medicine.MedicineTakenState.SNOOZED);
                card.UpdateTask();
                r.stop();
                finish();
            }
        };
    }

    private void setInnerContentForObjectivePopup(final ObjectiveTask task) {
        CardView cardView = (CardView) mRootLayout.findViewById(R.id.card_layout);
        CardHeader cardHeader = new CardHeaderInnerView(this) {
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                TextView textView1 = (TextView) view.findViewById(R.id.timeWhen);
                textView1.setText(task.getPayload().getQuestion());
                textView1.setTextSize(18);
            }
        };
        final ObjectiveCard card = new ObjectiveCard(this, cardHeader, task, false, false, false);
        card.setSwipeable(false);
        cardView.setCard(card);

        mYesString = "Done";
        mNoString = "Cancel";

        mYesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*card.setMedicineState(Medicine.MedicineTakenState.TAKEN);*/
                card.UpdateTask();
                if(task.getPayload().getAnswerId() != null) {
                    task.setState(BaseTask.CardState.DONE);
                    card.UpdateTask();
                    r.stop();
                    finish();
                }else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        };
        mNoClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setState(BaseTask.CardState.SEEN);
                card.UpdateTask();
                r.stop();
                finish();
            }
        };/*
        mLaterClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.setMedicineState(Medicine.MedicineTakenState.SNOOZED);
                card.UpdateTask();
                finish();
            }
        };*/
    }

    private void setInnerContentForVitalPopup(final VitalTask task) {
        CardView cardView = (CardView) mRootLayout.findViewById(R.id.card_layout);
        CardHeader cardHeader = new CardHeaderInnerView(this) {
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                TextView textView1 = (TextView) view.findViewById(R.id.timeWhen);
                textView1.setText("Please enter vital values");
                textView1.setTextSize(12);
            }
        };
        final VitalCard card = new VitalCard(this, cardHeader, task, false, false, false);
        card.setSwipeable(false);
        cardView.setCard(card);

        mYesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                card.UpdateTask();
                r.stop();
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

    private void playSound(final Context context, Uri alert) {


        Thread background = new Thread(new Runnable() {
            public void run() {

            }
        });
        background.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            }
    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {

        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
         r = RingtoneManager.getRingtone(getBaseContext(), alert);

        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        if(r != null)
            r.play();

        return alert;
    }


}