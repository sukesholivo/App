package com.doctl.patientcare.main.visit.om;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Satya Madala on 5/3/16.
 * email : satya.madala@olivo.in
 */
public class Visit {


    /**
     * date : 2016-03-07T15:57:30
     * uri : http://test.doctl.com/teledos/v1.0/telidos-oncology/visits/10566957939208/visit/4f74764f-a7a5-4f98-a6f7-03c36ede05b3/
     * clinicName : Manas Maheshwari Clinic
     * doctorName : Dr.Manas Maheshwari
     */
    @SerializedName("visit_date")
    private Date date;

    @SerializedName("visit_uri")
    private String uri;

    @SerializedName("clinic_name")
    private String clinicName;

    @SerializedName("doctor_name")
    private String doctorName;

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Date getDate() {
        if(date == null){
            date = new Date();
        }
        return date;
    }

    public String getUri() {
        return uri;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getDoctorName() {
        return doctorName;
    }
}

