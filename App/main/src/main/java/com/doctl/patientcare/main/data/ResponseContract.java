
package com.doctl.patientcare.main.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Satya Madala on 7/3/16.
 * email : satya.madala@olivo.in
 */
public class ResponseContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.doctl.patientcare.main";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.doctl.patientcare.main/response/ is a valid path for
    // looking at response data. content://com.doctl.patientcare.main/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_RESPONSE = "response";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the response table */
    public static final class ResponseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESPONSE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESPONSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESPONSE;

        public static final String TABLE_NAME = "response";


        // last_updated_time stored as long in milliseconds since the epoch
        public static final String COLUMN_LAST_UPDATED_TIME = "last_updated_time";

        // url stored as string
        public static final String COLUMN_URL = "url";

        // response json stored as String.
        public static final String COLUMN_RESPONSE = "response";

        // user_id stored as String
        public static final String COLUMN_USER_ID = "user_id";


        public static Uri buildResponseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildResponseURLWithUserId(String url, String userId) {
            return CONTENT_URI.buildUpon().appendPath(url)
                    .appendPath(userId).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static String getURLFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getUserIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_LAST_UPDATED_TIME);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }
}
