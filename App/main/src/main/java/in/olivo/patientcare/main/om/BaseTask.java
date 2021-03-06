package in.olivo.patientcare.main.om;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 6/14/2014.
 */
public abstract class BaseTask {
    @SerializedName("cardId")
    private String CardId;

    @SerializedName("etaType")
    private TypeOfEta EtaType;

    @SerializedName("eta")
    private Date ETA;

    @SerializedName("createdOn")
    private Date CreatedOn;

    @SerializedName("modifiedOn")
    private Date ModifiedOn;

    @SerializedName("source")
    private UserProfile Source;

    @SerializedName("influencers")
    private List<UserProfile> Influencer;

    @SerializedName("state")
    private CardState State;

    @SerializedName("type")
    private CardType Type;

    @SerializedName("points")
    private int Points;

    public String getCardId() {
        return CardId;
    }

    public TypeOfEta getEtaType() {
        return EtaType;
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

    public UserProfile getSource() {
        return Source;
    }

    public List<UserProfile> getInfluencer() {
        return Influencer;
    }

    public CardState getState() {
        return State;
    }

    public void setState(CardState state) {
        State = state;
    }

    public CardType getType() {
        return Type;
    }

    public int getPoints() {
        return Points;
    }

    public enum CardType {
        @SerializedName("Medicine")
        MEDICINE,
        @SerializedName("Vital")
        VITAL,
        @SerializedName("Education")
        EDUCATION,
        @SerializedName("Walk")
        WALK,
        @SerializedName("Followup")
        FOLLOWUP,
        @SerializedName("SimpleReminder")
        SIMPLEREMINDER,
        @SerializedName("GenericReminder")
        GENERICREMINDER,
        @SerializedName("Recommendation")
        RECOMMENDATION,
        @SerializedName("Objective")
        OBJECTIVE,
        DEFAULT;

        public static CardType lookup(String type) {
            try {
                return CardType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return DEFAULT;
            }
        }
    }

    public enum CardState {
        UNSEEN,
        SEEN,
        DMSD,
        DONE,
        SNOOZED
    }

    public enum TypeOfEta {
        PRECISE,
        DAYLONG
    }
}
