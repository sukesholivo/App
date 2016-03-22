package in.olivo.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import in.olivo.patientcare.main.R;
import it.gmariotti.cardslib.library.internal.CardHeader;

import static android.view.View.OnClickListener;

/**
 * Created by mailtovishal.r on 6/27/2014.
 */
public class HowruFeelingCard extends BaseCard {
    private static final String TAG = HowruFeelingCard.class.getSimpleName();

    public HowruFeelingCard(Context context) {
        this(context, R.layout.card_inner_content_how_are_you_feeling);
    }

    public HowruFeelingCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public HowruFeelingCard(Context context, int innerLayout, CardHeader cardHeader) {
        super(context, innerLayout, cardHeader);
    }

    public HowruFeelingCard(Context context, CardHeader cardHeader) {
        this(context, R.layout.card_inner_content_how_are_you_feeling, cardHeader);
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, View view) {
//        this.setBackgroundResourceId(R.drawable.card_background_blue);
        RelativeLayout ly = (RelativeLayout) view.findViewById(R.id.smiley_images);
        int lyChildCount = ly.getChildCount();
        for (int i = 0; i < lyChildCount; i++) {
            LinearLayout ly1 = (LinearLayout) ly.getChildAt(i);

            OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    RelativeLayout parentLY = (RelativeLayout) view.getParent();

                    ImageView iv1 = (ImageView) parentLY.findViewById(R.id.feeling_normal_unit_tick);
                    iv1.setVisibility(View.INVISIBLE);

                    ImageView iv2 = (ImageView) parentLY.findViewById(R.id.feeling_ok_unit_tick);
                    iv2.setVisibility(View.INVISIBLE);

                    ImageView iv3 = (ImageView) parentLY.findViewById(R.id.feeling_sad_unit_tick);
                    iv3.setVisibility(View.INVISIBLE);

                    int id = view.getId();

                    switch (id) {
                        case R.id.feeling_ok_unit:
                            iv2.setVisibility(View.VISIBLE);
                            break;
                        case R.id.feeling_normal_unit:
                            iv1.setVisibility(View.VISIBLE);
                            break;
                        case R.id.feeling_sad_unit:
                            iv3.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            };
            ly1.setOnClickListener(clickListener);
        }

    }

    @Override
    public int getType() {
        return CardType.DUMMY_CARD_TYPE.ordinal();
    }
}
