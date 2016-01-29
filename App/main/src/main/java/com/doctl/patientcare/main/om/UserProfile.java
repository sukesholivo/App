package com.doctl.patientcare.main.om;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/28/2014.
 */
public class UserProfile {

    public UserProfile(String id, String displayName, String profilePicUrl, String email, String phone, String dob, String sex, Address address, String role){
        this.id = id;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.sex = sex;
        this.address = address;
        this.role = role;
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
    private Address address;

    @SerializedName("role")
    private String role;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public Address getAddress() {
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

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAbout(){
        //TODO get depends on type
        if(getAddress() == null){
            return null;
        }
        return getAddress().getStreet();
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dob='" + dob + '\'' +
                ", sex='" + sex + '\'' +
                ", address=" + address +
                ", role='" + role + '\'' +
                '}';
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("displayName", this.getDisplayName());
        data.put("phone", this.getPhone());
        if (this.getDob() != null && !this.getDob().isEmpty()) {
            data.put("dob", this.getDob());
        }
        if (this.getSex() != null && !this.getSex().isEmpty()) {
            data.put("sex", this.getSex());
        }
        return data;
    }

    public class Address {
        @SerializedName("street")
        public String street;

        @SerializedName("landmark")
        public String landmark;

        @SerializedName("city")
        public String city;

        @SerializedName("state")
        public String state;

        @SerializedName("country")
        public String country;

        @SerializedName("pin")
        public String pin;

        @SerializedName("longitude")
        public String longitude;

        @SerializedName("latitude")
        public String latitude;

        public String getStreet() {
            return street;
        }

        public String getLandmark() {
            return landmark;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getCountry() {
            return country;
        }

        public String getPin() {
            return pin;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getPrintableAddress(){
            return this.getStreet() + this.getCity();
        }

        @Override
        public String toString() {
            return "Address{" +
                    "street='" + street + '\'' +
                    ", landmark='" + landmark + '\'' +
                    ", city='" + city + '\'' +
                    ", state='" + state + '\'' +
                    ", country='" + country + '\'' +
                    ", pin='" + pin + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", latitude='" + latitude + '\'' +
                    '}';
        }
    }
}
