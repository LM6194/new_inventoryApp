package com.example.luis.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.luis.inventoryapp.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int RING_LOADER = 0;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open ItemEditorActivity
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ItemEditorActivity.class);
                startActivity(intent);
            }
        });

        //Find the ListView which will be populated with the ring data
        ListView ringListView = (ListView) findViewById(R.id.list);

        //Setup an Adapter to create a list item for each row of ring data in the Cursor.
        //There is not ring data yet (until the loader finished) so pass in null for the Cursor.
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        ringListView.setAdapter(mCursorAdapter);

        // kick off the loader
        getLoaderManager().initLoader(RING_LOADER, null,this);
    }
    private void insertRing(){

        // Create a ContentValues object, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_STOCK_ID, 9780);
        values.put(InventoryEntry.COLUMN_SUPPLIER, "Pandora");
        values.put(InventoryEntry.COLUMN_DETAILS, "1.5 ct. round diamond solitary ring");
        values.put(InventoryEntry.COLUMN_QUANTITY, 2);
        values.put(InventoryEntry.COLUMN_COST, 4500);
        values.put(InventoryEntry.COLUMN_PRICE, 11250);

        // Receive the new content uri that will allow us to access data in the future
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertRing();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_SUPPLIER,
                InventoryEntry.COLUMN_DETAILS,
        };
        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, //Parent activity context
                InventoryEntry.CONTENT_URI,  //Provider content URI to query
                projection,                  //Columns to include in the resulting Cursor
                null,                //No selection clause
                null,             //No selection arguments
                null);              //default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link InventoryCursorAdapter} with this new cursor containing updated ring data
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
      // Callback called when data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }
}
