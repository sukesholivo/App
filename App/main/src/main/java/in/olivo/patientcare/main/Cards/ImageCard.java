package in.olivo.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.olivo.patientcare.main.R;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 6/28/2014.
 */
public class ImageCard extends BaseCard {
    private static final String TAG = MythCard.class.getSimpleName();
    private boolean isInfluencerVisible = false;
    private int imageResourceId;

    public ImageCard(Context context) {
        this(context, R.layout.card_inner_content_image);
    }

    public ImageCard(Context context, int innerLayout) {
        super(context, innerLayout);
        this.imageResourceId = R.drawable.motivationcard_backgroundpic4_lesssugar;
    }

    public ImageCard(Context context, int innerLayout, CardHeader cardHeader) {
        super(context, innerLayout, cardHeader);
        this.imageResourceId = R.drawable.motivationcard_backgroundpic4_lesssugar;
    }

    public ImageCard(Context context, CardHeader cardHeader) {
        this(context, R.layout.card_inner_content_image, cardHeader);
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public void setInfluencerVisible(boolean isInfluencerVisible) {
        this.isInfluencerVisible = isInfluencerVisible;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        this.setBackgroundResourceId(R.drawable.card_background_white);

        ImageView image = (ImageView) view.findViewById(R.id.cardImage);
        image.setImageResource(imageResourceId);

        if (isInfluencerVisible) {
            ImageView influencerImage = (ImageView) view.findViewById(R.id.influencerImage);
            influencerImage.setImageResource(R.drawable.profile_dummy);
        }
    }

    @Override
    public int getType() {
        return CardType.DUMMY_CARD_TYPE.ordinal();
    }
}
