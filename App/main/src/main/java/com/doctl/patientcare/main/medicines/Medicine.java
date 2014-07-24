package com.doctl.patientcare.main.medicines;

import com.doctl.patientcare.main.R;

/**
 * Created by Administrator on 6/12/2014.
 */
public class Medicine {

    private String name;
    private int quantity;
    private MedicineType type;
    private String unit;
    private int iconResourceId;
    private int quantityToTake[];
    private String medicineInstruction[];
    private String notes;

    public Medicine(String name, int quantity, MedicineType type, String unit){
        this(name, quantity, type, unit, R.drawable.pill_icon, null, null,"");
    }
    public Medicine(String name, int quantity,
                    MedicineType type, String unit,
                    int iconResourceId, int[] quantityToTake,
                    String[] medicineInstruction, String notes){
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.unit = unit;
        this.iconResourceId = iconResourceId;
        this.quantityToTake = quantityToTake;
        this.medicineInstruction = medicineInstruction;
        this.notes = notes;
    }

    //region getter
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public MedicineType getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public int[] getQuantityToTake() {
        return quantityToTake;
    }

    public String[] getMedicineInstruction() {
        return medicineInstruction;
    }

    public String getNotes() {
        return notes;
    }

    //endregion

    public enum MedicineType{
        CAPSULE,
        TABLET,
        INJECTION
    }
}
