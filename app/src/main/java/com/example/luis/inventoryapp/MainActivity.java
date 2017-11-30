package com.example.luis.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.luis.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.luis.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;

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

        mDbHelper = new InventoryDbHelper(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the engagement_ring database table.
     */
    private void displayDatabaseInfo() {
        //Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_SUPPLIER,
                InventoryEntry.COLUMN_DETAILS,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_COST,
                InventoryEntry.COLUMN_PRICE
        };
        //Perform a query on the provider using the ContentResolver.
        //Use the {@link InventoryEntry#CONTENT_URI}to access the ring data
        Cursor cursor = getContentResolver().query(
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);


        TextView displayView = (TextView) findViewById(R.id.text_view_inventory);
        try {
            // Create a header in the Text View that looks like this;
            //The dining table contain <number of rows in Cursor>.
            // _id - supplier
            //
            //In the while loop below, iterate through the rows of the cursor
            //and display the information from each column in this order.
            displayView.setText("The Engagement Ring table contains " + cursor.getCount() + " rings.\n\n");
            displayView.append(InventoryEntry._ID + " - " + InventoryEntry.COLUMN_SUPPLIER + " - " + InventoryEntry.COLUMN_QUANTITY + "\n");

            // figure out  the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER);
            int detailsColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_DETAILS);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int costColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_COST);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);

            // Iterate through all the returned rows of the cursor
            while (cursor.moveToNext()){
                // use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentDetails = cursor.getString(detailsColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentCost = cursor.getInt(costColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);

                displayView.append("\n" + currentID + " - " + currentSupplier + " - " +
                currentQuantity );
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
