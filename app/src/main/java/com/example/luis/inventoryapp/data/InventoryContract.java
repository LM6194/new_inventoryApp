package com.example.luis.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Luis on 11/24/2017.
 */

public final class InventoryContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.luis.inventoryapp";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.luis.inventoryapp/engagement_ring/ is a valid path for
     * looking at ring data. content://com.example.luis.inventoryapp/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_RINGS = "engagement_ring";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract(){}

    public static final class InventoryEntry implements BaseColumns{
        /** The content URI to access the ring data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RINGS);
        /**Nme of database table for rings*/
        public final static String TABLE_NAME = "engagement_ring";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of rings.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RINGS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single ring.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RINGS;

        /** unique ID number for the ring
         * type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        /**
         * Stock id
         * type: INTEGER
         */
        public final static String COLUMN_STOCK_ID = "stock_id";
        /**
         * name of supplier
         * type: TEXT
         */
        public final static String COLUMN_SUPPLIER = "supplier";
        /**
         * details of the ring
         * type: TEXT
         */
        public final static String COLUMN_DETAILS = "details";
        /**
         * how many rings
         * type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";
        /**
         * how much it cost the company
         * type: INTEGER
         */
        public final static String COLUMN_COST = "cost";
        /**
         * how much it will sell for
         * type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";


    }
}
