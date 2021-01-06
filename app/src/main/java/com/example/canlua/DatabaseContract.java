package com.example.canlua;

import android.provider.BaseColumns;

public class DatabaseContract {

    private DatabaseContract(){}

    public static final class HistoryTable implements BaseColumns {
        public static final String TABLE_NAME = "HistoryTable";
        public static final String COLUMN_TENGIONG = "riceType";
        public static final String COLUMN_DONGIA = "pricePerKG";
        public static final String COLUMN_BAOBI = "bagWeight";
        public static final String COLUMN_TONGBAO = "bagSum";
        public static final String COLUMN_TONGKG = "weightSum";
        public static final String COLUMN_THANHTIEN = "totalCost";
        public static final String COLUMN_TIENCOC = "deposit";
        public static final String COLUMN_TIMESTAMP = "datecreate";
    }

    public static final class CustomerTable implements BaseColumns {
        public static final String TABLE_NAME = "CustomerTable";
        public static final String COLUMN_TENKH = "nameCustomer";
        public static final String COLUMN_SDT = "phoneNumer";
        public static final String COLUMN_TIMESTAMP = "datetime";
    }
}
