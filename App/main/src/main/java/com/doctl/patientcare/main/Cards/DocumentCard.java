package com.doctl.patientcare.main.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.documents.Document;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.squareup.picasso.Picasso;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 4/27/2015.
 */
public class DocumentCard extends Card {
    Document document;
    View.OnClickListener deleteActionButtonClickListener;
    public DocumentCard(Context context) {
        this(context, R.layout.card_inner_content_document);
    }

    public DocumentCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public DocumentCard(Context context, int innerLayout, Document document){
        super(context, innerLayout);
        this.document = document;
        this.setBackgroundResourceId(R.drawable.card_background_gray);
    }

    public DocumentCard(Context context, Document document) {
        this(context, R.layout.card_inner_content_document, document);
    }

    public void setListenerToDeleteButtons(View.OnClickListener deleteActionButtonClickListener){
        this.deleteActionButtonClickListener = deleteActionButtonClickListener;
    }

    public void setupInnerViewElements(ViewGroup parent, View view) {
        String thumbnail_url = document.getFileUrl();
        Logger.e("", "Thumbnail url: " + thumbnail_url);
        ImageView imageView = (ImageView)view.findViewById(R.id.document_preview);
        if (Utils.isImageFile(thumbnail_url)) {
            Picasso.with(getContext())
                    .load(Constants.SERVER_URL + thumbnail_url)
                    .into(imageView);
        }
        TextView titleTextView = (TextView)view.findViewById(R.id.document_title);
        titleTextView.setText(document.getTitle());
        TextView descriptionTextView = (TextView)view.findViewById(R.id.document_description);
        descriptionTextView.setText(document.getDescription());

        Button deleteDocumentButton = (Button)view.findViewById(R.id.delete_document_button);
        deleteDocumentButton.setOnClickListener(deleteActionButtonClickListener);
    }

    public String getCardId(){
        return this.document.getId();
    }
}
