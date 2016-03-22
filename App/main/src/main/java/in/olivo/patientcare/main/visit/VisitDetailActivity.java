package in.olivo.patientcare.main.visit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import in.olivo.patientcare.main.BaseActivity;
import in.olivo.patientcare.main.R;

public class VisitDetailActivity extends BaseActivity {

    public final static String TAG = VisitDetailActivity.class.getSimpleName();
    private WebView visitDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_details);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Visit Details");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        visitDetails = (WebView) findViewById(R.id.visit_details);
//        visitDetails.loadDataWithBaseURL(visitUri, null, "text/html", HTTP.UTF_8, null);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String visitUri = getIntent().getStringExtra(VisitListActivity.VISIT_URI);
        visitDetails.loadUrl(visitUri);
    }

    /*  @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, VisitListActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, VisitListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            // Something else
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
