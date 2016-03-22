package in.olivo.patientcare.main;

import android.support.v7.app.ActionBarActivity;

/**
 * Created by Administrator on 4/10/2015.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.activityPaused();
    }
}
