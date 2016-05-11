package in.olivo.patientcare.main.measure.om;

/**
 * Created by Satya Madala on 11/5/16.
 * email : satya.madala@olivo.in
 */
public class Measure {


    /**
     * category : Urine Chemistry
     * description :
     * valuesSchema : {"units": "umol/24hrs", "data_type": "float"}
     * classificationSchema : {"gender": {"male": [{"max_age": 50.0, "max_value": 76.0, "min_value": 28.0, "min_age": 19.0}, {"max_age": 50.0, "max_value": 52.0, "min_value": 21.0, "min_age": 19.0}, {"max_value": 63.0, "min_value": 17.0}, {"max_value": 31.0, "min_value": 10.0}], "female": [{"max_age": 50.0, "max_value": 76.0, "min_value": 28.0, "min_age": 19.0}, {"max_age": 50.0, "max_value": 52.0, "min_value": 21.0, "min_age": 19.0}, {"max_value": 63.0, "min_value": 17.0}, {"max_value": 31.0, "min_value": 10.0}]}}
     * id : 195
     * name : 17 Oxo-steroids
     */

    private MeasureSchema measureSchema;
    /**
     * measureSchema : {"category":"Urine Chemistry","description":"","valuesSchema":"{\"units\": \"umol/24hrs\", \"data_type\": \"float\"}","classificationSchema":"{\"gender\": {\"male\": [{\"max_age\": 50.0, \"max_value\": 76.0, \"min_value\": 28.0, \"min_age\": 19.0}, {\"max_age\": 50.0, \"max_value\": 52.0, \"min_value\": 21.0, \"min_age\": 19.0}, {\"max_value\": 63.0, \"min_value\": 17.0}, {\"max_value\": 31.0, \"min_value\": 10.0}], \"female\": [{\"max_age\": 50.0, \"max_value\": 76.0, \"min_value\": 28.0, \"min_age\": 19.0}, {\"max_age\": 50.0, \"max_value\": 52.0, \"min_value\": 21.0, \"min_age\": 19.0}, {\"max_value\": 63.0, \"min_value\": 17.0}, {\"max_value\": 31.0, \"min_value\": 10.0}]}}","id":195,"name":"17 Oxo-steroids"}
     * lastUpdatedTime : 2016-05-10T12:36:31Z
     * measuredTime : 2016-05-10T12:36:31Z
     * values : {"value": 100}
     * classifications : High
     * id : 23
     */

    private String lastUpdatedTime;
    private String measuredTime;
    private String values;
    private String classifications;
    private Long id;

    public void setMeasureSchema(MeasureSchema measureSchema) {
        this.measureSchema = measureSchema;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void setMeasuredTime(String measuredTime) {
        this.measuredTime = measuredTime;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public void setClassifications(String classifications) {
        this.classifications = classifications;
    }

    public MeasureSchema getMeasureSchema() {
        return measureSchema;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public String getMeasuredTime() {
        return measuredTime;
    }

    public String getValues() {
        return values;
    }

    public String getClassifications() {
        return classifications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
