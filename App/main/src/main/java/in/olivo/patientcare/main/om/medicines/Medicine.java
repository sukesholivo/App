package in.olivo.patientcare.main.om.medicines;


import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 6/12/2014.
 */
public class Medicine {
    static final SparseArray<MedicineType> MedicineMap = new SparseArray<MedicineType>() {{
        put(1, MedicineType.TABLET);
        put(2, MedicineType.CAPSULE);
        put(3, MedicineType.INJECTION);
        put(4, MedicineType.SYRUP);
        put(5, MedicineType.DROPS);
    }};
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("type")
//    private MedicineType type;
    private int type;
    @SerializedName("dosage")
    private String dosage;
    @SerializedName("unit")
    private String unit;
    @SerializedName("instruction")
    private String instruction;
    @SerializedName("showInfoIcon")
    private boolean showInfoIcon;
    private String timestamp;
    private int state;
    private int quantityToTake[];
    private String medicineInstruction[];

    //region getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    //    public MedicineType getType() {
    public int getType() {
        return type;
    }

    public String getDosage() {
        return dosage;
    }

    public String getUnit() {
        return unit;
    }

    public String getInstruction() {
        return instruction;
    }

    public boolean isShowInfoIcon() {
        return showInfoIcon;
    }

    public int[] getQuantityToTake() {
        return quantityToTake;
    }

    public String[] getMedicineInstruction() {
        return medicineInstruction;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    //endregion

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("id", this.getId());
        data.put("timeStamp", this.getTimestamp());
        data.put("state", this.getState());
        return data;
    }

    public enum MedicineType {
        TABLET,     //1
        CAPSULE,    //2
        INJECTION,  //3
        SYRUP,      //4
        DROPS,      //5
        OTHER;      //6

        public static MedicineType fromInteger(int x) {
            switch (x) {
                case 1:
                    return TABLET;
                case 2:
                    return CAPSULE;
                case 3:
                    return INJECTION;
                case 4:
                    return SYRUP;
                case 5:
                    return DROPS;
                case 100:
                    return OTHER;
            }
            return null;
        }
    }

    public enum MedicineTakenState {
        TAKEN,     //1
        SNOOZED,    //2
        DISMISSED;  //3

        public static int getInteger(MedicineTakenState state) {
            switch (state) {
                case TAKEN:
                    return 1;
                case SNOOZED:
                    return 2;
                case DISMISSED:
                    return 3;
            }
            return 0;
        }
    }
}
