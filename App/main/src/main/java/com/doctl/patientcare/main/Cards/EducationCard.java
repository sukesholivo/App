package com.doctl.patientcare.main.Cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.education.EducationTask;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;


public class EducationCard extends BaseCard{
    private static final String TAG = EducationCard.class.getSimpleName();

    public EducationCard(Context context) {
        this(context, R.layout.card_inner_content_education_video);
    }

    public EducationCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public EducationCard(Context context, int innerLayout, CardHeader cardHeader, EducationTask educationTask){
        super(context, innerLayout, cardHeader);
        this.task = educationTask;
        this.setId(educationTask.getCardId());
    }

    public EducationCard(Context context, CardHeader cardHeader, EducationTask educationTask) {
        this(context, R.layout.card_inner_content_education_video, cardHeader, educationTask);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        setListnerToCard();
        final EducationTask.EducationData educationData = ((EducationTask)task).getPayload();
        String thumbnail_url = educationData.getThumbnail();
        Activity activity = (Activity)getContext();
        ImageView imageView = (ImageView)view.findViewById(R.id.videoPreview);
        try {
            URL url = new URL(Constants.SERVER_URL + thumbnail_url);
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            imageView.setImageBitmap(bmp);
            Picasso.with(getContext())
                        .load(Constants.SERVER_URL + thumbnail_url)
                        .into(imageView);

            //set right icon on thumbnail.

            String content_url = educationData.getUrl();
            if(content_url.toUpperCase().contains("YOUTUBE.COM"))
            {
                ImageView iconView = (ImageView)view.findViewById(R.id.playIcon);
                iconView.setImageResource(R.drawable.play_icon);
                iconView.setVisibility(View.VISIBLE);
            }
            else
            {
                ImageView iconView = (ImageView)view.findViewById(R.id.redirectIcon);
                iconView.setImageResource(R.drawable.play_icon);
                iconView.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
        setupCardFooter(view, task);
        updateCardAsSeen();
    }

    private void setListnerToCard(){
        this.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Context context = getContext();
                final EducationTask.EducationData educationData = ((EducationTask)task).getPayload();
                String content_url = educationData.getUrl();
                if(content_url.toUpperCase().contains("YOUTUBE.COM"))
                {
                    final String delimiter = "=";
                    String video_id = content_url.split(delimiter)[1];
                    if(video_id.contains("&"))
                    {
                        video_id = video_id.split("&")[0];
                    }
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity)context, Constants.ANDROID_DEVELOPER_KEY, video_id);
                    context.startActivity(intent);
                }
                else{
                    Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                    httpIntent.setData(Uri.parse(content_url));
                    context.startActivity(httpIntent);
                }
            }
        });
    }

    @Override
    public int getType() {
        return CardType.EDUCATION_CARD_TYPE.ordinal();
    }

    @Override
    public void UpdateTask(){
        JSONObject data;
        EducationTask educationTask = (EducationTask)task;
        educationTask.setState(BaseTask.CardState.DONE);
        String cardId = educationTask.getCardId();
        try {
            data = educationTask.getDataToPatch();
            Logger.d(TAG, data.toString());
            UpdateTask(cardId, data);
        }catch (JSONException e){
            Logger.e(TAG, e.toString());
        }
    }
}
