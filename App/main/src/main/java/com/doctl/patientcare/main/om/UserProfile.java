package com.doctl.patientcare.main.om;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/28/2014.
 */
public class UserProfile {

    public UserProfile(String id, String displayName, String profilePicUrl, String email, String phone, String dob, String sex, String address){
        this.id = id;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.sex = sex;
        this.address = address;
    }
    @SerializedName("id")
    private String id;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("profilePicUrl")
    private String profilePicUrl;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("dob")
    private String dob;

    @SerializedName("sex")
    private String sex;

    @SerializedName("address")
    private String address;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDob() {
        return dob;
    }

    public String getSex() {
        return sex;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("displayName", this.getDisplayName());
        data.put("phone", this.getPhone());
        data.put("dob", this.getDob());
        data.put("sex", this.getSex());
        return data;
    }
}
