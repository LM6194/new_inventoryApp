package com.example.luis.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
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
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Inset the new ring with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        // if the id is -1, then the insertion failed, log an error and return null.
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Return the new URI with the id of the newly inserted row appended at the end
        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
