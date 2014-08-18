package com.doctl.patientcare.main.om.followup;

import com.doctl.patientcare.main.om.BaseTask;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Administrator on 8/9/2014.
 */
public class FollowupTask extends BaseTask {
    @SerializedName("data")
    private FollowupData payload;

    public FollowupData getPayload() {
        return payload;
    }

    public class FollowupData{
        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("multipleChoice")
        private boolean multipleChoice;

        @SerializedName("options")
        private ArrayList<String> options;

        @SerializedName("selected")
        private ArrayList<Integer> selected;

        @SerializedName("getComments")
        private boolean getComments;

        @SerializedName("comment")
        private String comment;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public boolean isMultipleChoice() {
            return multipleChoice;
        }

        public ArrayList<String> getOptions() {
            return options;
        }

        public ArrayList<Integer> getSelected() {
            return selected;
        }

        public void setSelected(ArrayList<Integer> selected) {
            this.selected = selected;
        }

        public boolean isGetComments() {
            return getComments;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
