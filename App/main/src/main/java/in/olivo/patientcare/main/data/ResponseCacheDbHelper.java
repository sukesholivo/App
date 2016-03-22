
package in.olivo.patientcare.main.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.olivo.patientcare.main.data.ResponseContract.ResponseEntry;

/**
 * Created by Satya Madala on 7/3/16.
 * email : satya.madala@olivo.in
 */
public class ResponseCacheDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "offline.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public ResponseCacheDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_RESPONSE_TABLE = "CREATE TABLE " + ResponseEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.
                ResponseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ResponseEntry.COLUMN_LAST_UPDATED_TIME + " INTEGER NOT NULL, " +
                ResponseEntry.COLUMN_RESPONSE + " TEXT NOT NULL, " +
                ResponseEntry.COLUMN_URL + " TEXT NOT NULL," +
                ResponseEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +


                // To assure the application have just one response entry per url
                // per user_id, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + ResponseEntry.COLUMN_USER_ID + ", " +
                ResponseEntry.COLUMN_URL + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_RESPONSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next line
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ResponseEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
