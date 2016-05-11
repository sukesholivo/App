package in.olivo.patientcare.main.measure.om;

/**
 * Created by Satya Madala on 11/5/16.
 * email : satya.madala@olivo.in
 */
public class MeasureSchema {
    /**
     * category : Vital
     * description :
     * valuesSchema : {"units": "mg/dL", "data_type": "float"}
     * classificationSchema : {"max_value": 160, "min_value": 120}
     * id : 1000
     * name : Blood Sugar
     */

    private String category;
    private String description;
    private String valuesSchema;
    private String classificationSchema;
    private int id;
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

    public void setClassificationSchema(String classificationSchema) {
        this.classificationSchema = classificationSchema;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getClassificationSchema() {
        return classificationSchema;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
