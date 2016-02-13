package com.doctl.patientcare.main.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.doctl.patientcare.main.R;

import java.io.IOException;

/**
 * Created by satya on 8/1/16.
 */
public class ImageAttachFragment extends Fragment {
    private static final int SELECT_IMAGE = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_thread_detail,container, false);
        ImageButton attachButton = (ImageButton) v.findViewById(R.id.list_cardId);
        System.out.println(" ########  Button #########3" + attachButton);

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });
        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        System.out.println(" Got Image ");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
