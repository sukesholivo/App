package com.doctl.patientcare.main.om.contact;

import com.doctl.patientcare.main.om.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by satya on 26/1/16.
 */
public class ContactData {


    //TODO make one user profile list instead three as role is present in user profile
    private List<UserProfile> labs;
    private List<UserProfile> pharmacies;

    private List<UserProfile> doctors;

    public void setLabs(List<UserProfile> labs) {
        this.labs = labs;
    }

    public void setPharmacies(List<UserProfile> pharmacies) {
        this.pharmacies = pharmacies;
    }

    public void setDoctors(List<UserProfile> doctors) {
        this.doctors = doctors;
    }

    public List<UserProfile> getLabs() {
        return labs;
    }

    public List<UserProfile> getPharmacies() {
        return pharmacies;
    }

    public List<UserProfile> getDoctors() {
        return doctors;
    }

    @Override
    public String toString() {
        return "ContactData{" +
                "labs=" + labs +
                ", pharmacies=" + pharmacies +
                ", doctors=" + doctors +
                '}';
    }

    public List<UserProfile> getAllUserProfiles(){

        List<UserProfile> allUserProfiles = new ArrayList<>();
        allUserProfiles.addAll(getDoctors());
        allUserProfiles.addAll(getPharmacies());
        allUserProfiles.addAll(getLabs());
        return allUserProfiles;
    }

    public Map<Integer, String> getListPositions(){
        Map<Integer, String> categoryPositions = new HashMap<>();
        int len=0;
        categoryPositions.put(len, "Doctors");
        if( doctors != null ){
            len+=doctors.size();
        }
        categoryPositions.put(len, "Pharmacy Store");
        if( pharmacies != null){
            len+=pharmacies.size();
        }
        categoryPositions.put(len, "Labs");
        return categoryPositions;
    }
}
