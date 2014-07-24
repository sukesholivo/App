package com.doctl.patientcare.main.om;

import java.util.Date;

/**
 * Created by Administrator on 6/14/2014.
 */
public abstract class BaseTasks {
    public static final String TAG_TASK_ID = "cardId";
    public static final String TAG_TASK_ETA = "eta";
    public static final String TAG_TASK_CREATED = "createdOn";
    public static final String TAG_TASK_MODIFIED = "modifiedOn";
    public static final String TAG_TASK_SOURCE = "source";
    public static final String TAG_TASK_STATE = "state";
    public static final String TAG_TASK_TYPE = "type";
    public static final String TAG_TASK_POINTS = "points";

    protected String CardId;
    protected Date ETA;
    protected Date CreatedOn;
    protected Date ModifiedOn;
    protected int Source;
    protected CardState State;
    protected CardType Type;
    protected String templateIdentifier;
    protected int Points;

    public String getCardId() {
        return CardId;
    }

    public Date getETA() {
        return ETA;
    }

    public Date getCreatedOn() {
        return CreatedOn;
    }

    public Date getModifiedOn() {
        return ModifiedOn;
    }

    public int getSource() {
        return Source;
    }

    public CardState getState() {
        return State;
    }

    public CardType getType() {
        return Type;
    }

    public String getTemplateIdentifier() {
        return templateIdentifier;
    }

    public int getPoints() {
        return Points;
    }

    public enum CardType{
        MEDICINE,
        VITAL,
        EDUCATION,
        WALK
    }

    public enum CardState {
        ACTIVE,
        DONE
    }
}
