
package in.olivo.patientcare.main.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import in.olivo.patientcare.main.utility.Logger;

/**
 * Created by Satya Madala on 7/3/16.
 * email : satya.madala@olivo.in
 */

public class ResponseProvider extends ContentProvider {

    static final int RESPONSE = 100;
    static final int RESPONSE_WITH_URL_AND_USER_ID = 200;
    private static final String TAG = ResponseProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sResponseQueryBuilder;
    //url = ? AND user_id = ?
    private static final String sURLAndUserIdSelection =
            ResponseContract.ResponseEntry.COLUMN_URL + " = ? AND " +
                    ResponseContract.ResponseEntry.COLUMN_USER_ID + " = ? ";

    static {
        sResponseQueryBuilder = new SQLiteQueryBuilder();

        sResponseQueryBuilder.setTables(ResponseContract.ResponseEntry.TABLE_NAME);
    }

    private ResponseCacheDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ResponseContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ResponseContract.PATH_RESPONSE + "/*/*", RESPONSE_WITH_URL_AND_USER_ID);
        matcher.addURI(authority, ResponseContract.PATH_RESPONSE, RESPONSE);

        return matcher;
    }

    private Cursor getResponseByURLAndUserId(
            Uri uri, String[] projection, String sortOrder) {
        String url = ResponseContract.ResponseEntry.getURLFromUri(uri);
        String userId = ResponseContract.ResponseEntry.getUserIdFromUri(uri);

        Log.d(TAG, "querying data from response for url = " + url + " userId = " + userId);
        return sResponseQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sURLAndUserIdSelection,
                new String[]{url, userId},
                null,
                null,
                sortOrder
        );
    }

    /*
        Students: We've coded this for you.  We just create a new ResponseCacheDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new ResponseCacheDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case RESPONSE:
                return ResponseContract.ResponseEntry.CONTENT_TYPE;
            case RESPONSE_WITH_URL_AND_USER_ID:
                return ResponseContract.ResponseEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Log.d(TAG, "querying uri = " + uri);
        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)) {
            // "response/*/*
            case RESPONSE_WITH_URL_AND_USER_ID: {
                retCursor = getResponseByURLAndUserId(uri, projection, sortOrder);
                break;
            }
            default:
                Logger.e(TAG, "Unknown uri: " + uri);
                break;
        }
        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RESPONSE: {
                normalizeDate(values);
                long _id = db.insert(ResponseContract.ResponseEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    Log.d(TAG, "Successfully inserted into response " + values.toString());
                    returnUri = ResponseContract.ResponseEntry.buildResponseUri(_id);
                } else {
                    Log.e(TAG, "failed to insert to response " + values.toString());
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case RESPONSE:
                rowsDeleted = db.delete(
                        ResponseContract.ResponseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(ResponseContract.ResponseEntry.COLUMN_LAST_UPDATED_TIME)) {
            long dateValue = values.getAsLong(ResponseContract.ResponseEntry.COLUMN_LAST_UPDATED_TIME);
            values.put(ResponseContract.ResponseEntry.COLUMN_LAST_UPDATED_TIME, ResponseContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case RESPONSE:
                normalizeDate(values);
                rowsUpdated = db.update(ResponseContract.ResponseEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RESPONSE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(ResponseContract.ResponseEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}