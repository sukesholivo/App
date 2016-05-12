package in.olivo.patientcare.main.measure.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.measure.om.MeasureSchema;

public class MeasureSchemaListActivity extends ActionBarActivity {

    ListView listView;
    List<MeasureSchema> measureSchemas;
    MeasureSchemaListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_schema_list);
        measureSchemas = MeasureSchema.getMeasureSchemas(this);
        listView = (ListView) findViewById(R.id.measure_list);
        listAdapter = new MeasureSchemaListAdapter(this, measureSchemas);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long measureSchemaId = ((MeasureSchema)listView.getItemAtPosition(position)).getId();
//                Intent intent = new Intent(MeasureSchemaListActivity.this, );

            }
        });
    }

}
