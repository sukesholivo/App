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

    private List<UserProfile> patients=new ArrayList<>();

    public List<UserProfile> getLabs() {
        return labs;
    }

    public void setLabs(List<UserProfile> labs) {
        this.labs = labs;
    }

    public List<UserProfile> getPharmacies() {
        return pharmacies;
    }

    public void setPharmacies(List<UserProfile> pharmacies) {
        this.pharmacies = pharmacies;
    }

    public List<UserProfile> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<UserProfile> doctors) {
        this.doctors = doctors;
    }

    public List<UserProfile> getPatients() {
        return patients;
    }

    public void setPatients(List<UserProfile> patients) {
        this.patients = patients;
    }

    @Override
    public String toString() {
        return "ContactData{" +
                "labs=" + labs +
                ", pharmacies=" + pharmacies +
                ", doctors=" + doctors +
                ", patients=" + patients +
                '}';
    }

    public List<UserProfile> getAllUserProfiles() {

        List<UserProfile> allUserProfiles = new ArrayList<>();
        addIfNotNull(allUserProfiles, getDoctors());
        addIfNotNull(allUserProfiles, getPatients());
        addIfNotNull(allUserProfiles, getPharmacies());
        addIfNotNull(allUserProfiles, getLabs());
        return allUserProfiles;
    }

    private void addIfNotNull(List<UserProfile> to, List<UserProfile> userProfiles) {
        if (userProfiles != null) {
            to.addAll(userProfiles);
        }
    }

    public Map<Integer, String> getListPositions() {
        Map<Integer, String> categoryPositions = new HashMap<>();
        int len = 0;
        categoryPositions.put(len, "Doctors");
        if (doctors != null) {
            len += doctors.size();
        }
        categoryPositions.put(len, "Patients");
        if (patients != null) {
            len += patients.size();
        }
        categoryPositions.put(len, "Pharmacy Store");
        if (pharmacies != null) {
            len += pharmacies.size();
        }
        categoryPositions.put(len, "Labs");
        return categoryPositions;
    }
}
