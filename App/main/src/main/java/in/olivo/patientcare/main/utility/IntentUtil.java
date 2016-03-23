package in.olivo.patientcare.main.utility;

import android.content.Intent;

/**
 * Created by Satya Madala on 24/2/16.
 * email : satya.madala@Olivo.in
 */
public class IntentUtil {

    public static Intent getPickImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }
}
