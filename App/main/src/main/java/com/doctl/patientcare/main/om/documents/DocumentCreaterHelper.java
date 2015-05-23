package com.doctl.patientcare.main.om.documents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.utility.Utils;

/**
 * Created by Administrator on 5/23/2015.
 */
public class DocumentCreaterHelper {
    private static final int CAMERA_REQUEST = 101;
    private static final int SELECT_FILE = 102;
    private Uri imageUri;
    String filePath = null;

    public static void createDocument(Context context){

    }

    private void showFilePickerDialog(final Activity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Upload Report")
                .setIcon(0)
                .setCancelable(true)
                .setItems(R.array.image_picker_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            imageUri = Uri.fromFile(Utils.getImageUrlForImageSave("document"));
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            context.startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } else if (item == 1) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            context.startActivityForResult(galleryIntent, SELECT_FILE);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
