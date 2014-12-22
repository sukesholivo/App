package com.doctl.patientcare.main.om;

import com.google.gson.annotations.SerializedName;

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
}
