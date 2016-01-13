package com.doctl.patientcare.main.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.doctl.patientcare.main.BaseActivityWithNavigation;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.documents.Document;
import com.doctl.patientcare.main.om.documents.DocumentAdapter;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.HttpFileUpload;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.github.clans.fab.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by Administrator on 4/27/2015.
 */
public class DocumentsActivity extends BaseActivityWithNavigation {
    private static final String TAG = DocumentsActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST = 101;
    private static final int SELECT_FILE = 102;
    private Uri imageUri;
    String filePath = null;
    DocumentAdapter mDocumentAdapter;
    ListView mDocumentListView;
    ArrayList<Document> mDocumentArrayList;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        this.setupNavigationDrawer();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Reports");
        }
        mDocumentArrayList = new ArrayList<>();
        mDocumentAdapter = new DocumentAdapter(DocumentsActivity.this, mDocumentArrayList);
        mDocumentListView = (ListView)findViewById(R.id.documents_list);
        mDocumentListView.setAdapter(mDocumentAdapter);
        fab = (FloatingActionButton) this.findViewById(R.id.button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilePickerDialog();
            }
        });
        refresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                filePath = imageUri.getPath();
                bitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
                showFilePreviewDialog(bitmap);
            } else if (requestCode == SELECT_FILE) {
                selectedImageUri = data.getData();
                filePath = Utils.getRealPathFromURI(this, selectedImageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    showFilePreviewDialog(bitmap);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void showFilePickerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Upload Report")
                .setIcon(0)
                .setCancelable(true)
                .setItems(R.array.image_picker_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            imageUri = Uri.fromFile(Utils.getImageUrlForImageSave("document"));
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } else if (item == 1) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, SELECT_FILE);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFilePreviewDialog(Bitmap bitmap){
        View view = getLayoutInflater().inflate(R.layout.dialog_file_upload_preview, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.file_preview);
        imageView.setImageBitmap(bitmap);
        final EditText titleEditText = (EditText ) view.findViewById(R.id.file_title);
        final EditText descriptionEditText = (EditText) view.findViewById(R.id.file_description);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(0)
                .setCancelable(true)
                .setView(view)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String title = titleEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();
                        if (title.isEmpty()) {
                            Toast.makeText(DocumentsActivity.this, "Title is required", Toast.LENGTH_SHORT).show();
                        } else {

                            Logger.e("", filePath);
                            new SaveDocuments().execute(Constants.DOCUMENTS_URL, filePath, title, description);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.d(TAG, "OK CLICKED");
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void refresh(){
        if (Utils.isNetworkAvailable(this)){
            new GetDocuments().execute();
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    protected void refreshDocuments() {
        String data = downloadDocumentsData();
        if (data != null && !data.isEmpty()) {
            ArrayList<Document> documents = parseDocumentsData(data);
            Logger.e("", "Total documents: " + documents.size());
            if (documents.size() == 0) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUploadLayout();
                    }
                });
            }
            resetDocuments(documents);
        }
    }

    private String downloadDocumentsData(){
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(Constants.DOCUMENTS_URL, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private ArrayList<Document> parseDocumentsData(String jsonStr){
        final Document[] documents = new Gson().fromJson(jsonStr, Document[].class);
        ArrayList<Document> documentArrayList = new ArrayList<>();
        for (final Document doc: documents){
            documentArrayList.add(doc);
        }
        return documentArrayList;
    }

    private void showUploadLayout(){
        Card uploadCard = new Card(this,R.layout.card_inner_content_add_document);
        CardView uploadCardView =  (CardView)this.findViewById(R.id.upload_layout);
        uploadCardView.setVisibility(View.VISIBLE);
        uploadCardView.setCard(uploadCard);
        Button uploadButton = (Button) this.findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePickerDialog();
            }
        });
    }

    private void hideUploadLayout(){
        CardView uploadCardView =  (CardView)this.findViewById(R.id.upload_layout);
        uploadCardView.setVisibility(View.GONE);
    }

    private void showAddedDocument(final String jsonStr){

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideUploadLayout();
                Document document = new Gson().fromJson(jsonStr, Document.class);
                mDocumentAdapter.add(document);
                mDocumentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void removeDeletedDocument(Document document){
        mDocumentAdapter.remove(document);
        mDocumentAdapter.notifyDataSetChanged();
        new DeleteDocument().execute(Constants.DOCUMENTS_URL + document.getId());
    }

    private void resetDocuments(final ArrayList<Document> documentsArrayList){
        this.runOnUiThread(new Runnable() {
            public void run() {
                mDocumentAdapter.clear();
                mDocumentAdapter.addAll(documentsArrayList);
                mDocumentAdapter.notifyDataSetChanged();
            }
        });
    }

    private class GetDocuments extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            refreshDocuments();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class SaveDocuments extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String serverUrl = arg0[0];
            String imageUrl = arg0[1];
            String title = arg0[2];
            String description = arg0[3];
            uploadFile(serverUrl, imageUrl, title, description);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public void uploadFile(String serverUrl, String imageFile, String title, String description){
            try {
                FileInputStream fstrm = new FileInputStream(imageFile);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("title", title));
                nameValuePairs.add(new BasicNameValuePair("description", description));
                HttpFileUpload hfu = new HttpFileUpload(DocumentsActivity.this, serverUrl);
                JSONObject response = hfu.Send_Now("document.jpg", fstrm, nameValuePairs);
                if( response == null) throw new Exception("Error in uploading image");
                String docURL = response.getString("profilePicUrl");
                showAddedDocument(docURL);
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
        }
    }

    private class DeleteDocument extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String serverUrl = arg0[0];
            HTTPServiceHandler serviceHandler = new HTTPServiceHandler(DocumentsActivity.this);
            Logger.e("", "serverUrl: " + serverUrl);
                    serviceHandler.makeServiceCall(serverUrl, HTTPServiceHandler.HTTPMethod.DELETE, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
