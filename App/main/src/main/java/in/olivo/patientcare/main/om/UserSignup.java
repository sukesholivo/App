package in.olivo.patientcare.main.om;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sukesh Cherlo on 13-10-2016.
 */
public class UserSignup implements Parcelable {

    public UserSignup(String name,  String mobile, String role, String email,String extra ){
        this.name= name;


        this.mobile = mobile;
        this.role = role;
        this.email = email;



    }

    public UserSignup() {
    }

    public UserSignup(String name,String role, String email,String password ) {


        this.name = name;

        this.role = role;
        this.email = email;
        this.password = password;

    }


    @SerializedName("name")
    private String name;

    @SerializedName("mobile")
    private String mobile;


    @SerializedName("role")
    private String role;

    @SerializedName("email")
    private String email;

    @SerializedName("password")

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public static Creator<UserSignup> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "UserSignup{" +
                "name='" + name + '\'' +
                ", phone_number='" + mobile + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public JSONObject getDataToPatch() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("name", this.getName());
        data.put("phone_number", this.getMobile());
        data.put("email", this.getEmail());
        data.put("role", this.getRole());


        return data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {


        parcel.writeString(this.name);


        parcel.writeString(this.mobile);
        parcel.writeString(this.email);
        parcel.writeString(this.role);



    }
    protected UserSignup(Parcel in) {

        this.name = in.readString();

        this.mobile = in.readString();
        this.email = in.readString();
        this.role = in.readString();


    }
    public static final Parcelable.Creator<UserSignup> CREATOR = new Parcelable.Creator<UserSignup>() {
        public UserSignup createFromParcel(Parcel source) {
            return new UserSignup(source);
        }

        public UserSignup[] newArray(int size) {
            return new UserSignup[size];
        }
    };
}
