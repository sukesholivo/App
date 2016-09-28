package in.olivo.patientcare.main.Cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import in.olivo.patientcare.main.MedicineDetailActivity;
import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.BaseTask;
import in.olivo.patientcare.main.om.medicines.Medicine;
import in.olivo.patientcare.main.om.medicines.MedicineAdapter;
import in.olivo.patientcare.main.om.medicines.MedicineTask;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/12/2014.
 */
public class MedicineCard extends BaseCard {
    private static final String TAG = MedicineCard.class.getSimpleName();
    private boolean addListener = true;
    private int state = Medicine.MedicineTakenState.getInteger(Medicine.MedicineTakenState.TAKEN);

    public MedicineCard(Context context) {
        this(context, R.layout.card_inner_content_medication);
    }

    public MedicineCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public MedicineCard(Context context, int innerLayout, CardHeader cardHeader, MedicineTask medicineTask) {
        super(context, innerLayout, cardHeader);
        this.task = medicineTask;
        this.setId(medicineTask.getCardId());
    }

    public MedicineCard(Context context, CardHeader cardHeader, MedicineTask medicineTask) {
        this(context, R.layout.card_inner_content_medication, cardHeader, medicineTask);
    }

    public MedicineCard(Context context, CardHeader cardHeader, MedicineTask medicineTask,
                        boolean showFooter, boolean addListener, boolean isSwipable) {
        this(context, R.layout.card_inner_content_medication, cardHeader, medicineTask);
        this.showFooter = showFooter;
        this.addListener = addListener;
        this.isSwipable = isSwipable;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ViewHolder viewHolder;
        if (view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.medicationList);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        MedicineAdapter medicines = new MedicineAdapter(getContext(), ((MedicineTask) task).getPayload().getMedicines());

        LinearLayout list = viewHolder.linearLayout;
        list.removeAllViews();
        for (int i = 0; i < medicines.getCount(); i++) {
            View listView = medicines.getView(i, null, null);
            list.addView(listView);
        }
        setupCardFooter(view, task);

        if (addListener) {
            setListenerToCard();
        }

//        updateCardAsSeen();
    }

    private void setListenerToCard() {
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Logger.d("postInitCard", "Card clicked " + card.getId());
                Context context = getContext();
                Intent intent = new Intent(context, MedicineDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("prescriptionId", ((MedicineTask) task).getPayload().getPrescriptionId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getType() {
        return CardType.MEDICINE_CARD_TYPE.ordinal();
    }

    @Override
    public void UpdateTask() {
        JSONObject data;
        MedicineTask medicineTask = (MedicineTask) task;
        ArrayList<Medicine> meds = medicineTask.getPayload().getMedicines();
        medicineTask.setState(BaseTask.CardState.DONE);
        String cardId = medicineTask.getCardId();
        for (Medicine med : meds) {
            med.setTimestamp(Utils.getIsoDateString(new GregorianCalendar()));
            med.setState(state);
        }
        try {
            data = medicineTask.getDataToPatch();
            Logger.d(TAG, data.toString());
            UpdateTask(cardId, data);
        } catch (JSONException e) {
            Logger.e(TAG, e.toString());
        }
    }

    public void setMedicineState(Medicine.MedicineTakenState state) {
        this.state = Medicine.MedicineTakenState.getInteger(state);
    }

    private static class ViewHolder extends BaseViewHolder {
        LinearLayout linearLayout;
    }
}
