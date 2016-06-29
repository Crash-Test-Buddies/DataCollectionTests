package rit.se.crashavoidance.datacollectiontests.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chris on 6/19/2016.
 */
public class DBParcelable implements Parcelable {

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    private String step; // Step the run is performed for
    private int startTime; // Start time of step

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    private int endTime; // End time of step

    public DBParcelable(String step, int startTime, int endTime){
        this.step = step;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Object is receive through intent as String array so this will
     * create the object from that String array
     * @param in
     */
    public DBParcelable(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        this.step = data[0];
        this.startTime = Integer.parseInt(data[1]);
        this.endTime = Integer.parseInt(data[2]);
    }

    /**
     * Standard parcelable methods
     */
    public static final Creator<DBParcelable> CREATOR = new Creator<DBParcelable>() {
        @Override
        public DBParcelable createFromParcel(Parcel in) {
            return new DBParcelable(in);
        }

        @Override
        public DBParcelable[] newArray(int size) {
            return new DBParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write the object to a String array so it can be sent through an Intent
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.step
                ,Integer.toString(this.startTime)
                ,Integer.toString(this.endTime)
        });

    }
}
