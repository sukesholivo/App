package com.doctl.patientcare.main.om.medicines;


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
    private MedicineType type;

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

    public Medicine(String id, String name, int quantity, MedicineType type,
                    String dosage, String unit, String instruction, boolean showInfoIcon){
        this(id, name, quantity, type, dosage, unit, instruction, showInfoIcon, null, null);
    }
    public Medicine(String id, String name, int quantity, MedicineType type,
                    String dosage, String unit, String instruction, boolean showInfoIcon,
                    int[] quantityToTake, String[] medicineInstruction){
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.dosage = dosage;
        this.unit = unit;
        this.instruction = instruction;
        this.showInfoIcon = showInfoIcon;
        this.quantityToTake = quantityToTake;
        this.medicineInstruction = medicineInstruction;
    }

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

    public MedicineType getType() {
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
        INJECTION
    }
}
