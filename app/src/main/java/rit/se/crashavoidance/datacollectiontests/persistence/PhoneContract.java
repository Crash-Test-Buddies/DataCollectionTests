package rit.se.crashavoidance.datacollectiontests.persistence;

import android.provider.BaseColumns;

/**
 * Created by Chris on 6/12/2016.
 */
public class PhoneContract {
    public PhoneContract(){

    }
    public static abstract class PhoneEntry implements BaseColumns{
        public static final String TABLE_NAME = "Phone";
        public static final String COLUMN_NAME_DEVICE_ADDRESS = "device_address";
        public static final String COLUMN_NAME_OS = "operating_system";
        public static final String COLUMN_NAME_BRAND = "brand";
        public static final String COLUMN_NAME_MANUFACTURER = "manufacturer";
        public static final String COLUMN_NAME_MODEL = "model";
        public static final String COLUMN_NAME_STATUS = "status";
    }
}
