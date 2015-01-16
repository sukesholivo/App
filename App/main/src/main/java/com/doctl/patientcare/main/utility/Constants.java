package com.doctl.patientcare.main.utility;

/**
 * Created by Administrator on 7/11/2014.
 */
public final class Constants {
//    TODO: Change this server URL to production server url
    public final static String SERVER_URL = "https://www.demo.doctl.com";
    public final static String API_VERSION = "v1.0";
//    TODO: Get this patient id from server and save to local storage.
    public final static String CARDS_URL = SERVER_URL + "/api/card/" + API_VERSION + "/cards/";
    public final static String PRESCRIPTION_URL = SERVER_URL + "/api/treatment/" + API_VERSION + "/prescriptions/";
    public final static String PROGRESS_DATA_URL = SERVER_URL + "/api/treatment/" + API_VERSION + "/progress/";
    public final static String VITAL_DETAIL_URL = SERVER_URL + "/api/treatment/" + API_VERSION + "/vitals/";
    public final static String PERSONAL_DETAIL_URL = SERVER_URL + "/api/account/" + API_VERSION + "/personal/";
    public final static String IMAGE_UPLOAD_URL = SERVER_URL + "/api/account/" + API_VERSION + "/image/";
    public final static String LOGIN_URL = SERVER_URL + "/api/account/" + API_VERSION + "/login/";
    public final static String REGISTER_URL = SERVER_URL + "/api/account/" + API_VERSION + "/register/";
    public final static String GCM_REGISTER_URL = Constants.SERVER_URL+"/gcm/register/";

    public final static String PERSONAL_DETAIL_SHARED_PREFERENCE_NAME= "personal_detail";
    public final static String AUTH_SHARED_PREFERENCE_NAME= "auth_prefs";
    public final static String AUTH_SHARED_PREFERENCE_KEY= "serveraccesstoken";

    public final static String GCM_SHARED_PREFERENCE_KEY = "gcm_appdata";
    public final static String PROPERTY_GCM_REGISTRATION_ID = "gcm_registration_id";
    public final static String PROPERTY_APP_VERSION = "app_version";
    public final static String SENDER_ID = "258383232963";

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
}
