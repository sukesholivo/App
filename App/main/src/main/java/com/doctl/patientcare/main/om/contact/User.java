package com.doctl.patientcare.main.om.contact;

import com.doctl.patientcare.main.utility.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satya on 20/1/16.
 */
public class User {

    enum UserType{
        DOCTOR, PATIENT, LAB, MEDICAL_STORE
    }
    private String id;

    private UserType userType;

    private String name;

    private String profilePicUrl;

    public User(String id, UserType userType, String name, String profilePicUrl) {
        this.id = id;
        this.userType = userType;
        this.name = name;
        this.profilePicUrl = profilePicUrl;
    }

    public String getId() {
        return id;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public static List<User> getDummyUsers(){

        String defaultProfilePicURL = "https://www.test.doctl.com/" + "/static/files/uploaded_files/1432672583_34_Akansha.jpg";
        List<User> users =new ArrayList<>();
        for(int i=0;i<10;i++) {
            int ordinal =(int) Math.random() % 4;
            User user = new User(""+i, UserType.values()[ordinal], "name"+i, defaultProfilePicURL);
            users.add(user);
        }
        return users;
    }
}
