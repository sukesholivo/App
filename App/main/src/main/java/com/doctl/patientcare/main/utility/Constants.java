package com.doctl.patientcare.main.utility;

/**
 * Created by Administrator on 7/11/2014.
 */
public final class Constants {
//    TODO: Change this server URL to production server url
    public final static String SERVER_URL = "http://test.doctl.com";
    public final static String API_VERSION = "v1.0";
//    TODO: Get this patient id from server and save to local storage.
    public final static String PATIENT_ID = "1496532e154f4b0db877278985762769";
    public final static String CARDS_URL = SERVER_URL + "/api/card/" + API_VERSION + "/"+ PATIENT_ID +"/cards/";
    public final static String PRESCRIPTION_URL = SERVER_URL + "/api/treatment/" + API_VERSION + "/prescriptions/";
    public final static String PROGRESS_DATA_URL = SERVER_URL + "/api/treatment/" + API_VERSION + "/progress/";
    public final static String VITAL_DETAIL_URL = SERVER_URL + "/api/treatment/" + API_VERSION + "/vitals/";
    public final static String PERSONAL_DETAIL_URL = SERVER_URL + "/api/account/" + API_VERSION + "/personal/";

    public final static String PERSONAL_DETAIL_SHARED_PREFERENCE_NAME= "personal_detail";
    public final static String AUTH_SHARED_PREFERENCE_NAME= "auth_prefs";
    public final static String AUTH_SHARED_PREFERENCE_KEY= "serveraccesstoken";
}
