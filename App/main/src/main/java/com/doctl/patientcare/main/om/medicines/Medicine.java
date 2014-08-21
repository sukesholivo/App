package com.doctl.patientcare.main.om.medicines;


import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 6/12/2014.
 */
public class Medicine {
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

    //endregion

    public enum MedicineType{
        CAPSULE,
        TABLET,
        INJECTION,
        SYRUP,
        DROPS
    }

    static final SparseArray<String> MedicineMap = new SparseArray<String>() {{
        put(1, MedicineType.TABLET.toString());
        put(2, MedicineType.CAPSULE.toString());
        put(3, MedicineType.INJECTION.toString());
        put(4, MedicineType.SYRUP.toString());
        put(5, MedicineType.DROPS.toString());
    }};
}
