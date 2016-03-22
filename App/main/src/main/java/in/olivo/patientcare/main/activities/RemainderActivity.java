package in.olivo.patientcare.main.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import in.olivo.patientcare.main.BaseActivity;
import in.olivo.patientcare.main.R;

public class RemainderActivity extends BaseActivity {

    Calendar myCalendar = Calendar.getInstance();
    private EditText date;
    DatePickerDialog.OnDateSetListener dateD = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    private EditText time;
    TimePicker.OnTimeChangedListener myOnTimeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            time.setText(hourOfDay + ":" + minute);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder);
        date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);

        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(RemainderActivity.this);

                dialog.setContentView(R.layout.date_time_picker);
                dialog.setTitle("Custom Dialog");

                TimePicker tp = (TimePicker) dialog.findViewById(R.id.time_picker);
                tp.setOnTimeChangedListener(myOnTimeChangedListener);
                DatePicker dp = (DatePicker) dialog.findViewById(R.id.date_picker);

                /*new DatePickerDialog(RemainderActivity.this, dateD, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
            }
        });

    }

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        date.setText(sdf.format(myCalendar.getTime()));
    }

}
