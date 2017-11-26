package com.example.luis.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Luis on 11/24/2017.
 */

public final class InventoryContract {

    private InventoryContract(){}

    public static final class InventoryEntry implements BaseColumns{

        public final static String TABLE_NAME = "engagement_ring";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_STOCK_ID = "stock_id";
        public final static String COLUMN_SUPPLIER = "supplier";
        public final static String COLUMN_DETAILS = "details";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_COST = "cost";
        public final static String COLUMN_PRICE = "price";


    }
}
