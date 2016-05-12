package in.olivo.patientcare.main.measure.om;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.utility.Utils;

/**
 * Created by Satya Madala on 11/5/16.
 * email : satya.madala@olivo.in
 */
public class MeasureSchema {
    /**
     * category : Vital
     * description :
     * valuesSchema : {"units": "mg/dL", "data_type": "float"}
     * classificationSchemas : {"max_value": 160, "min_value": 120}
     * id : 1000
     * name : Blood Sugar
     */

    private String category;
    private String description;
    private String valuesSchema;
    private String classificationSchemas;
    private Long id;
    private String name;

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValuesSchema(String valuesSchema) {
        this.valuesSchema = valuesSchema;
    }

    public void setClassificationSchemas(String classificationSchemas) {
        this.classificationSchemas = classificationSchemas;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getValuesSchema() {
        return valuesSchema;
    }

    public String getClassificationSchemas() {
        return classificationSchemas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MeasureSchema{");
        sb.append("category='").append(category).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", valuesSchema='").append(valuesSchema).append('\'');
        sb.append(", classificationSchemas='").append(classificationSchemas).append('\'');
        sb.append(", id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private static Map<Long, MeasureSchema> measureSchemaMap;
    private static List<MeasureSchema> measureSchemas;
    public static List<MeasureSchema> getMeasureSchemas(Context context){
        getDefaultMeasuresMap(context);
        return measureSchemas;
    }
    public static Map<Long, MeasureSchema> getDefaultMeasuresMap(Context context){

        if(measureSchemaMap != null) return measureSchemaMap;

        String defaultMeasureSchemas = Utils.parsonJsonFromFile(context, R.raw.default_measure_schemas);
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        MeasureSchema[] measureSchemaArray = gson.fromJson(defaultMeasureSchemas, MeasureSchema[].class);
        measureSchemas = Arrays.asList(measureSchemaArray);
        measureSchemaMap= new HashMap<>();
        for(MeasureSchema measureSchema:measureSchemas){
            measureSchemaMap.put(measureSchema.getId(), measureSchema);
        }
        return measureSchemaMap;
    }
}
