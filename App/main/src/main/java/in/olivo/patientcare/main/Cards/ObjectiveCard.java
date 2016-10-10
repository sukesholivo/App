package in.olivo.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.BaseTask;
import in.olivo.patientcare.main.om.education.ObjectiveTask;
import in.olivo.patientcare.main.om.medicines.ObjectiveAdapter;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Satya Madala on 14/7/16.
 * email : satya.madala@olivo.in
 */
public class ObjectiveCard extends BaseCard {
    private static final String TAG = MedicineCard.class.getSimpleName();
    private boolean addListener = true;

    public ObjectiveCard(Context context) {
        this(context, R.layout.card_inner_content_objective);
    }

    public ObjectiveCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public ObjectiveCard(Context context, int innerLayout, CardHeader cardHeader, ObjectiveTask objectiveTask) {
        super(context, innerLayout, cardHeader);
        this.task = objectiveTask;
        this.setId(objectiveTask.getCardId());
    }

    public ObjectiveCard(Context context, CardHeader cardHeader, ObjectiveTask objectiveTask) {
        this(context, R.layout.card_inner_content_objective, cardHeader, objectiveTask);
    }

    public ObjectiveCard(Context context, CardHeader cardHeader, ObjectiveTask objectiveTask,
                        boolean showFooter, boolean addListener, boolean isSwipable) {
        this(context, R.layout.card_inner_content_objective, cardHeader, objectiveTask);
        this.showFooter = showFooter;
        this.addListener = addListener;
        this.isSwipable = isSwipable;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ViewHolder viewHolder;
        if (view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.optionList);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final List<ObjectiveTask.ObjectiveData.Option> options =  ((ObjectiveTask) task).getPayload().getOptions();
        ObjectiveAdapter objectiveAdapter = new ObjectiveAdapter(getContext(), options, (ObjectiveTask) task);

        LinearLayout list = viewHolder.linearLayout;
        //list.setAdapter(objectiveAdapter);
        /*TextView questionView = (TextView)layout.findViewById(R.id.questionText);
        questionView.setText(((ObjectiveTask) task).getPayload().getQuestion());*/

        list.removeAllViews();
        for (int i = 0; i < objectiveAdapter.getCount(); i++) {
            View listView = objectiveAdapter.getView(i, null, null);
           /* listView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView id = (TextView) v.findViewById(R.id.id);
                    Toast.makeText(getContext(), id.getText(), Toast.LENGTH_LONG).show();
                            ((ObjectiveTask) task).getPayload().setAnswerId((String)id.getText());
                }
            });*/
            list.addView(listView);
        }
        setupCardFooter(view, task);

        if (addListener) {
            setListenerToCard();
        }

        updateCardAsSeen();
    }

    private void setListenerToCard() {
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Logger.d("postInitCard", "Card clicked " + card.getId());
                Context context = getContext();
                Toast.makeText(context, "option id" + ((ObjectiveTask) task).getPayload().getId(), Toast.LENGTH_LONG).show();
                /*Intent intent = new Intent(context, MedicineDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("prescriptionId", ((MedicineTask) task).getPayload().getPrescriptionId());
                intent.putExtras(bundle);
                context.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getType() {
        return CardType.OBJECTIVE_CARD_TYPE.ordinal();
    }

    @Override
    public void UpdateTask() {
        JSONObject data;

        ObjectiveTask objectiveTask = (ObjectiveTask) task;
        String cardId = objectiveTask.getCardId();
        try {
            data = objectiveTask.getDataToPatch();
            Logger.d(TAG, data.toString());
            UpdateTask(cardId, data);
        } catch (JSONException e) {
            Logger.e(TAG, e.toString());
        }
    }

    private static class ViewHolder extends BaseViewHolder {
        LinearLayout linearLayout;
    }
}
