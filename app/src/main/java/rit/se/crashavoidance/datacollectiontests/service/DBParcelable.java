package rit.se.crashavoidance.datacollectiontests.service;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private String step; // Step the run is performed for
    private long startTime; // Start time of step

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    private long endTime; // End time of step

    private DBParcelable(String step, long startTime, long endTime){
        this.step = step;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Object is receive through longent as String array so this will
     * create the object from that String array
     * @param in
     */
    public DBParcelable(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        this.step = data[0];
        this.startTime = Long.parseLong(data[1]);
        this.endTime = Long.parseLong(data[2]);
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
     * Write the object to a String array so it can be sent through an longent
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.step
                ,Long.toString(this.startTime)
                ,Long.toString(this.endTime)
        });

    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("STEP: " + step +"\n").append(startTime).append("\n").append(endTime);
        return stringBuilder.toString();
    }

    public static class Builder {

        private String step;
        private long startTime;
        private long endTime;

        public Builder(String stepName) {
            this.step = stepName;
        }

        public void start() {
            this.startTime = new Date().getTime();
        }

        public void end() {
            this.endTime = new Date().getTime();
        }

        public DBParcelable build() {
            return new DBParcelable(this.step, this.startTime, this.endTime);
        }
    }
}
