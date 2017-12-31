package com.example.luis.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.luis.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Luis on 11/29/2017.
 */

public class InventoryProvider extends ContentProvider {
    /**
     * URI matcher code for the content URI for the engagement_ring table
     */
    private static final int RINGS = 100;

    /**
     * URI matcher code for the content URI for a single ring in the engagement_ring table
     */
    private static final int RING_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.luis.inventoryapp/engagement_ring" will map to the
        // integer code {@link #RINGS}.This URI is used to provide access to MULTIPLE rows
        // of the engagement_ring table.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_RINGS, RINGS);

        // The content URI of the form "content://com.example.luis.inventoryapp/engagement_ring/#" will map to the
        // integer code {@link #RINGS}.This URI is used to provide access to specific row
        // of the engagement_ring table.
        //
        // In this case, the "#" wildcard is used where "#" con be substituted for an integer.
        // For example, "content://com.example.luis.inventoryapp/engagement_ring/3" matches, but
        // "content://com.example.luis.inventoryapp/engagement_ring"(without a number at the end)doesn't match.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_RINGS + "/#", RING_ID);

    }
    /**
     * database helper object
     */
    private InventoryDbHelper mDbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {

        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection,
     * selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher cam match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case RINGS:
                // For the RINGS code, query the engagement_ring table directly with the given
                // projection, selection ,selection arguments, and sort order. The cursor
                // could contain multiple rows of the engagement_ring table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case RING_ID:
                // For the RING_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.luis.inventoryapp/engagement_ring/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the engagement_ring table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the  Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }
    /**
     * Returns the MIME type of data for the content URI
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RINGS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case RING_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RINGS:
                return insertRing(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a ring into the database with the given content values. Return the content URI
     * for that specific row in the database.
     */
    private Uri insertRing(Uri uri, ContentValues values){

        // check that the stock value is not 0.
        int stock = values.getAsInteger(InventoryEntry.COLUMN_STOCK_ID);
        if (stock == 0) {
            throw new IllegalArgumentException("Ring requires a stock id");
        }


        // If the {@link InventoryEntry#COLUMN_SUPPLIER} key is present,
        // check that the stock value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(InventoryEntry.COLUMN_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Ring requires a supplier name");
            }
        }

        // If the {@link InventoryEntry#COLUMN_DETAILS} key is present,
        // check that the stock value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_DETAILS)) {
            String detail = values.getAsString(InventoryEntry.COLUMN_DETAILS);
            if (detail == null) {
                throw new IllegalArgumentException("Ring requires details");
            }
        }

        // If the {@link InventoryEntry#COLUMN_QUANTITY} key is present,
        // check that the stock value is not 0.
        if (values.containsKey(InventoryEntry.COLUMN_QUANTITY)) {
            int quantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
            if (quantity == 0) {
                throw new IllegalArgumentException("Ring requires quantity");
            }
        }

        // If the {@link InventoryEntry#COLUMN_COST} key is present,
        // check that the stock value is not 0.
        if (values.containsKey(InventoryEntry.COLUMN_COST)) {
            int cost = values.getAsInteger(InventoryEntry.COLUMN_COST);
            if (cost == 0) {
                throw new IllegalArgumentException("Ring requires cost");
            }
        }

        // If the {@link InventoryEntry#COLUMN_PRICE} key is present,
        // check that the stock value is not 0.
        if (values.containsKey(InventoryEntry.COLUMN_PRICE)) {
            int price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
            if (price == 0) {
                throw new IllegalArgumentException("Ring requires price");
            }
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Inset the new ring with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        // if the id is -1, then the insertion failed, log an error and return null.
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the ring content URI
        getContext().getContentResolver().notifyChange(uri, null);
        
        // Return the new URI with the id of the newly inserted row appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RINGS:
                return updateRing(uri, contentValues, selection, selectionArgs);
            case RING_ID:
                // For the RING_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateRing(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    /**
     * Update engagement_ring in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more rings).
     * Return the number of rows that were successfully updated.
     */
    private int updateRing(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link InventoryEntry#COLUMN_STOCK_ID} key is present,
        // check that the stock value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_STOCK_ID)) {
            String stock = values.getAsString(InventoryEntry.COLUMN_STOCK_ID);
            if (stock == null) {
                throw new IllegalArgumentException("Ring requires a stock id");
            }
        }

        // If the {@link InventoryEntry#COLUMN_SUPPLIER} key is present,
        // check that the stock value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(InventoryEntry.COLUMN_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Ring requires a supplier name");
            }
        }

        // If the {@link InventoryEntry#COLUMN_DETAILS} key is present,
        // check that the stock value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_DETAILS)) {
            String detail = values.getAsString(InventoryEntry.COLUMN_DETAILS);
            if (detail == null) {
                throw new IllegalArgumentException("Ring requires details");
            }
        }

        // If the {@link InventoryEntry#COLUMN_QUANTITY} key is present,
        // check that the stock value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_QUANTITY)) {
            String quantity = values.getAsString(InventoryEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Ring requires quantity");
            }
        }

        // If the {@link InventoryEntry#COLUMN_COST} key is present,
        // check that the stock value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_COST)) {
            String cost = values.getAsString(InventoryEntry.COLUMN_COST);
            if (cost == null) {
                throw new IllegalArgumentException("Ring requires cost");
            }
        }

        // If the {@link InventoryEntry#COLUMN_PRICE} key is present,
        // check that the stock value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_PRICE)) {
            String price = values.getAsString(InventoryEntry.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Ring requires price");
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RINGS:
                // Delete all rows that match the selection and selection args
                // For  case RINGS:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RING_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;

    }


}
