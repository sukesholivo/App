package in.olivo.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.olivo.patientcare.main.R;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/28/2014.
 */
public class MythCard extends BaseCard {
    private static final String TAG = MythCard.class.getSimpleName();

    public MythCard(Context context) {
        this(context, R.layout.card_inner_content_myth);
    }

    public MythCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public MythCard(Context context, int innerLayout, CardHeader cardHeader) {
        super(context, innerLayout, cardHeader);
    }

    public MythCard(Context context, CardHeader cardHeader) {
        this(context, R.layout.card_inner_content_myth, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        this.setBackgroundResourceId(R.drawable.card_background_white);
        TextView mythTitle = (TextView) view.findViewById(R.id.mythTitle);
        mythTitle.setText("DIABETES IS CAUSED BY EATING TOO MUCH SUGAR.");
        mythTitle.bringToFront();

        ImageView mythImage = (ImageView) view.findViewById(R.id.mythImage);
        mythImage.setImageResource(R.drawable.education_myths_sugar);

        TextView mythDescription = (TextView) view.findViewById(R.id.mythDescription);
        mythDescription.setText("Type 1 diabetes is caused by genetics and unknown factors. " +
                "Being overweight increases your risk for developing type 2, " +
                "and a diet high in calories from any source contributes to weight gain.");
        mythDescription.bringToFront();
    }

    @Override
    public int getType() {
        return CardType.DUMMY_CARD_TYPE.ordinal();
    }
}
