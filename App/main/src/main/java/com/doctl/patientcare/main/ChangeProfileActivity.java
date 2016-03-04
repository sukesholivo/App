package com.doctl.patientcare.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.HttpFileUpload;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 12/30/2014.
 */
public class ChangeProfileActivity extends ActionBarActivity {
    private static final String TAG = ChangeProfileActivity.class.getSimpleName();
    UserProfile userProfile;
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    FloatingActionButton fab;

    private static final int PICK_FROM_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_profile);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Change Profile");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        userProfile = Utils.getPatientDataFromSharedPreference(this);
        setData(userProfile);
        setImagePicker();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_save_profile:
                saveProfile();
//                Toast.makeText(this, "Profile saved", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.activityPaused();
    }

    private void setImagePicker(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setItems(R.array.image_picker_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mImageCaptureUri = Uri.fromFile(Utils.getImageUrlForImageSave());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    try {
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        Logger.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Crop.pickImage(ChangeProfileActivity.this);
                }
            }
        });

        final AlertDialog dialog = builder.create();
       // Button button = (Button) findViewById(R.id.btn_crop);
        mImageView = (ImageView) findViewById(R.id.photo);
        fab = (FloatingActionButton) this.findViewById(R.id.btn_crop);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.show();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case Crop.REQUEST_PICK:
                beginCrop(data.getData());
                break;
            case PICK_FROM_CAMERA:
                beginCrop(mImageCaptureUri);
                break;
            case Crop.REQUEST_CROP:
                handleCrop(data);
                break;
        }
    }

    private void handleCrop(Intent result) {
        Uri croppedImage = Crop.getOutput(result);
        mImageView.setImageURI(croppedImage);
        new SaveImage().execute(Constants.IMAGE_UPLOAD_URL, croppedImage.getPath());
    }

    private void beginCrop(Uri source) {
        Uri mImageCroppedUri = Uri.fromFile(Utils.getImageUrlForImageSave());
        new Crop(source).output(mImageCroppedUri).asSquare().start(this);
    }

    private void setData(UserProfile userProfile){
        final ImageView photoView = (ImageView) findViewById(R.id.photo);
        if (userProfile.getProfilePicUrl() != null && !userProfile.getProfilePicUrl().isEmpty()) {
            Picasso.with(this)
                    .load(Constants.SERVER_URL + userProfile.getProfilePicUrl())
                    .into(photoView);
        }
        final EditText nameText = (EditText) findViewById(R.id.name);
        nameText.setText(userProfile.getDisplayName());
        setNameListener(nameText);

        final EditText phoneText = (EditText) findViewById(R.id.phone);
        phoneText.setText(userProfile.getPhone());
        setPhoneListener(phoneText);

        final EditText dobText = (EditText) findViewById(R.id.dob);
        String dob = userProfile.getDob();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(dob);
        } catch (ParseException e){
            Logger.e(TAG, e.getMessage());
        }

        format = new SimpleDateFormat("dd-MM-yyyy");
        String date = "";
        if (newDate != null) {
            date = format.format(newDate);
        }
        dobText.setText(date);
        setDOBListener(dobText);

        final EditText sexText = (EditText) findViewById(R.id.sex);
        String sex = userProfile.getSex();
        if (sex.equals("m")){
            sexText.setText("Male");
        }
        if (sex.equals("f")){
            sexText.setText("Female");
        }
        setSexListener(sexText);
    }

    private void setNameListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Logger.d(TAG, charSequence.toString());
                userProfile.setDisplayName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setPhoneListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Logger.d(TAG, charSequence.toString());
                userProfile.setPhone(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setDOBListener(final EditText editText){
        String text = editText.getText().toString();
        String[] dates = text.split("/");
        int mDay = 1;
        int mMonth = 1;
        int mYear = 1980;
        if (dates.length == 3){
            mDay = Integer.getInteger(dates[0]);
            mMonth = Integer.getInteger(dates[1]);
            mYear = Integer.getInteger(dates[2]);
        }

        final DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    dpd.show();
                }
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Logger.d(TAG, charSequence.toString());
                Date newDate = null;
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    newDate = format.parse(charSequence.toString());
                } catch (ParseException e){
                    Logger.e(TAG, e.getMessage());
                }

                format = new SimpleDateFormat("yyyy-MM-dd");
                String date = "";
                if (newDate != null) {
                    date = format.format(newDate);
                }
                userProfile.setDob(date);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setSexListener(final EditText editText){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender")
                .setItems(R.array.sex_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        editText.setText(getResources().getStringArray(R.array.sex_array)[which]);
                    }
                });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    builder.show();
                }
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Logger.d(TAG, charSequence.toString());
                userProfile.setSex(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void saveProfile(){
        if (Utils.isNetworkAvailable(this)){
            String url = Constants.PERSONAL_DETAIL_URL;
            JSONObject data = null;
            try {
                data = userProfile.getDataToPatch();
                EditText password1 = (EditText) findViewById(R.id.password1);
                String p1 = password1.getText().toString();
                EditText password2 = (EditText) findViewById(R.id.password2);
                String p2 = password2.getText().toString();

                if (p1 != null && !p1.isEmpty()){
                    if (p2 == null || p2.isEmpty() || !p1.equals(p2)){
                        Toast.makeText(this, "Password doesn't match", Toast.LENGTH_LONG).show();
                        return;
                    }
                    data.put("password1", p1);
                    data.put("password2", p2);
                }
            } catch (JSONException e){
                Logger.e(TAG, e.getMessage());
            }
            new SaveProfile().execute(url, data);

        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    private class SaveProfile extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... arg0) {
            String url = (String)arg0[0];
            JSONObject data= (JSONObject)arg0[1];
            HTTPServiceHandler serviceHandler = new HTTPServiceHandler(ChangeProfileActivity.this);
            String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.PATCH, null, data);
            if (response != null && !response.isEmpty()) {
                userProfile = new Gson().fromJson(response, UserProfile.class);
                Utils.savePatientDataToSharedPreference(ChangeProfileActivity.this, userProfile);
                Utils.showToastOnUiThread(ChangeProfileActivity.this, "Profile saved");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class SaveImage extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String serverUrl = arg0[0];
            String imageUrl = arg0[1];
            uploadFile(serverUrl, imageUrl);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public void uploadFile(String serverUrl, String imageFile){
            try {
                FileInputStream fstrm = new FileInputStream(imageFile);
                HttpFileUpload hfu = new HttpFileUpload(ChangeProfileActivity.this, serverUrl);
                JSONObject response = hfu.Send_Now("profile_pic.jpg", fstrm);
                if( response == null) throw new Exception("Error in uploading image");
                String profilePicUrl = response.getString("profilePicUrl");
                userProfile.setProfilePicUrl(profilePicUrl);
                Utils.savePatientDataToSharedPreference(ChangeProfileActivity.this, userProfile);

            } catch (Exception e){
                Logger.e(TAG, e.getMessage());
            }

        }
    }
}