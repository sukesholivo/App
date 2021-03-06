package in.olivo.patientcare.main.Cards;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.RichTextEducationDetailActivity;
import in.olivo.patientcare.main.utility.Logger;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

public class EducationRichtextCard extends BaseCard {

    private static final String TAG = EducationCard.class.getSimpleName();

    public EducationRichtextCard(Context context) {
        this(context, R.layout.card_inner_content_education_richtext);
    }

    public EducationRichtextCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public EducationRichtextCard(Context context, int innerLayout, CardHeader cardHeader) {
        super(context, innerLayout, cardHeader);
    }

    public EducationRichtextCard(Context context, CardHeader cardHeader) {
        this(context, R.layout.card_inner_content_education_richtext, cardHeader);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
//        Logger.d(TAG, "Setting up inner view element of card.");
        Random r = new Random();
        int n = r.nextInt() % 100;
        n = n > 0 ? n : -n;
        setupCardFooter(view, "" + n);
        setListnerToCard();
    }

    private void setListnerToCard() {
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
//                Toast.makeText(getContext(), "Card clicked: " + card.getId(), Toast.LENGTH_LONG).show();
                Logger.d("postInitCard", "Card clicked " + card.getId());
                Context context = getContext();
                Intent intent = new Intent(context, RichTextEducationDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getType() {
        return CardType.EDUCATION_CARD_TYPE.ordinal();
    }
}
