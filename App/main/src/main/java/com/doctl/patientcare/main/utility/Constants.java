package com.doctl.patientcare.main.utility;

/**
 * Created by Administrator on 7/11/2014.
 */
public final class Constants {
//    TODO: Change this server URL to production server url
    public final static String SERVER_URL = "http://test.doctl.com";
//    TODO: Get this patient id from server and save to local storage.
    public final static String PATIENT_ID = "1496532e154f4b0db877278985762769";
    public final static String CARDS_URL = SERVER_URL + "/api/card/v1.0/"+ PATIENT_ID +"/cards/";
    public final static String PRESCRIPTION_URL = SERVER_URL + "/api/treatment/v1.0/" + PATIENT_ID + "/prescriptions/";
    public final static String PROGRESS_DATA_URL = SERVER_URL + "/api/treatment/v1.0/" + PATIENT_ID + "/progress/";
    public final static String VITAL_DETAIL_URL = SERVER_URL + "/api/treatment/v1.0/" + PATIENT_ID + "/vitals/";
}
