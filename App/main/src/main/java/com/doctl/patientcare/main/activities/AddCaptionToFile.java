package com.doctl.patientcare.main.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class AddCaptionToFile extends BaseActivity implements AdapterView.OnItemSelectedListener {

    Uri imageUri;
    List<String> categories;
    String docCategory;
    //String categoryPopupContents[];
//    PopupWindow popupWindow;
    Button categoryButton;
    ImageView imagePreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caption_to_file);

        ActionBar actionBar= getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Send Photo");
            actionBar.setSubtitle("user name");
        }

        categories = new ArrayList<>();
        docCategory = "Other"; // default value
        categories.add("Other");
        categories.add("Prescription");
        categories.add("Report");
        categories.add("Bill");
        //categoryPopupContents = new String[categories.size()];
        //categories.toArray(categoryPopupContents);

        imageUri = getIntent().getData();
        imagePreview=(ImageView)findViewById(R.id.imagePreview);

        //ViewTreeObserver vto = imagePreview.getViewTreeObserver();
        //vto.addOnPreDrawListener(new ImageUtils.LoadOnPreDraw(this, imagePreview, imageUri));
        ImageUtils.loadImage(imagePreview, this, imageUri);
//        imagePreview.setImageURI(imageUri);


        categoryButton = (Button)findViewById(R.id.select_category);

       // popupWindow = popupWindowCategory();
        categoryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // popupWindow.showAsDropDown(v, 0, categories.size());
                final CharSequence colors[] = new CharSequence[categories.size()];
                for (int j = 0; j < categories.size(); j++) {
                    colors[j] = categories.get(j);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AddCaptionToFile.this);
                builder.setTitle("Pick Category");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryButton.setText(colors[which]);
                        docCategory = (String) colors[which];
                    }
                });
                builder.show();
            }
        });

        /*Spinner spinner=(Spinner)findViewById(R.id.doc_type);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);*/
        Button sendButton= (Button) findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.setData(imageUri);
                EditText captionView = (EditText) findViewById(R.id.caption);
                data.putExtra(Constants.CAPTION, captionView.getText().toString());
                data.putExtra(Constants.DOC_CATEGORY, docCategory);
                finishActivity(data, RESULT_OK);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity(null, RESULT_CANCELED);
            }
        });
    }

    private void finishActivity(Intent data, int result) {
        if (getParent() == null) {
            setResult(result, data);
        } else {
            getParent().setResult(result, data);
        }
        finish();
    }

   /* private PopupWindow popupWindowCategory() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        // the drop down list is a list view
        ListView listViewCategory = new ListView(this);

        //listViewCategory.setBackgroundColor(categoryButton.getShadowColor());

        // set our adapter and pass our pop up window contents
        listViewCategory.setAdapter(categoryAdapter(categoryPopupContents));

        // set the item click listener
        listViewCategory.setOnItemClickListener(new CategoryDropdownOnItemClickListener());

        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(350);

        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        popupWindow.setContentView(listViewCategory);

        return popupWindow;
    }

    private ArrayAdapter<String> categoryAdapter(String dogsArray[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dogsArray) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // setting the ID and text for every items in the list
                String text= getItem(position);

                // visual settings for the list item
                TextView listItem = new TextView(AddCaptionToFile.this);

                listItem.setText(text);
                listItem.setTextSize(22);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.BLACK);
                return listItem;
            }
        };

        return adapter;
    }

    private static class CategoryDropdownOnItemClickListener implements AdapterView.OnItemClickListener {

        private static final String TAG = CategoryDropdownOnItemClickListener.class.getSimpleName();

        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

            // get the context and main activity to access variables
            Context mContext = v.getContext();
            AddCaptionToFile addCaptionToFile = ((AddCaptionToFile) mContext);

            // add some animation when a list item was clicked
            Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
            fadeInAnimation.setDuration(10);
            v.startAnimation(fadeInAnimation);

            // dismiss the pop up
            addCaptionToFile.popupWindow.dismiss();

            // get the text and set it as the button text
            String selectedItemText = ((TextView) v).getText().toString();
            addCaptionToFile.categoryButton.setText(selectedItemText);
            addCaptionToFile.docCategory = selectedItemText;

        }
    }*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = categories.get(position);
        docCategory = item;
        // Showing selected spinner item
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
